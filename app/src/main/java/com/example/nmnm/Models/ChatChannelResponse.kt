package com.example.nmnm.Models

data class ChatChannelDto(
    val userId: String,
    val email: String,
    val fullName: String,
    val profileImage: String?,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int
)

data class ChatItem(
    val id: String,
    val name: String,
    val email: String,
    val lastMessage: String,
    val time: String,
    val image: String?,
    var unreadCount: Int = 0
)