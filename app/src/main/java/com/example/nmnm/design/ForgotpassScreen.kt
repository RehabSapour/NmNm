package com.example.nmnm.design
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nmnm.Api.Resource
import com.example.nmnm.VM.LoginViewModel

@Composable
fun ResetPasswordScreen(
    navController: NavController,
    // هتحتاج ViewModel خاص بالـ Reset أو تستخدم الـ AuthViewModel
    loginViewModel: LoginViewModel = viewModel()
) {
    val primaryGreen = Color(0xFF106B61)
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    val forgetState by loginViewModel.forgetPasswordState.collectAsStateWithLifecycle()

    LaunchedEffect(forgetState) {
        when (forgetState) {
            is Resource.Success -> {
                Toast.makeText(context, "Check your email for reset link! 📧", Toast.LENGTH_LONG).show()
                // يمكنك الانتقال لصفحة الـ Verification أو العودة للـ Login
                navController.navigate("VerificationCodeScreen/$email")
            }
            is Resource.Error -> {
                Toast.makeText(context, (forgetState as Resource.Error).message, Toast.LENGTH_LONG).show()
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

        // نص توضيحي للمستخدم
        Text(
            text = "Enter your email to receive a password reset link",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 10.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // حقل إدخال الإيميل (استخدمت الـ CustomTextField بتاعك)
        CustomTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email Address",
            icon = Icons.Default.Email,
            primaryColor = primaryGreen,
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(30.dp))

        // زر الـ Continue
        Button(
            onClick = {
                if (email.isNotEmpty()) {
                    // هنا تنادي الميثود من الـ ViewModel
                     loginViewModel.sendForgetPassword(email)
                } else {
                    Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
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

        Spacer(modifier = Modifier.height(25.dp))

        // نص الـ Back to Sign In في المنتصف
        Text(
            text = "Back to Sign In",
            color = primaryGreen,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .clickable {
                    navController.navigate("login")
                }
                .padding(10.dp)
        )
    }
}