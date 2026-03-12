package com.example.nmnm.VM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nmnm.Api.AuthRepository
import com.example.nmnm.Api.Resource
import com.example.nmnm.Models.RegisterResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()
    // ─── Login State ──────────────────────────────────────────────
    private val _loginState = MutableStateFlow<Resource<RegisterResponse>?>(null)
    val loginState: StateFlow<Resource<RegisterResponse>?> = _loginState
    // ─── Forget password State ──────────────────────────────────────────────
    private val _forgetPasswordState = MutableStateFlow<Resource<String>>(Resource.Loading())
    val forgetPasswordState = _forgetPasswordState.asStateFlow()

    // ─── Verify Code State ─────────────────────────────
    private val _verifyCodeState = MutableStateFlow<Resource<String>>(Resource.Loading())
    val verifyCodeState = _verifyCodeState.asStateFlow()
    var resetToken: String? = null
    // ─── Login ────────────────────────────────────────────────────
    fun loginUser(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = Resource.Error("Please fill all fields")
            return
        }

        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            _loginState.value = repository.loginUser(email, password)
        }
    }


    // 3. حالة إعادة تعيين كلمة المرور (الخطوة الأخيرة)
    private val _resetPasswordState = MutableStateFlow<Resource<RegisterResponse>?>(null)
    val resetPasswordState = _resetPasswordState.asStateFlow()

    // ─── Forgot password ────────────────────────────────────────────────────
    fun sendForgetPassword(email: String) {
        if (email.isBlank()) {
            _forgetPasswordState.value = Resource.Error("Please enter your email")
            return
        }

        viewModelScope.launch {
            _forgetPasswordState.value = Resource.Loading()
            val result = repository.forgetPassword(email)
            _forgetPasswordState.value = result
        }
    }


    fun verifyResetCode(email: String, code: String) {
        if (email.isBlank() || code.isBlank()) {
            _verifyCodeState.value = Resource.Error("Email and code must be provided")
            return
        }
        viewModelScope.launch {
            _verifyCodeState.value = Resource.Loading()

            val result = repository.verifyResetCode(email, code)

            if (result is Resource.Success) {
                resetToken = result.data
            }
            _verifyCodeState.value = result
        }
    }

    fun resetVerifyCodeState() {
        _verifyCodeState.value = Resource.Loading()
    }

    fun resetState() {
        _forgetPasswordState.value = Resource.Loading() // أو حالة Idle
    }


    fun resetLoginState() { _loginState.value = null }

    fun resetPassword(email: String, token: String, newPassword: String) {
        viewModelScope.launch {
            _resetPasswordState.value = Resource.Loading()
            val result = repository.resetPassword(email, token, newPassword)
            _resetPasswordState.value = result
        }
    }
}