package com.kevin.courseApp.ui.main.compose.componentes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kev.apptest12.data.model.App
import com.kev.apptest12.data.remote.ApiService
import com.kev.apptest12.data.remote.TestRequest
import kotlinx.coroutines.launch

@Composable
fun PopularAppsSection(apps: List<App>, apiService: ApiService) {
    val coroutineScope = rememberCoroutineScope()
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Popular Apps",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "View all",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { /* Acción para ver todas */ }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(apps) { app ->
                AppCard(app) {
                    coroutineScope.launch {
                        try {
                            val response = apiService.testApp(app.id, TestRequest(tester_id = "anonymous"))
                            if (!response.isSuccessful) {
                                errorMessage = "Error en POST /apps/${app.id}/test: ${response.errorBody()?.string()}"
                            }
                        } catch (e: Exception) {
                            errorMessage = "Error en POST /apps/${app.id}/test: ${e.message}"
                        }
                    }
                }
            }
        }
    }
}