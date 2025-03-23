package com.kevin.courseApp.ui.main.compose.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kev.apptest12.R
import com.kev.apptest12.data.model.App

@Composable
fun AppCard(app: App, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // Fondo blanco para el Card
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White) // Fondo blanco para el Row (redundante pero explícito)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = "https://backtest.aftconta.mx/storage/${app.logo_path}",
                contentDescription = "${app.name} Logo",
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 12.dp),
                alignment = Alignment.Center,
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                error = painterResource(R.drawable.ic_launcher_foreground)
            )
            // Contenido de texto
            Column(
                modifier = Modifier.weight(1f) // Ocupa el espacio restante
            ) {
                Text(
                    text = app.name,
                    fontSize = 16.sp,
                    color = Color.Black, // Título en negro para contraste
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Text(
                    text = app.description,
                    fontSize = 12.sp,
                    color = Color.Black, // Subtítulo en negro para contraste
                    maxLines = 2
                )
            }
        }
    }
}