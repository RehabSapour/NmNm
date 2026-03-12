package com.example.nmnm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nmnm.navigation.AppNavHost
import com.example.nmnm.ui.theme.NmNmTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NmNmTheme {
                val navController = rememberNavController()


                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val bottomBarScreens = listOf("friends", "chats", "settings")

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // 3. شرط الظهور: لو المسار الحالي موجود في القائمة، اظهر الـ Bar
                        if (currentRoute in bottomBarScreens) {
                            NavigationBar(
                                containerColor = Color.White, // تقدري تغيريها حسب ديزاين فيجما
                                tonalElevation = 8.dp
                            ) {
                                NavigationBarItem(
                                    selected = currentRoute == "chats",
                                    onClick = { navController.navigate("chats") {
                                        popUpTo(navController.graph.startDestinationId)
                                        launchSingleTop = true
                                    }},
                                    label = { Text("Chats") },
                                    icon = { Icon(Icons.Default.Chat, contentDescription = null) }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "friends",
                                    onClick = { navController.navigate("friends") {
                                        popUpTo(navController.graph.startDestinationId)
                                        launchSingleTop = true
                                    }},
                                    label = { Text("Friends") },
                                    icon = { Icon(Icons.Default.People, contentDescription = null) }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "settings",
                                    onClick = { navController.navigate("settings") {
                                        popUpTo(navController.graph.startDestinationId)
                                        launchSingleTop = true
                                    }},
                                    label = { Text("Settings") },
                                    icon = { Icon(Icons.Default.Settings, contentDescription = null) }
                                )
                            }
                        }
                    }
                ) { innerPadding ->

                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
