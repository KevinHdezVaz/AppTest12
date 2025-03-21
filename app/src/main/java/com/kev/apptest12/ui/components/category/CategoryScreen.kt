package com.kevin.courseApp.ui.main.compose.componentes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.times
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.kev.apptest12.data.model.App
import com.kev.apptest12.data.remote.ApiService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(apiService: ApiService) {
    val coroutineScope = rememberCoroutineScope()
    var apps by remember { mutableStateOf(listOf<App>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isGridView by remember { mutableStateOf(false) } // Estado para alternar entre lista y cuadrícula

    // Cargar las aplicaciones
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                apps = apiService.getApps()
            } catch (e: Exception) {
                errorMessage = "Error loading apps: ${e.message}"
            }
        }
    }

    // Agrupar aplicaciones por categoría
    val appsByCategory = apps.groupBy { it.category }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Categories") },
                actions = {
                    IconButton(onClick = { isGridView = !isGridView }) {
                        Icon(
                            imageVector = if (isGridView) Icons.Default.List else Icons.Default.GridView,
                            contentDescription = if (isGridView) "Switch to List View" else "Switch to Grid View"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item {
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            appsByCategory.forEach { (category, appsInCategory) ->
                item {
                    Text(
                        text = category,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                if (isGridView) {
                    item {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.height((appsInCategory.size / 2 + appsInCategory.size % 2) * 150.dp),
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            items(appsInCategory) { app ->
                                AppGridItem(app = app)
                            }
                        }
                    }
                } else {
                    items(appsInCategory) { app ->
                        AppListItem(app = app)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun AppListItem(app: App) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { /* Acción al hacer clic en la app */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = app.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
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
fun AppGridItem(app: App) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { /* Acción al hacer clic en la app */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = app.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
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