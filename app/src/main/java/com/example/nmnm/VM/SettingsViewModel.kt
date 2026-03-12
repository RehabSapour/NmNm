package com.example.nmnm.VM

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nmnm.Api.AuthRepository
import com.example.nmnm.Api.Resource
import com.example.nmnm.Models.UserResponse
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {
    private val _usersState = mutableStateOf<Resource<UserResponse>>(Resource.Loading())
    val usersState: State<Resource<UserResponse>> = _usersState

    private val _updateState = mutableStateOf<Resource<UserResponse>?>(null)
    val updateState:State<Resource<UserResponse>?> = _updateState

    init {
        fetchCurrentUser()
    }

    fun fetchCurrentUser() {
        viewModelScope.launch {
            // نضع الحالة Loading قبل بدء العملية
            _usersState.value = Resource.Loading()
            val result = repository.getCurrentUser()
            _usersState.value = result
        }
    }

    fun updateProfile(context: Context, newName: String, imageUri: Uri?) {
        viewModelScope.launch {
            _updateState.value = Resource.Loading()
            val result = repository.updateUser(context, newName, imageUri)
            _updateState.value = result

            if (result is Resource.Success) {
                fetchCurrentUser()
            }
        }
    }


}