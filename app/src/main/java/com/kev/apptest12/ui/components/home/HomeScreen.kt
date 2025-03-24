package com.kevin.courseApp.ui.main.compose

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kev.apptest12.Screen
import com.kev.apptest12.data.model.App
import com.kev.apptest12.data.remote.ApiService
import com.kev.apptest12.utils.LottieLoadingAnimation
import com.kevin.courseApp.ui.main.compose.componentes.AppCard
import com.kevin.courseApp.ui.main.compose.componentes.CategorySection
import com.kevin.courseApp.ui.main.compose.componentes.PopularAppsSectionHeader
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    apiService: ApiService,
    context: Context,
    navController: NavController,
    onUploadClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var apps by remember { mutableStateOf(listOf<App>()) }
    var searchQuery by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var popularAppsErrorMessage by remember { mutableStateOf<String?>(null) }
    var userName by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val loadApps: () -> Unit = {
        coroutineScope.launch {
            isLoading = true
            try {
                Log.d("HomeScreen", "Cargando aplicaciones registradas...")
                apps = apiService.getAllApps()
                Log.d("HomeScreen", "Aplicaciones cargadas: ${apps.size}")
                errorMessage = null
            } catch (e: Exception) {
                Log.e("HomeScreen", "Error al cargar aplicaciones: ${e.message}")
                errorMessage = "Error en GET /all-apps: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        userName = sharedPreferences.getString("user_name", null)
        loadApps()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = userName?.let { "Hello, $it" } ?: "Hello, Guest",
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Good morning 😊",
                            fontSize = 14.sp,
                            color = Color(0xFFB3E5FC)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2), // Blue color
                    actionIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { /* Acción de notificaciones */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp) // Adjust height to make it taller
                    .clip(
                        RoundedCornerShape(
                            bottomStart = 30.dp, // Rounded bottom-left corner
                            bottomEnd = 30.dp,   // Rounded bottom-right corner
                            topStart = 0.dp,     // No rounding on top-left
                            topEnd = 0.dp        // No rounding on top-right
                        )
                    )
            )
        },
        containerColor = Color(0xFFE3F2FD)
    ) { paddingValues ->
        if (isLoading) {
            LottieLoadingAnimation(
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(top = 15.dp, start = 15.dp, end = 15.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                errorMessage?.let {
                    item {
                        Text(
                            text = it,
                            color = Color.Red,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp)),
                        placeholder = { Text(text = "Buscar apps...", color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                tint = Color(0xFF1976D2)
                            )
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1976D2),
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                }

                item {
                    CategorySection(apiService = apiService, navController = navController)
                }

                val filteredApps = apps.filter {
                    it.name.contains(searchQuery, ignoreCase = true) || it.description.contains(
                        searchQuery,
                        ignoreCase = true
                    )
                }

                if (apps.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay aplicaciones registradas aún.",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    item {
                        PopularAppsSectionHeader(
                            errorMessage = popularAppsErrorMessage,
                            onViewAllClick = { navController.navigate(Screen.AllApps.route) }
                        )
                    }

                    if (filteredApps.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No se encontraron aplicaciones registradas.",
                                    fontSize = 16.sp,
                                    color = Color.Black.copy(alpha = 0.6f)
                                )
                            }
                        }
                    } else {
                        items(filteredApps) { app ->
                            AppCard(app) {
                                navController.navigate("app_detail/${app.id}")
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}