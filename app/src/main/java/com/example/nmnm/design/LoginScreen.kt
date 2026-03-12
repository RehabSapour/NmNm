package com.example.nmnm.design

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nmnm.Api.Resource
import com.example.nmnm.VM.AuthViewModel
import com.example.nmnm.VM.LoginViewModel
import com.example.nmnm.navGraph.BottomBarScreen

@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = viewModel()
) {
    val primaryGreen = Color(0xFF106B61)

    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val loginState by loginViewModel.loginState.collectAsStateWithLifecycle()

    // React to login state changes
    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is Resource.Success -> {

                Toast.makeText(context, "Welcome back! 👋", Toast.LENGTH_LONG).show()
                loginViewModel.resetLoginState()
                navController.navigate(BottomBarScreen.Friends.route) {
                    popUpTo("login") { inclusive = true } // بيمسح صفحة اللوجن من الذاكرة تماماً
                }
            }
            is Resource.Error -> {
                Toast.makeText(context, state.message ?: "Login failed", Toast.LENGTH_LONG).show()
                loginViewModel.resetLoginState()
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome Back Ya Man",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = primaryGreen
        )

        Spacer(modifier = Modifier.height(30.dp))

        CustomTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email Address",
            icon = Icons.Default.Email,
            primaryColor = primaryGreen,
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = primaryGreen)
            },
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = primaryGreen
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryGreen,
                focusedLabelColor = primaryGreen,
                cursorColor = primaryGreen
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        ForgotPassword{
           navController.navigate("forget_password")
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                loginViewModel.loginUser(email, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
            enabled = loginState !is Resource.Loading
        ) {
            if (loginState is Resource.Loading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("SIGN IN", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text ="Don`t have an account? ",
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = "Sign Up",
                color =primaryGreen ,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    navController.navigate("signup")
                }
            )
        }
    }
}


@Composable
fun ForgotPassword(
    onForgotPasswordClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = "forgot password",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color =Color(0xFF106B61) ,
            modifier = Modifier.clickable {
                onForgotPasswordClick()
            }
        )
    }
}
