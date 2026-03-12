package com.example.nmnm.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.example.nmnm.Models.UserResponse
import com.example.nmnm.design.ChatScreen
import com.example.nmnm.design.ChatsScreen
import com.example.nmnm.design.FriendsScreen
import com.example.nmnm.design.HomeScreen
import com.example.nmnm.design.LoginScreen
import com.example.nmnm.design.NewPasswordScreen
import com.example.nmnm.design.ResetPasswordScreen
import com.example.nmnm.design.SettingsScreen
import com.example.nmnm.design.SignUpScreen
import com.example.nmnm.design.VerificationCodeScreen
import com.example.nmnm.navGraph.BottomBarScreen
import com.google.gson.Gson


@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "signup",
        modifier = modifier
    ) {
        composable("signup") { SignUpScreen(navController) }
        composable("login") {
            LoginScreen(navController)
        }

        composable(BottomBarScreen.Friends.route) {
            FriendsScreen(navController = navController)
        }
        composable(BottomBarScreen.Chats.route) {
            ChatsScreen(navController = navController)
        }
        composable(BottomBarScreen.Settings.route) {
            SettingsScreen(navController)
        }

        composable("forget_password") { ResetPasswordScreen(navController) }
        composable("VerificationCodeScreen/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerificationCodeScreen(navController, email)
        }
        composable("newPassword/{email}/{resetToken}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val token = backStackEntry.arguments?.getString("resetToken") ?: ""
            NewPasswordScreen(navController, email, token)
        }
        composable("chat/{userJson}") { backStackEntry ->
            val userJson = backStackEntry.arguments?.getString("userJson")
            val user = Gson().fromJson(userJson, UserResponse::class.java) // تحويل الـ String لـ Object

            ChatScreen( user){
                navController.popBackStack()
            }
        }

    }
}