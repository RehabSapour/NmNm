package com.example.nmnm.design

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.nmnm.Api.Resource
import com.example.nmnm.R
import com.example.nmnm.VM.SettingsViewModel
import com.example.nmnm.cach.TokenManager
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = viewModel()
) {
    val state = viewModel.usersState.value
    val context = LocalContext.current
    val updateState = viewModel.updateState.value
    val scope = rememberCoroutineScope()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val currentName = (viewModel.usersState.value.data)?.fullName ?: ""
            viewModel.updateProfile(context, currentName, it)
           // viewModel.updateProfileImage(it)
        }
    }
// يمكنك إظهار Loading Indicator خفيف أثناء التحديث
    if (updateState is Resource.Loading) {
        CircularProgressIndicator(modifier = Modifier.size(24.dp))
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F3F3)) // الخلفية الرمادية الفاتحة
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (state) {
            is Resource.Loading -> CircularProgressIndicator(modifier = Modifier.padding(top = 50.dp))
            is Resource.Success -> {
                val user = state.data!!

                // 1. الصورة الشخصية مع زر التعديل
                Spacer(modifier = Modifier.height(40.dp))
                Box(contentAlignment = Alignment.BottomCenter) {
                    AsyncImage(
                        model = user.profileImageUrl ?: R.drawable.avatar,
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFF4A696A), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                TextButton(onClick = { imagePicker.launch("image/*") }) {
                    Text("Edit", color = Color(0xFF00897B), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(30.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // صف الاسم
                        InfoRow(
                            icon = Icons.Default.Person,
                            label = "Name",
                            value = user.fullName,
                            onEditClick = { /* افتحي Dialog لتعديل الاسم */ }
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                        // صف الإيميل
                        InfoRow(
                            icon = Icons.Default.Email,
                            label = "Email",
                            value = user.email,
                            isEditable = false
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // 3. زر Logout
                Button(
                    onClick = {
                        scope.launch {
                            // 1. مسح البيانات من الـ DataStore
                            TokenManager.clear()
                            // 2. الانتقال لصفحة الـ Login ومسح الـ BackStack
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true } // بيمسح كل الشاشات اللي فاتت
                                launchSingleTop = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            is Resource.Error -> Text("Error loading data", color = Color.Red)
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    isEditable: Boolean = true,
    onEditClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF4A696A),
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, color = Color.Gray, fontSize = 12.sp)
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }
        if (isEditable) {
            // يمكن إضافة أيقونة تعديل هنا إذا أردتِ
        }
    }
}