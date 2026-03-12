package com.example.nmnm.VM

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nmnm.Api.AuthRepository
import com.example.nmnm.Api.Resource
import com.example.nmnm.Models.UserResponse
import kotlinx.coroutines.launch

class FriendsViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    // حالة اليوزرز
    private val _usersState = mutableStateOf<Resource<List<UserResponse>>>(Resource.Loading())
    val usersState: State<Resource<List<UserResponse>>> = _usersState

    init {
        getUsers()
    }

    fun getUsers() {
        viewModelScope.launch {
            _usersState.value = Resource.Loading()
            val result = repository.getAllUsers()
            _usersState.value = result
        }
    }
}