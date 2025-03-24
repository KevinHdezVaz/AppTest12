package com.kevin.courseApp.ui.main.compose.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
            .padding(vertical = 8.dp, horizontal = 16.dp), // Más padding para un diseño más espacioso
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp), // Más sombra para un efecto elevado
        colors = CardDefaults.cardColors(
            containerColor = Color.White // Fondo blanco para el Card
        ),
        shape = RoundedCornerShape(12.dp) // Bordes más redondeados
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp), // Más padding interno para un diseño más limpio
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen de la app
            AsyncImage(
                model = "https://backtest.aftconta.mx/storage/${app.logo_path}",
                contentDescription = "${app.name} Logo",
                modifier = Modifier
                    .size(64.dp) // Aumentar el tamaño de la imagen
                    .clip(RoundedCornerShape(8.dp)) // Bordes redondeados para la imagen
                    .background(Color(0xFFE3F2FD)) // Fondo azul claro si la imagen falla
                    .padding(4.dp), // Padding interno para un efecto de "marco"
                alignment = Alignment.Center,
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                error = painterResource(R.drawable.ic_launcher_foreground)
            )

            Spacer(modifier = Modifier.width(16.dp)) // Más espacio entre la imagen y el texto

            // Contenido de texto
            Column(
                modifier = Modifier
                    .weight(1f) // Ocupa el espacio restante
                    .padding(end = 8.dp) // Espacio para el ícono de la derecha
            ) {
                Text(
                    text = app.name,
                    fontSize = 18.sp, // Aumentar el tamaño del título
                    fontWeight = FontWeight.Bold, // Hacer el título más destacado
                    color = Color(0xFF1976D2), // Usar el azul principal para el título
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = app.description,
                    fontSize = 14.sp, // Aumentar ligeramente el tamaño de la descripción
                    color = Color(0xFF666666), // Gris oscuro para mejor contraste
                    maxLines = 2,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Testear ahora", // Texto para incentivar la acción
                    fontSize = 12.sp,
                    color = Color(0xFF1976D2), // Azul para destacar
                    fontWeight = FontWeight.Medium
                )
            }

            // Ícono de flecha para indicar que es clickable
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Ver detalles",
                tint = Color(0xFF1976D2), // Azul principal
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}