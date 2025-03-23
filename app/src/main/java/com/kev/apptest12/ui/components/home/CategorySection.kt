package com.kevin.courseApp.ui.main.compose.componentes

import android.provider.CalendarContract
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kev.apptest12.Screen
import com.kev.apptest12.data.model.App
import com.kev.apptest12.data.remote.ApiService
import kotlinx.coroutines.launch

@Composable
fun CategorySection(
    apiService: ApiService,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    var apps by remember { mutableStateOf(listOf<App>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Cargar las aplicaciones
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                apps = apiService.getApps()
            } catch (e: Exception) {
                Log.e("CategorySection", "Error al cargar aplicaciones: ${e.message}")
                errorMessage = "Error loading categories: ${e.message}"
            }
        }
    }

    // Agrupar aplicaciones por categoría
    val categories = apps.map { it.category }.distinct()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categorias",
                fontSize = 18.sp,
                color =Color.Black,
                        fontWeight = FontWeight.Bold


            )
            TextButton(onClick = { navController.navigate(Screen.Categories.createCategoriesRoute()) }) {
                Text(
                    text = "Ver más",
                    fontSize = 14.sp,
                    color = Color(0xFF1976D2)
                )
            }
        }

        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                CategoryItem(category = category) {
                    navController.navigate(Screen.Categories.createCategoriesRoute(category))
                }
            }
        }
    }
}

@Composable
fun CategoryItem(category: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = category,
                fontSize = 14.sp,
                color = Color(0xFF1976D2)
            )
        }
    }
}