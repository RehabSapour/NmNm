package com.example.nmnm.cach

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import android.util.Base64
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

object TokenManager {

    private val TOKEN_KEY = stringPreferencesKey("auth_token")
    private val USER_ID_KEY = stringPreferencesKey("user_id")
    lateinit var dataStore: androidx.datastore.core.DataStore<Preferences>

    fun init(context: Context) {
        dataStore = context.dataStore
    }
    fun isInitialized() = dataStore != null
    fun tokenFlow(): Flow<String?> {
        return dataStore.data.map { prefs ->
            prefs[TOKEN_KEY]
        }
    }
    // Flow للـ userId
    fun userIdFlow(): Flow<String?> {
        return dataStore.data.map { prefs ->
            prefs[USER_ID_KEY]
        }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }
    // حفظ الـ userId
    suspend fun saveUserId(id: String) {
        dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = id
        }
    }
    suspend fun getToken(): String? {
        return dataStore.data.map { it[TOKEN_KEY] }.firstOrNull()
    }

    suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
            prefs.remove(USER_ID_KEY)
        }
    }
}

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val token = runBlocking {
            TokenManager.tokenFlow().first()
        }

        val requestBuilder = chain.request().newBuilder()

        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        val response = chain.proceed(requestBuilder.build())

        if (response.code == 401) {
            runBlocking { TokenManager.clear() }
        }

        return response
    }
}



fun getIdFromJWT(token: String): String? {
    return try {
        val parts = token.split(".")
        if (parts.size < 2) return null

        val payload = parts[1]
        val decoded = String(Base64.decode(payload, Base64.URL_SAFE))
        val json = JSONObject(decoded)

        // حسب شكل الـ payload عندك
        json.getString("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier")
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}