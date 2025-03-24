package com.kev.apptest12

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kev.apptest12.data.remote.ApiService
import com.kev.apptest12.data.remote.RetrofitClient
import com.kev.apptest12.ui.components.auth.LoginScreen
import com.kev.apptest12.ui.components.auth.RegisterScreen
import com.kev.apptest12.ui.components.home.AppDetailScreen
import com.kev.apptest12.ui.components.home.PantallaCambiarPlan
import com.kev.apptest12.ui.components.home.UploadScreen
import com.kev.apptest12.ui.components.profile.ProfileScreen
import com.kev.apptest12.ui.theme.AppTest12Theme
import com.kevin.courseApp.ui.main.compose.AllAppsScreen
 import com.kevin.courseApp.ui.main.compose.HomeScreen
import com.kevin.courseApp.ui.main.compose.componentes.CategoryScreen

sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    object Chats : Screen("chats_screen")
    object Estados : Screen("estados_screen")
    object Upload : Screen("upload_screen")
    object Categories : Screen("categories_screen?category={category}")
    object Login : Screen("login")
    object Register : Screen("register")
    object EditProfile : Screen("edit_profile")
    object Pricing : Screen("pricing")
    object MyApps : Screen("my_apps")
    object Settings : Screen("settings")
    object AppDetail : Screen("app_detail/{appId}")
    object AllApps : Screen("all_apps")
    object Profile : Screen("profile_screen")
    object ChangePlan : Screen("change_plan")

    fun createCategoriesRoute(category: String? = null): String {
        return if (category != null) {
            "categories_screen?category=${java.net.URLEncoder.encode(category, "UTF-8")}"
        } else {
            "categories_screen"
        }
    }
}

data class BottomNavItem(val title: String, val icon: ImageVector, val route: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            AppTest12Theme {
                val systemUiController = rememberSystemUiController()
                SideEffect {
                    systemUiController.setStatusBarColor(
                            color = Color(0xFF1976D2),
                        darkIcons = false
                    )
                }

                val navController = rememberNavController()
                val apiService = RetrofitClient.createApiService(this)

                val navItems = listOf(
                    BottomNavItem("Home", Icons.Default.Home, Screen.Home.route),
                    BottomNavItem("Search", Icons.Default.Search, Screen.Chats.route),
                    BottomNavItem("History", Icons.Default.Notifications, Screen.Estados.route),
                    BottomNavItem("Profile", Icons.Default.Person, Screen.Profile.route)
                )

                // Observar la ruta actual
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route?.substringBefore("?")

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)),
                    bottomBar = {
                        // Mostrar el BottomNavigationBar solo si no estamos en ciertas pantallas
                        if (currentRoute?.startsWith("app_detail/") != true &&
                            currentRoute?.startsWith("categories_screen") != true &&
                            currentRoute != Screen.AllApps.route &&
                            currentRoute != Screen.Pricing.route) {
                            BottomNavigationBar(navItems, navController)
                        }
                    },
                    containerColor = Color(0xFFE3F2FD)
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
                                context = this@MainActivity,
                                onUploadClick = {
                                    navController.navigate(Screen.Upload.route)
                                }
                            )
                        }
                        composable(Screen.Chats.route) {
                            Text("Search Screen (Chats) - To be implemented")
                        }
                        composable(
                            route = Screen.Categories.route,
                            arguments = listOf(
                                navArgument("category") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                }
                            )
                        ) { backStackEntry ->
                            val category = backStackEntry.arguments?.getString("category")?.let {
                                java.net.URLDecoder.decode(it, "UTF-8")
                            }
                            CategoryScreen(
                                apiService = apiService,
                                navController = navController,
                                selectedCategory = category
                            )
                        }
                        composable(Screen.Profile.route) {
                            ProfileScreen(apiService, this@MainActivity, navController)
                        }
                        composable(Screen.Estados.route) {
                            Text("History Screen (Estados) - To be implemented")
                        }
                        composable(Screen.Upload.route) {
                            UploadScreen(apiService, this@MainActivity, navController)
                        }
                        composable(Screen.Login.route) {
                            LoginScreen(apiService, this@MainActivity, navController)
                        }
                        composable(Screen.Register.route) {
                            RegisterScreen(apiService, this@MainActivity, navController)
                        }
                        composable(
                            route = "app_detail/{appId}",
                            arguments = listOf(navArgument("appId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val appId = backStackEntry.arguments?.getString("appId") ?: ""
                            AppDetailScreen(
                                appId = appId,
                                apiService = apiService,
                                navController = navController
                            )
                        }
                        composable(Screen.AllApps.route) {
                            AllAppsScreen(
                                apiService = apiService,
                                navController = navController
                            )
                        }
                        composable(Screen.Pricing.route) {
                            PantallaCambiarPlan(navController = navController)
                        }
                        composable(Screen.EditProfile.route) {
                            Text("Edit Profile Screen - To be implemented")
                        }
                        composable(Screen.MyApps.route) {
                            Text("My Apps Screen - To be implemented")
                        }
                        composable(Screen.Settings.route) {
                            Text("Settings Screen - To be implemented")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.substringBefore("?") ?: Screen.Home.route

    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        val itemsBeforeCenter = items.take(2)
        val itemsAfterCenter = items.drop(2)

        itemsBeforeCenter.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (currentRoute == item.route) Color(0xFF1976D2) else Color(0xFF999999),
                        modifier = Modifier.size(28.dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 12.sp,
                        fontWeight = if (currentRoute == item.route) FontWeight.Bold else FontWeight.Normal,
                        color = if (currentRoute == item.route) Color(0xFF1976D2) else Color(0xFF999999)
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF1976D2),
                    unselectedIconColor = Color(0xFF999999),
                    selectedTextColor = Color(0xFF1976D2),
                    unselectedTextColor = Color(0xFF999999),
                    indicatorColor = Color(0xFF1976D2).copy(alpha = 0.2f)
                )
            )
        }

        NavigationBarItem(
            icon = {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .offset(y = (-10).dp)
                        .background(Color(0xFF1976D2), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Upload",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            },
            label = { /* Sin etiqueta para el botón central */ },
            selected = false,
            onClick = {
                navController.navigate(Screen.Upload.route)
            },
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color(0xFF1976D2).copy(alpha = 0.2f)
            )
        )

        itemsAfterCenter.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (currentRoute == item.route) Color(0xFF1976D2) else Color(0xFF999999),
                        modifier = Modifier.size(28.dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 12.sp,
                        fontWeight = if (currentRoute == item.route) FontWeight.Bold else FontWeight.Normal,
                        color = if (currentRoute == item.route) Color(0xFF1976D2) else Color(0xFF999999)
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF1976D2),
                    unselectedIconColor = Color(0xFF999999),
                    selectedTextColor = Color(0xFF1976D2),
                    unselectedTextColor = Color(0xFF999999),
                    indicatorColor = Color(0xFF1976D2).copy(alpha = 0.2f)
                )
            )
        }
    }
}