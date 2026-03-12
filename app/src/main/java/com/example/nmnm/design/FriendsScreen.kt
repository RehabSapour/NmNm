package com.example.nmnm.design

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.nmnm.Api.Resource
import com.example.nmnm.Models.UserResponse
import com.example.nmnm.R
import com.example.nmnm.VM.FriendsViewModel
import com.google.gson.Gson

@Composable
fun FriendsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: FriendsViewModel = viewModel()
) {
    val state = viewModel.usersState.value

    Column(modifier = Modifier.fillMaxSize()) {
        // الـ Header كما في الصورة
        Text(
            text = "Friends",
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF4A696A), // لون قريب من الصورة
            fontWeight = FontWeight.Bold
        )

        when (state) {
            is Resource.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4A696A))
                }
            }
            is Resource.Success -> {
                val users = state.data ?: emptyList()
                LazyColumn {
                    items(users) { user ->
                        UserItem(user,navController)
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = Color.LightGray
                        )
                    }
                }
            }
            is Resource.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message ?: "Error", color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun UserItem(user: UserResponse,navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable{
                val userJson = Uri.encode(Gson().toJson(user)) // تحويل الـ Object لـ String
                navController.navigate("chat/$userJson")
            }
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.profileImageUrl ?: R.drawable.avatar,
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}