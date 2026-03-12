package com.example.nmnm.navGraph

sealed class BottomBarScreen(val route: String, val title: String) {
    object Friends : BottomBarScreen("friends", "Friends")
    object Chats : BottomBarScreen("chats", "Chats")
    object Settings : BottomBarScreen("settings", "Settings")
}