package com.kevin.courseApp.ui.main.compose

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.kev.apptest12.R
import com.kev.apptest12.data.model.App
import com.kev.apptest12.data.remote.ApiService
import com.kevin.courseApp.ui.main.compose.componentes.AppCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AllAppsScreen(
    apiService: ApiService,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    var apps by remember { mutableStateOf(listOf<App>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isGridView by remember { mutableStateOf(false) } // Estado para alternar entre lista y cuadrícula

    val loadApps: () -> Unit = {
        coroutineScope.launch {
            isLoading = true
            try {
                Log.d("AllAppsScreen", "Cargando todas las aplicaciones...")
                apps = apiService.getAllApps()
                Log.d("AllAppsScreen", "Aplicaciones cargadas: ${apps.size}")
                errorMessage = null
            } catch (e: Exception) {
                Log.e("AllAppsScreen", "Error al cargar aplicaciones: ${e.message}")
                errorMessage = "Error al cargar aplicaciones: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadApps()
    }

    // Configurar la animación Lottie
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animacionloader))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "All Apps",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isGridView = !isGridView }) {
                        Icon(
                            imageVector = if (isGridView) Icons.Default.List else Icons.Default.ViewModule,
                            contentDescription = if (isGridView) "Switch to List View" else "Switch to Grid View",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2),
                    actionIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFE3F2FD)
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(150.dp)
                )
            }
        } else {
            if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage ?: "Error desconocido",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else if (apps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay aplicaciones registradas aún.",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            } else {
                if (isGridView) {
                    // Vista de cuadrícula
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2), // 2 columnas
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(apps) { app ->
                            AppCard(app) {
                                navController.navigate("app_detail/${app.id}")
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                } else {
                    // Vista de lista
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(apps) { app ->
                            AppCard(app) {
                                navController.navigate("app_detail/${app.id}")
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}