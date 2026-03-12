package com.example.nmnm.design

import android.util.Log
import com.example.nmnm.Models.MessageModel
import com.example.nmnm.VM.ChatViewModel
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.nmnm.Models.UserResponse
import com.example.nmnm.R
import com.example.nmnm.VM.formatTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    user: UserResponse,
    viewModel: ChatViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    LaunchedEffect(user.id) {
        viewModel.initChat(user.id)
    }

    val authState by viewModel.userState.collectAsState()
    if (authState.isLoading || authState.token == null) {
        ShimmerChatScreen()
    }
    else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F6FA))

        ) {

            ChatHeader(user, onBackClick)

            MessagesSection(
                messages = viewModel.messages,
                modifier = Modifier.weight(1f)
            )


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .imePadding()
            ) {
                MessageInput { message ->
                    viewModel.sendMessage(user.id, message)
                }
            }
        }
    }
}

@Composable
fun ChatHeader(
    user: UserResponse,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = onBackClick ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back" , tint = Color(0xFF106B61))
        }

        AsyncImage(
            model = user.profileImageUrl ?: R.drawable.avatar,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(2.dp, Color(0xFF4A696A), CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = user.fullName,
                fontWeight = FontWeight.Bold,
                color= Color(0xFF106B61)
            )
            Text(
                text = user.email,
                 fontSize = 12.sp,
                color =  Color.Gray
            )
        }
    }
}


@Composable
fun MessagesSection(
    messages: List<MessageModel>,
    modifier: Modifier = Modifier
) {

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(vertical = 12.dp),
        modifier = modifier
            .background(Color(0xFFF5F6FA))
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(messages) { message ->
            MessageBubble(message)
        }
    }
}


@Composable
fun MessageBubble(message: MessageModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isSender) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    color = if (message.isSender) Color(0xFFE3F2FD) else Color(0xFFEEEEEE),
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (message.isSender) 20.dp else 0.dp,
                        bottomEnd = if (message.isSender) 0.dp else 20.dp
                    )
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.text,
                color = Color.Black,
                fontSize = 15.sp,
                lineHeight = 20.sp
            )
        }

        Text(
            text = formatTime(message.time),
            fontSize = 10.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
        )
    }
}

@Composable
fun MessageInput(
    onSend: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxWidth(),

        color = Color(0xFFF5F6FA)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(Color.White, CircleShape)
                .border(1.dp, Color(0xFFE0E0E0), CircleShape)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {


            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("type message here", color = Color.Gray) },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                maxLines = 4,
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color=Color.Black
                ),
            )


            IconButton(
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xFF106B61), CircleShape),
                onClick = {
                    if (text.isNotBlank()) {
                        onSend(text)
                        text = ""
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun Modifier.shimmerEffect(): Modifier {
    val transition = rememberInfiniteTransition(label = "")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f),
        ),
        start = Offset(10f, 10f),
        end = Offset(translateAnim, translateAnim)
    )

    return this.background(brush)
}

@Composable
fun ShimmerChatHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .shimmerEffect())

        Spacer(modifier = Modifier.width(16.dp))


        Box(modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .shimmerEffect())

        Spacer(modifier = Modifier.width(8.dp))

        Column {

            Box(modifier = Modifier
                .width(100.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect())
            Spacer(modifier = Modifier.height(6.dp))

            Box(modifier = Modifier
                .width(60.dp)
                .height(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect())
        }
    }
}

@Composable
fun ShimmerMessageItem(isSender: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = if (isSender) Alignment.End else Alignment.Start
    ) {

        Box(
            modifier = Modifier
                .width(if (isSender) 200.dp else 180.dp)
                .height(50.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp, topEnd = 20.dp,
                        bottomStart = if (isSender) 20.dp else 0.dp,
                        bottomEnd = if (isSender) 0.dp else 20.dp
                    )
                )
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(4.dp))

        Box(modifier = Modifier
            .width(40.dp)
            .height(10.dp)
            .clip(RoundedCornerShape(2.dp))
            .shimmerEffect())
    }
}

@Composable
fun ShimmerChatScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6FA))
    ) {
        ShimmerChatHeader()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            userScrollEnabled = false
        ) {
            items(6) { index ->
                ShimmerMessageItem(isSender = index % 2 == 0)
            }
        }
    }
}
