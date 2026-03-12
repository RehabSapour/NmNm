package com.example.nmnm.VM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nmnm.Api.RetrofitInstance
import com.example.nmnm.Models.ChatItem
import com.example.nmnm.cach.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChannelsViewModel : ViewModel() {
    private val _channels = MutableStateFlow<List<ChatItem>?>(null)
    val channels: StateFlow<List<ChatItem>?> = _channels

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading
    private var repository: ChatRepository? = null

    init {
        loadChannels()
    }

    fun loadChannels() {
        viewModelScope.launch {
            _isLoading.value = true
            // هنجيب التوكن من الـ TokenManager
            val token = TokenManager.getToken()
            if (token != null) {
                // بنعمل Instance مؤقت للـ Repository عشان الـ Channels
                val repo = ChatRepository(token, RetrofitInstance.authApi)
                _channels.value = repo.getChatChannels()
            }
            _isLoading.value = false
        }
    }
}