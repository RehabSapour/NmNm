package com.example.nmnm.Models

data class UserResponse(
    val id: String,
    val email: String,
    val fullName: String,
    val profileImageUrl: String?
)