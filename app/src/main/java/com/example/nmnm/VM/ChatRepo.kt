package com.example.nmnm.VM

// ChatRepository.kt
import android.util.Log
import com.example.nmnm.Api.AuthApiService
import com.example.nmnm.Models.MessageModel
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import io.reactivex.rxjava3.core.Single

class ChatRepository(private val token: String,private val apiService: AuthApiService) {

    private lateinit var hubConnection: HubConnection

    // بتبني الـ connection مع السيرفر
    fun buildConnection() {
        hubConnection = HubConnectionBuilder
            .create("http://nmnm.runasp.net/chatHub")
            .withAccessTokenProvider(
            Single.defer { Single.just(token) }
             )
            .build()
    }

    // بتفتحي الـ connection
    suspend fun connect() {
        try {
            hubConnection.start().blockingAwait()
            Log.e("SignalR", "Connected")
        } catch (e: Exception) {
            Log.e("SignalR", "Connection Error", e)
        }
    }

    // بتبعتي رسالة عن طريق SignalR
    fun sendMessage(receiverId: String, content: String) {
        val dto = mapOf(
            "receiverId" to receiverId,
            "content" to content,
            "messageType" to 0,
            "latitude" to 0,
            "longitude" to 0
        )
        hubConnection.send("SendMessage", dto)
    }

    // بتسمعي على الرسائل الجاية
    fun onReceiveMessage(callback: (MessageDto) -> Unit) {
        hubConnection.on("ReceiveMessage", { message ->
            callback(message)
        }, MessageDto::class.java)
    }

    fun disconnect() {
        if (::hubConnection.isInitialized && hubConnection.connectionState == HubConnectionState.CONNECTED) {
            hubConnection.stop()
        }
    }

    suspend fun getOldMessages(receiverId: String): List<MessageDto> {
        val response = apiService.getConversation("Bearer $token", receiverId)
        return if (response.isSuccessful) {
            Log.d("traceM", "getOldMessages:${response.body()?.size} ")
            response.body() ?: emptyList()
        } else {
            emptyList()
        }
    }

}

data class MessageDto(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val sentAt: String = ""
)