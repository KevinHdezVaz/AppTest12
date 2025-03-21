package com.kevin.courseApp.ui.main.compose.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kev.apptest12.R
 import com.kev.apptest12.data.remote.ApiService
import kotlinx.coroutines.launch

data class Category(val name: String, val icon: Int)

data class Ground(val name: String, val location: String, val image: Int, val sports: List<Int>, val distance: String? = null)

val popularGrounds = listOf(
    Ground("Hover ground", "Fairfield", R.drawable.ic_launcher_foreground, listOf(R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground)),
    Ground("Sport ground", "Bangore", R.drawable.ic_launcher_foreground, listOf(R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground))
)

val nearbyGrounds = listOf(
    Ground("Nearby ground", "", R.drawable.ic_launcher_foreground, listOf(), "8 KM")
)

// Sección de categorías
@Composable
fun CategorySection(apiService: ApiService, navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var categories by remember { mutableStateOf(listOf<Category>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Cargar las aplicaciones y extraer las categorías
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val apps = apiService.getApps()
                // Extraer categorías únicas y mapearlas a Category
                categories = apps.map { it.category }
                    .distinct()
                    .map { categoryName ->
                        Category(
                            name = categoryName,
                            icon = R.drawable.ic_launcher_foreground // Usamos el mismo ícono placeholder para todas
                        )
                    }
            } catch (e: Exception) {
                errorMessage = "Error loading categories: ${e.message}"
            }
        }
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categories",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "View all",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    navController.navigate("categories_screen") // Navegar a CategoryScreen
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories) { category ->
                CategoryCard(category)
            }
        }
    }
}

@Composable
fun CategoryCard(category: Category) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = category.icon),
            contentDescription = category.name,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = category.name,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// Sección de grounds populares
@Composable
fun PopularGroundsSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Popular ground",
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

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(popularGrounds) { ground ->
            GroundCard(ground)
        }
    }
}

// Sección de grounds cercanos
@Composable
fun NearbyGroundsSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Nearby you",
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

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(nearbyGrounds) { ground ->
            GroundCard(ground)
        }
    }
}

@Composable
fun GroundCard(ground: Ground) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable { /* Acción al hacer clic */ },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = ground.image),
                contentDescription = ground.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = ground.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Location",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = ground.location,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    ground.sports.forEach { sportIcon ->
                        Image(
                            painter = painterResource(id = sportIcon),
                            contentDescription = "Sport Icon",
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp)
                        )
                    }
                }
                if (ground.distance != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = ground.distance,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}