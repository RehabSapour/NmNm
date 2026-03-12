package com.example.nmnm.design
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.nmnm.Api.Resource
import com.example.nmnm.VM.LoginViewModel

//@Preview(showSystemUi = true, showBackground = true)
@Composable
fun NewPasswordScreen(
    navController: NavHostController
    , email: String,
    token: String,
    loginViewModel: LoginViewModel = viewModel()
 ) {
    val primaryGreen = Color(0xFF106B61)
    var newPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val resetPassState by loginViewModel.resetPasswordState.collectAsStateWithLifecycle()
    Log.d("trace", "NewPasswordScreen:$token ")
    val context = LocalContext.current
    LaunchedEffect(resetPassState) {
        when (resetPassState) {
            is Resource.Success -> {
                Toast.makeText(context, "Password changed Successfully! 📧", Toast.LENGTH_LONG).show()
                navController.navigate("login")
            }
            is Resource.Error -> {
                Toast.makeText(context, (resetPassState as Resource.Error).message, Toast.LENGTH_LONG).show()
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
        // العنوان الرئيسي
        Text(
            text = "Reset Password",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = primaryGreen
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Enter a new password",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 10.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // حقل إدخال الإيميل (استخدمت الـ CustomTextField بتاعك)
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
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
        Spacer(modifier = Modifier.height(30.dp))

        // زر الـ Continue
        Button(
            onClick = {
                if (newPassword.isNotEmpty()) {
                     loginViewModel.resetPassword(
                         email,
                         token,
                         newPassword
                     )
                } else {
                    Toast.makeText(context, "Please enter Your Password", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
        ) {
            Text(
                text = "CONTINUE",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

    }
}