package com.example.nmnm.Models

data class RegisterResponse(
    val fullName: String,
    val email: String,
    val token: String,
    val profileImageUrl: String?
)