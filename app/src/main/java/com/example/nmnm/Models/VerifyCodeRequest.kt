package com.example.nmnm.Models

data class VerifyCodeRequest(
val email: String,
val code: String
)

data class ResetPasswordRequest(
    val email: String,
    val TempToken: String, // تأكدي من كتابة الحروف Case Sensitive زي الـ API بالظبط
    val password: String
)

