package com.example.nmnm.design
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nmnm.Api.Resource
import com.example.nmnm.VM.LoginViewModel

//@Preview(showSystemUi = true, showBackground = true)
@Composable
fun VerificationCodeScreen(
    navController: NavController,
    email:String,
    loginViewModel: LoginViewModel = viewModel()
) {
    // لونك الأساسي من كود الـ Login
    val primaryGreen = Color(0xFF106B61)
    val VerifyState by loginViewModel.verifyCodeState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    LaunchedEffect(VerifyState) {
        when (VerifyState) {
            is Resource.Success -> {
                Toast.makeText(context, "Now You Can Change Your Password! 📧", Toast.LENGTH_LONG).show()
                Log.d("temp", "VerificationCodeScreen: ${loginViewModel.resetToken}")
                navController.navigate("newPassword/${email}/${loginViewModel.resetToken.toString()}")
            }
            is Resource.Error -> {
                Toast.makeText(context, (VerifyState as Resource.Error).message, Toast.LENGTH_LONG).show()
            }
            else -> Unit
        }
    }
    // حالات لحفظ كل رقم في المربع الخاص به (افترضت كود من 4 أرقام)
    val (code1, setCode1) = remember { mutableStateOf("") }
    val (code2, setCode2) = remember { mutableStateOf("") }
    val (code3, setCode3) = remember { mutableStateOf("") }
    val (code4, setCode4) = remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // خلفية بيضاء ليتناسق مع الـ Login
            .padding(horizontal = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // العنوان بنفس تنسيق صفحة الـ Login
        Text(
            text = "Enter Verification Code",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = primaryGreen, // اللون الأخضر الأساسي
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))

        // صف يحتوي على مربعات إدخال الكود
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // مربع الإدخال الأول
            CodeInputField(value = code1, onValueChange = setCode1, primaryColor = primaryGreen)
            // مربع الإدخال الثاني
            CodeInputField(value = code2, onValueChange = setCode2, primaryColor = primaryGreen)
            // مربع الإدخال الثالث
            CodeInputField(value = code3, onValueChange = setCode3, primaryColor = primaryGreen)
            // مربع الإدخال الرابع
            CodeInputField(value = code4, onValueChange = setCode4, primaryColor = primaryGreen)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // نص Resend بنفس تنسيق الـ Sign Up
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "If you don't receive a code? ",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Text(
                text = "Resend",
                color = primaryGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    // منطق إعادة إرسال الكود
                }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // زر الـ Send بنفس تصميم زر הـ Sign In
        Button(
            onClick = {
                val completeCode = "$code1$code2$code3$code4"
                if (completeCode.length == 4) {
                    loginViewModel.verifyResetCode(email,completeCode)
                    // navController.navigate("home")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
        ) {
            Text(
                text = "SEND",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// عنصر الـ Composable الفرعي لمربع إدخال الكود الفردي
@Composable
fun CodeInputField(
    value: String,
    onValueChange: (String) -> Unit,
    primaryColor: Color
) {
    // نستخدم OutlinedTextField لتخصيص الحواف مثل صورتك
    OutlinedTextField(
        value = value,
        onValueChange = {
            // نقبل رقماً واحداً فقط
            if (it.length <= 1 && it.all { char -> char.isDigit() }) {
                onValueChange(it)
            }
        },
        modifier = Modifier
            .size(65.dp), // حجم مربع الإدخال
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center, // تمركز الرقم داخل المربع
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(12.dp), // نفس درجة الانحناء في الديزاين
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primaryColor, // عند التحديد، يصبح الحاف أخضر
            unfocusedBorderColor = Color.LightGray, // الحاف غير المحدد رمادي فاتح
            focusedContainerColor = Color(0x11106B61), // خلفية خضراء باهتة عند التحديد (اختياري)
            cursorColor = primaryColor
        )
    )
}