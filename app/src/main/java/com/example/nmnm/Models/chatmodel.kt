package com.example.nmnm.Models

data class ChatUser(
    val id: String,
    val name: String,
    val imageRes: Int,
    val isOnline: Boolean
)

data class MessageModel(
    val id: String,
    val text: String,
    val time: String,
    val isSender: Boolean
)
val fakeMessages = listOf(
    MessageModel(
        id = "1",
        text = "Hello Aya, your session request has been accepted. I look forward to working with you and helping you achieve your goals.",
        time = "12:15 PM",
        isSender = false
    ),
    MessageModel(
        id = "2",
        text = "Thank you for accepting my request. I am excited to start.",
        time = "12:16 PM",
        isSender = true
    ),
    MessageModel(
        id = "3",
        text = "Before our session, can you tell me your current level in Android development?",
        time = "12:17 PM",
        isSender = false
    ),
    MessageModel(
        id = "4",
        text = "I have completed the basics and built small projects, but I need help with MVVM and clean architecture.",
        time = "12:16 PM",
        isSender = true
    ),
    MessageModel(
        id = "3",
        text = "That is a good starting point. In our session, we will focus on MVVM concepts and apply them in a practical example.",
        time = "12:17 PM",
        isSender = false
    ),
)