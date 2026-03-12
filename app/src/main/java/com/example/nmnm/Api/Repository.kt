package com.example.nmnm.Api

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.nmnm.Models.LoginRequest
import com.example.nmnm.Models.RegisterResponse
import com.example.nmnm.Models.ResetPasswordRequest
import com.example.nmnm.Models.UserResponse
import com.example.nmnm.Models.VerifyCodeRequest
import com.example.nmnm.cach.TokenManager
import com.example.nmnm.cach.getIdFromJWT
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}

class AuthRepository {
    private val api = RetrofitInstance.authApi

    suspend fun registerUser(
        context: Context,
        fullName: String,
        email: String,
        password: String,
        imageUri: Uri?
    ): Resource<RegisterResponse> {
        return try {
            val fullNameBody = fullName.toRequestBody("text/plain".toMediaTypeOrNull())
            val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
            val passwordBody = password.toRequestBody("text/plain".toMediaTypeOrNull())

            // Convert Uri to MultipartBody.Part if image is selected
            val imagePart: MultipartBody.Part? = imageUri?.let { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = File(context.cacheDir, "profile_image.jpg")
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("ProfileImage", file.name, requestBody)
            }

            val response = api.registerUser(
                fullName = fullNameBody,
                email = emailBody,
                password = passwordBody,
                profileImage = imagePart
            )

            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Registration failed"
                Resource.Error(errorMsg)
            }

        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    suspend fun loginUser(
        email: String,
        password: String
    ): Resource<RegisterResponse> {
        return try {
            val response = api.loginUser(LoginRequest(email, password))

            if (response.isSuccessful) {
                val body = response.body()!!

                body.token.let {
                    TokenManager.saveToken(it)
                    Log.d("token", "loginUser:$it ")
                    val userId = getIdFromJWT(it)
                   // Log.d("id", "User ID from JWT: $userId")
                    userId?.let { id ->
                        TokenManager.saveUserId(id) // هنا بنخزن الـ id
                    }
                }

                Resource.Success(body)

            } else {
                Resource.Error(response.errorBody()?.string() ?: "Login failed")
            }

        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    suspend fun forgetPassword(email: String): Resource<String> {
        return try {

            val response = api.forgetPassword(email)
            if (response.isSuccessful) {
                val message = response.body()?.toString() ?: "Reset link sent successfully"
                Resource.Success(message)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Failed to send reset link"
                Log.d("trace", "forgetPassword: $errorMsg")
                Resource.Error(errorMsg)
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    suspend fun verifyResetCode(email: String, code: String): Resource<String> {
        return try {
            val response = api.verifyResetCode(
                VerifyCodeRequest(email, code)
            )

            if (response.isSuccessful) {
                val resetToken = response.body()?.string() ?: ""
                Resource.Success(resetToken)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Verification failed"
                Log.d("trace", "verifyResetCode: $errorMsg")
                Resource.Error(errorMsg)
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    suspend fun resetPassword(
        email: String,
        token: String,
        newPassword: String
    ): Resource<RegisterResponse> {

        return try {

            val request = ResetPasswordRequest(
                email = email,
                TempToken = token,
                password = newPassword
            )

            val response = api.resetPassword(request)

            if (response.isSuccessful) {

                val body = response.body()

                if (body != null) {
                    Resource.Success(body)
                } else {
                    Resource.Error("Empty response from server")
                }

            } else {

                val errorMsg = response.errorBody()?.string() ?: "Failed to reset password"
                Log.d("trace", "ResetPassword: $errorMsg")
                Resource.Error(errorMsg)

            }

        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }


    suspend fun getAllUsers(): Resource<List<UserResponse>> {
        return try {
            val response = api.getAllUsers() // تأكدي أن هذه الدالة معرفة في الـ Api Interface
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    suspend fun getCurrentUser(): Resource<UserResponse> {
        return try {
            // بنجيب التوكن من الدالة اللي لسه عاملينها
            val token = TokenManager.getToken()

            if (token.isNullOrBlank()) {
                return Resource.Error("Unauthorized: No token found")
            }

            // بنبعت التوكن في الـ Header
            val response = api.getCurrentUser("Bearer $token")

            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Failed to get user data"
                Resource.Error(errorMsg)
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    suspend fun updateUser(
        context: Context,
        newName: String,
        imageUri: Uri?
    ): Resource<UserResponse> {
        return try {
            val token = TokenManager.getToken() ?: return Resource.Error("No token found")
            val nameBody = newName.toRequestBody("text/plain".toMediaTypeOrNull())
            var imagePart: MultipartBody.Part? = null
            imageUri?.let { uri ->
                val file = File(context.cacheDir, "update_profile.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(file).use { output -> input.copyTo(output) }
                }
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                imagePart = MultipartBody.Part.createFormData("ProfileImage", file.name, requestBody)
            }

            val response = api.updateUser("Bearer $token", nameBody, imagePart)

            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Update failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error occurred")
        }
    }
}


