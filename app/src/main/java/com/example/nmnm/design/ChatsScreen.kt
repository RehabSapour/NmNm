package com.example.nmnm.design

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.nmnm.Models.ChatItem
import com.example.nmnm.Models.UserResponse
import com.example.nmnm.R
import com.example.nmnm.VM.ChannelsViewModel
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlin.String


@Composable
fun ChatsScreen(
    navController: NavController,
    viewModel: ChannelsViewModel = viewModel(),
    onChatClick: (ChatItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val chats by viewModel.channels.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val filteredChats = remember(searchQuery, chats) {
        if (searchQuery.isBlank()) chats.orEmpty()
        else chats.orEmpty().filter {
            it.name.contains(searchQuery, true) ||
                    it.lastMessage.contains(searchQuery, true)
        }
    }


    val showNoChats = !isLoading && chats.isNullOrEmpty() && searchQuery.isBlank()
    val showNoResults = !isLoading && chats?.isNotEmpty() == true && filteredChats.isEmpty() && searchQuery.isNotBlank()



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        // horizontalAlignment = Alignment.CenterHorizontally,
        // verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Chats",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 20.dp, bottom = 20.dp)
                .fillMaxWidth(),
            color=Color(0xFF106B61),
            textAlign = TextAlign.Center
        )


        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search") },
            textStyle = TextStyle(
                fontSize = 16.sp,
                color=Color.Black
            ),
            leadingIcon = { Icon(Icons.Outlined.Search, null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )


        Spacer(Modifier.height(12.dp))


        when {
            isLoading -> {
                LazyColumn {
                    items(6) { ChatRowShimmer() }
                }}


            showNoChats -> {
                EmptyState(
                    title = "No chats yet",
                    subtitle = "Start a conversation to see messages here"
                )
            }


            showNoResults -> {
                EmptyState(
                    title = "No results",
                    subtitle = "Try searching with another name"
                )
            }

            else -> {
                LazyColumn {
                   items(filteredChats){
                       ChatRow(it) {
                           val userJson = Uri.encode(Gson().toJson(UserResponse(
                               id = it.id,
                               email = it.email,
                               fullName = it.name,
                               profileImageUrl = it.image

                           )))
                           it.unreadCount = 0
                           navController.navigate("chat/$userJson")
                       }
                   }
                }
            }
        }
    }
}

@Composable
private fun ChatRow(
    chat: ChatItem,
    onClick: ( ) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                AsyncImage(
                    model = chat.image ?: R.drawable.avatar,
                    contentDescription = null,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(chat.name, fontWeight = FontWeight.SemiBold,color=Color.Black)
                Spacer(Modifier.height(4.dp))
                Text(chat.lastMessage, fontSize = 13.sp, color = Color.Gray, maxLines = 1)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(chat.time, fontSize = 12.sp, color = Color.Gray)
                if (chat.unreadCount > 0) {
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF106B61), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                            .defaultMinSize(minWidth = 20.dp, minHeight = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = chat.unreadCount.toString(),
                            color = Color.White,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(start = 76.dp),
            thickness = 0.7.dp,
            color = Color(0xFFC5C3C3)
        )
    }
}

/* -------------------- SHIMMER -------------------- */

@Composable
private fun ChatRowShimmer() {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.3f),
        Color.LightGray.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing)
        ), label = "translate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translate - 200f, 0f),
        end = Offset(translate, 0f)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(brush)
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .height(14.dp)
                    .fillMaxWidth(0.5f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .height(12.dp)
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
        }
    }
}

/* -------------------- EMPTY STATE -------------------- */

@Composable
private fun EmptyState(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text(subtitle, fontSize = 14.sp, color = Color.Gray)
    }
}
