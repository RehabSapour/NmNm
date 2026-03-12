package com.example.nmnm.design

import android.net.Uri
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.nmnm.Api.Resource
import com.example.nmnm.VM.AuthViewModel

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val primaryGreen = Color(0xFF106B61)
    val lightGreen = Color(0xFFE8F5E9)

    val context = LocalContext.current

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val registerState by authViewModel.registerState.collectAsStateWithLifecycle()

    // React to state changes (show toast, navigate, etc.)
    LaunchedEffect(registerState) {
        when (val state = registerState) {
            is Resource.Success -> {
                Toast.makeText(context, "Registration Successful! 🎉", Toast.LENGTH_LONG).show()
                authViewModel.resetState()
                // TODO: navigate to HomeScreen or LoginScreen
                navController.navigate("login")
            }
            is Resource.Error -> {
                Toast.makeText(context, state.message ?: "Error occurred", Toast.LENGTH_LONG).show()
                authViewModel.resetState()
            }
            else -> Unit
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { imageUri = it }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = primaryGreen
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Profile Image
        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape)
                .background(lightGreen)
                .border(2.dp, primaryGreen, CircleShape)
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(45.dp),
                        tint = primaryGreen
                    )
                    Text("Add Photo", fontSize = 10.sp, color = primaryGreen)
                }
            }
        }

        Spacer(modifier = Modifier.height(35.dp))

        CustomTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = "Full Name",
            icon = Icons.Default.Person,
            primaryColor = primaryGreen
        )

        Spacer(modifier = Modifier.height(15.dp))

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
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = primaryGreen) },
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = primaryGreen
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryGreen,
                focusedLabelColor = primaryGreen,
                cursorColor = primaryGreen
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Sign Up Button with Loading indicator
        Button(
            onClick = {
                if (!isValidEmail(email)) {
                    Toast.makeText(context, "Enter a valid email", Toast.LENGTH_LONG).show()
                    return@Button
                }
                if (!isValidPassword(password)) {
                    Toast.makeText(
                        context,
                        "Password must contain capital, small, number and special character",
                        Toast.LENGTH_LONG
                    ).show()
                    return@Button
                }
                authViewModel.registerUser(
                    context = context,
                    fullName = fullName,
                    email = email,
                    password = password,
                    imageUri = imageUri
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
            enabled = registerState !is Resource.Loading
        ) {
            if (registerState is Resource.Loading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("SIGN UP", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text ="Already have an account? ",
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = "Sign In",
                color =primaryGreen ,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                   navController.navigate("login")
                }
            )
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    primaryColor: Color,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        leadingIcon = { Icon(icon, contentDescription = null, tint = primaryColor) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primaryColor,
            focusedLabelColor = primaryColor,
            cursorColor = primaryColor
        ),
        singleLine = true
    )
}

fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isValidPassword(password: String): Boolean {
    val passwordPattern =
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}$".toRegex()

    return password.matches(passwordPattern)
}