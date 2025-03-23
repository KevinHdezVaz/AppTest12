package com.kev.apptest12.ui.components.profile

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.navigation.NavHostController
import com.kev.apptest12.Screen
import com.kev.apptest12.data.remote.ApiService
import com.kev.apptest12.data.remote.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(apiService: ApiService, context: Context, navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("access_token", null)

    var userName by remember { mutableStateOf("Cargando...") }
    var userEmail by remember { mutableStateOf("Cargando...") }
    var credits by remember { mutableStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val profileOptions = listOf(
        ProfileOption("Editar Perfil", Icons.Default.Edit, Screen.EditProfile.route),
        ProfileOption("Precios", Icons.Default.MonetizationOn, Screen.Pricing.route),
        //ProfileOption("Mis Apps", Icons.Default.Apps, Screen.MyApps.route),
        ProfileOption("Ajustes", Icons.Default.Settings, Screen.Settings.route),
        ProfileOption("Cerrar Sesión", Icons.Default.ExitToApp, null)
    )

    LaunchedEffect(token) {
        if (token != null) {
            isLoading = true
            coroutineScope.launch {
                try {
                    val response = apiService.getUser()
                    if (response.isSuccessful) {
                        val userResponse = response.body()
                        if (userResponse != null) {
                            userName = userResponse.user.name
                            userEmail = userResponse.user.email
                            credits = 100 // Valor estático por ahora
                        } else {
                            errorMessage = "Error: Respuesta del servidor vacía"
                        }
                    } else {
                        errorMessage = "Error al cargar el perfil: ${response.message()}"
                        if (response.code() == 401) {
                            sharedPreferences.edit().remove("access_token").apply()
                            RetrofitClient.saveToken(context, null)
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Categories.route) { inclusive = true }
                            }
                        }
                    }
                } catch (e: Exception) {
                    errorMessage = "Error: ${e.message}"
                } finally {
                    isLoading = false
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Perfil",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2), // Azul principal
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFE3F2FD) // Fondo azul claro
    ) { innerPadding ->
        if (token == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Por favor, inicia sesión para ver tu perfil",
                            fontSize = 16.sp,
                            color = Color(0xFF1976D2), // Azul principal
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                navController.navigate(Screen.Login.route)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1976D2), // Azul principal
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Iniciar Sesión", fontSize = 16.sp)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1976D2)), // Azul principal
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userName.first().toString().uppercase(),
                                    color = Color.White,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color(0xFF1976D2), // Azul principal
                                    modifier = Modifier.size(32.dp)
                                )
                            } else {
                                errorMessage?.let { message ->
                                    Text(
                                        text = message,
                                        color = Color.Red,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                }
                                Text(
                                    text = "Bienvenido, $userName",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1976D2) // Azul principal
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Email: $userEmail",
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Créditos",
                                tint = Color(0xFF1976D2), // Azul principal
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Créditos",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                                Text(
                                    text = "Tienes $credits créditos",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                items(profileOptions) { option ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        onClick = {
                            if (option.route != null) {
                                navController.navigate(option.route)
                            } else if (option.title == "Cerrar Sesión") {
                                sharedPreferences.edit()
                                    .remove("access_token")
                                    .apply()
                                RetrofitClient.saveToken(context, null)
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) { inclusive = false }
                                }
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = option.icon,
                                contentDescription = option.title,
                                tint = Color(0xFF1976D2), // Azul principal
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = option.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Ir a ${option.title}",
                                tint = Color(0xFF999999),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

data class ProfileOption(val title: String, val icon: ImageVector, val route: String?)