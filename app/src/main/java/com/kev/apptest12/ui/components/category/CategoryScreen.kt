package com.kevin.courseApp.ui.main.compose.componentes

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.kev.apptest12.R
import com.kev.apptest12.data.model.App
import com.kev.apptest12.data.remote.ApiService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    apiService: ApiService,
    navController: NavController,
    selectedCategory: String? = null // Nuevo parámetro para filtrar por categoría
) {
    val coroutineScope = rememberCoroutineScope()
    var apps by remember { mutableStateOf(listOf<App>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isGridView by remember { mutableStateOf(false) } // Estado para alternar entre lista y cuadrícula
    var isLoading by remember { mutableStateOf(true) } // Estado para la carga

    // Cargar las aplicaciones
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                apps = apiService.getApps()
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Error loading apps: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Configurar la animación Lottie
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animacionloader))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    // Filtrar aplicaciones por categoría seleccionada, si existe
    val filteredApps = if (selectedCategory != null) {
        apps.filter { it.category == selectedCategory }
    } else {
        apps
    }

    // Agrupar aplicaciones por categoría
    val appsByCategory = filteredApps.groupBy { it.category }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = selectedCategory ?: "Categories",
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
                            imageVector = if (isGridView) Icons.Default.List else Icons.Default.GridView,
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
            } else if (filteredApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedCategory != null) {
                            "No hay aplicaciones en la categoría $selectedCategory."
                        } else {
                            "No hay aplicaciones registradas aún."
                        },
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                ) {
                    appsByCategory.forEach { (category, appsInCategory) ->
                        item {
                            Text(
                                text = category,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        if (isGridView) {
                            item {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(4.dp)
                                ) {
                                    items(appsInCategory) { app ->
                                        AppGridItem(app = app) {
                                            navController.navigate("app_detail/${app.id}")
                                        }
                                    }
                                }
                            }
                        } else {
                            items(appsInCategory) { app ->
                                AppListItem(app = app) {
                                    navController.navigate("app_detail/${app.id}")
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppListItem(
    app: App,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder para el logo
            Icon(
                imageVector = Icons.Default.Apps,
                contentDescription = "App Logo",
                modifier = Modifier.size(40.dp),
                tint = Color(0xFF1976D2)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = app.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Creator: ${app.creator}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = app.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun AppGridItem(
    app: App,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Placeholder para el logo
            Icon(
                imageVector = Icons.Default.Apps,
                contentDescription = "App Logo",
                modifier = Modifier.size(60.dp),
                tint = Color(0xFF1976D2)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = app.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Creator: ${app.creator}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}