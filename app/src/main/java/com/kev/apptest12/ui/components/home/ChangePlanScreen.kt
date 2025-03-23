package com.kev.apptest12.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kev.apptest12.data.model.Feature
import com.kev.apptest12.data.model.Plan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCambiarPlan(
    navController: NavController
) {
    // Lista de planes (simulada, puedes obtenerla de una API si es necesario)
    val planes = listOf(
        Plan(
            name = "Pruebas Públicas",
            price = "GRATIS",
            features = listOf(
                Feature("12 Probadores en las próximas 36 horas", true),
                Feature("Necesitas 60 créditos para publicar apps", false),
                Feature("Solo 1 app por mes", false)
            ),
            isSelected = false
        ),
        Plan(
            name = "Pruebas Públicas Especiales",
            price = "$10 /App",
            features = listOf(
                Feature("12 Probadores en 12 horas", true),
                Feature("Los usuarios de nuestra app probarán tu app", true)
            ),
            isSelected = true
        ),
        Plan(
            name = "Pruebas Privadas",
            price = "$15 /App",
            features = listOf(
                Feature("25 Probadores Profesionales en las próximas 6 horas", true),
                Feature("Pruebas Continuas durante 16 días", true),
                Feature("Garantía de Acceso a Producción", true),
                Feature("Informe de Retroalimentación de Probadores", true),
                Feature("Respuestas del Formulario de Acceso a Producción", true)
            ),
            isSelected = false
        )
    )

    // Estado para el plan seleccionado
    var planSeleccionado by remember { mutableStateOf(planes.find { it.isSelected }?.name ?: "Pruebas Públicas Especiales") }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cambiar Plan",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
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
        containerColor = Color(0xFFF5F5F5) // Fondo gris claro diferente
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "TODOS LOS PLANES",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            items(planes) { plan ->
                TarjetaPlan(
                    plan = plan,
                    estaSeleccionado = plan.name == planSeleccionado,
                    onSeleccionar = {
                        planSeleccionado = plan.name
                        // Aquí puedes agregar lógica para cambiar el plan en el backend si es necesario
                    }
                )
            }

            item {
                SeccionCreditos(
                    creditos = listOf(
                        "Pruebas Públicas" to 0,
                        "Pruebas Públicas Especiales" to 0
                    ),
                    planSeleccionado = planSeleccionado
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun TarjetaPlan(
    plan: Plan,
    estaSeleccionado: Boolean,
    onSeleccionar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSeleccionar() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), // Fondo blanco para las tarjetas
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = plan.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
                RadioButton(
                    selected = estaSeleccionado,
                    onClick = { onSeleccionar() },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF1976D2),
                        unselectedColor = Color(0xFF999999)
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = plan.price,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            plan.features.forEach { caracteristica ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (caracteristica.isPositive) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = if (caracteristica.isPositive) Color(0xFF4CAF50) else Color(0xFFF44336),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = caracteristica.description,
                        fontSize = 14.sp,
                        color = Color(0xFF333333)
                    )
                }
            }
        }
    }
}

@Composable
fun SeccionCreditos(
    creditos: List<Pair<String, Int>>,
    planSeleccionado: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "CRÉDITOS",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            creditos.forEach { (nombrePlan, credito) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$nombrePlan:",
                        fontSize = 14.sp,
                        color = Color(0xFF333333)
                    )
                    Text(
                        text = credito.toString(),
                        fontSize = 14.sp,
                        color = Color(0xFF333333)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* Acción para cambiar el plan */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1976D2),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Confirmar Cambio",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Pruebas Públicas Especiales:",
                    fontSize = 14.sp,
                    color = Color(0xFF333333)
                )
                Text(
                    text = "0",
                    fontSize = 14.sp,
                    color = Color(0xFF333333)
                )
            }
        }
    }
}