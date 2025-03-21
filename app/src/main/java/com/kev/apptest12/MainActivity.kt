package com.kev.apptest12

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Category // Añadimos el ícono para Categories
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kev.apptest12.data.model.App
import com.kev.apptest12.data.remote.ApiService
import com.kev.apptest12.data.remote.RetrofitClient
import com.kev.apptest12.ui.theme.AppTest12Theme
import com.kevin.courseApp.ui.main.compose.HomeScreen
import com.kevin.courseApp.ui.main.compose.componentes.CategoryScreen

sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    object Chats : Screen("chats_screen")
    object Estados : Screen("estados_screen")
    object Upload : Screen("upload_screen")
    object Categories : Screen("categories_screen") // Añadimos la ruta para Categories
}

data class BottomNavItem(val title: String, val icon: ImageVector, val route: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        // Inicializar apiService dentro de onCreate
        val apiService = RetrofitClient.createApiService(this)

        setContent {
            AppTest12Theme {
                MainScreen(apiService, this)
            }
        }
    }
}

@Composable
fun MainScreen(apiService: ApiService, context: Context) {
    val navController = rememberNavController()
    val navItems = listOf(
        BottomNavItem("Home", Icons.Default.Home, Screen.Home.route),
        BottomNavItem("Chats", Icons.Default.Chat, Screen.Chats.route),
        BottomNavItem("Categories", Icons.Default.Category, Screen.Categories.route), // Añadimos Categories al BottomNav
        BottomNavItem("Estados", Icons.Default.Circle, Screen.Estados.route),
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        bottomBar = { BottomNavigationBar(navItems, navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    apiService = apiService,
                    navController = navController,
                    onUploadClick = { navController.navigate(Screen.Upload.route) }
                )
            }
            composable(Screen.Chats.route) { /* ChatsScreen() */ }
            composable(Screen.Categories.route) { CategoryScreen(apiService) } // Añadimos la ruta para CategoryScreen
            composable(Screen.Estados.route) { /* EstadosScreen() */ }
            composable(Screen.Upload.route) { /* UploadScreen(apiService, context) */ }
        }
    }
}

@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    NavigationBar(
        containerColor = Color(0xFF202C33),
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(bottom = 8.dp)
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (currentRoute == item.route) Color(0xFF25D366) else Color(0xFF8696A0),
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 12.sp,
                        fontWeight = if (currentRoute == item.route) FontWeight.Bold else FontWeight.Normal,
                        color = if (currentRoute == item.route) Color(0xFF25D366) else Color(0xFF8696A0)
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route)
                    }
                },
                alwaysShowLabel = true
            )
        }
    }
}