package com.example.nmnm.VM

import com.example.nmnm.Api.AuthRepository
import com.example.nmnm.Api.Resource
import com.example.nmnm.Models.RegisterResponse
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _registerState = MutableStateFlow<Resource<RegisterResponse>?>(null)
    val registerState: StateFlow<Resource<RegisterResponse>?> = _registerState

    fun registerUser(
        context: Context,
        fullName: String,
        email: String,
        password: String,
        imageUri: Uri?
    ) {
        // Basic validation before calling API
        if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
            _registerState.value = Resource.Error("Please fill all fields")
            return
        }

        viewModelScope.launch {
            _registerState.value = Resource.Loading()
            val result = repository.registerUser(
                context = context,
                fullName = fullName,
                email = email,
                password = password,
                imageUri = imageUri
            )
            _registerState.value = result
        }
    }

    fun resetState() {
        _registerState.value = null
    }
}