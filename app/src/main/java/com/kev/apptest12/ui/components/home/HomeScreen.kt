package com.kevin.courseApp.ui.main.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kev.apptest12.data.model.App
import com.kev.apptest12.data.remote.ApiService
import com.kevin.courseApp.ui.main.compose.componentes.CategorySection
import com.kevin.courseApp.ui.main.compose.componentes.PopularAppsSection
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    apiService: ApiService,
    navController: NavController, // Añadimos NavController como parámetro
    onUploadClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var apps by remember { mutableStateOf(listOf<App>()) }
    var searchQuery by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                apps = apiService.getApps()
            } catch (e: Exception) {
                errorMessage = "Error en GET /apps: ${e.message}"
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hello, Tester",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Good morning 😊",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Acción de notificaciones */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onUploadClick) {
                Text("Upload")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Search apps") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            CategorySection(apiService = apiService, navController = navController) // Pasamos el NavController

            Spacer(modifier = Modifier.height(24.dp))

            PopularAppsSection(
                apps.filter {
                    it.name.contains(searchQuery, ignoreCase = true) || it.description.contains(
                        searchQuery,
                        ignoreCase = true
                    )
                },
                apiService
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}