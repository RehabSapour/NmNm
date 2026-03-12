package com.example.nmnm.VM

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nmnm.Api.RetrofitInstance
import com.example.nmnm.Models.MessageModel
import com.example.nmnm.cach.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


data class UserAuthState(
    val token: String? = null,
    val userId: String? = null,
    val isLoading: Boolean = true
)
class ChatViewModel : ViewModel() {
    private var currentReceiverId: String = ""
    private val _messages = mutableStateListOf<MessageModel>()
    val messages: List<MessageModel> = _messages
    private var repository: ChatRepository? = null

    val userState: StateFlow<UserAuthState> = combine(
        TokenManager.tokenFlow(),
        TokenManager.userIdFlow()
    ) { token, id ->
        UserAuthState(token = token, userId = id, isLoading = false)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserAuthState()
    )

    // أول ما الـ ID يوصل من الشاشة، بنحاول نشغل الـ Repository
    fun initChat(receiverId: String) {
        if (currentReceiverId == receiverId) return // عشان ميعملش ريستارت لو ندينا عليها كذا مرة بنفس الـ ID
        currentReceiverId = receiverId

        viewModelScope.launch {
            // بنستنى لحد ما الـ UserState تتملي (التوكن يوصل)
            userState.collect { state ->
                if (state.token != null && repository == null) {
                    setupRepository(state.token, state.userId ?: "", receiverId)
                }
            }
        }
    }

    private fun setupRepository(token: String, currentUserId: String, receiverId: String) {
        val apiService = RetrofitInstance.authApi
        repository = ChatRepository(token, apiService).apply {
            buildConnection()

            onReceiveMessage { dto ->
                // مهم: الـ SignalR بيرجع الداتا في Thread خلفي، لازم نضيفها للـ List في الـ Main Thread
                if (dto.senderId == receiverId || dto.senderId == currentUserId) {
                    _messages.add(mapDtoToModel(dto, currentUserId))
                    Log.d("ChatVM", "Message added: ${dto.content}")
                } else {
                    // الرسالة دي مش تبع المحادثة اللي مفتوحة دلوقتي
                    Log.d("ChatVM", "Message ignored. From: ${dto.senderId}, Current Chat with: $receiverId")
                }
            }

            viewModelScope.launch {
                connect()
                // تحميل الرسايل القديمة
                val oldMsgs = getOldMessages(receiverId)
                _messages.clear()
                _messages.addAll(oldMsgs.map { mapDtoToModel(it, currentUserId) })
            }
        }
    }
    private fun mapDtoToModel(dto: MessageDto, currentUserId: String) = MessageModel(
        id = dto.id,
        text = dto.content,
        time = dto.sentAt,
        isSender = dto.senderId == currentUserId
    )

    fun sendMessage(receiverId: String, text: String) {
        repository?.sendMessage(receiverId, text)
    }

    override fun onCleared() {
        super.onCleared()
        repository?.disconnect()
    }
}
