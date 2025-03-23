package com.kev.apptest12.ui.components.home

import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.kev.apptest12.Screen
import com.kev.apptest12.data.remote.ApiService
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(apiService: ApiService, context: Context, navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var logoUri by remember { mutableStateOf<Uri?>(null) }
    var tags by remember { mutableStateOf(listOf<String>()) }
    var newTag by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }

    val categories = listOf("Productividad", "Juegos", "Educación", "Entretenimiento", "Utilidades", "Social")

    val logoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        logoUri = uri
    }

    val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        android.Manifest.permission.READ_MEDIA_IMAGES
    } else {
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            logoLauncher.launch("image/*")
        } else {
            Toast.makeText(
                context,
                "Se necesita permiso para acceder a las fotos. Por favor, habilita el permiso en la configuración.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("access_token", null)

    LaunchedEffect(Unit) {
        if (token == null) {
            showAuthDialog = true
        }
    }

    if (showAuthDialog) {
        AlertDialog(
            onDismissRequest = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Upload.route) { inclusive = true }
                }
            },
            title = { Text("Autenticación requerida") },
            text = { Text("Necesitas estar logueado para subir apps.") },
            confirmButton = {
                Button(
                    onClick = {
                        navController.navigate(Screen.Login.route)
                        showAuthDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2), // Azul principal
                        contentColor = Color.White
                    )
                ) {
                    Text("Iniciar Sesión")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        navController.navigate(Screen.Register.route)
                        showAuthDialog = false
                    }
                ) {
                    Text("Registrarse", color = Color(0xFF1976D2)) // Azul para el texto
                }
            }
        )
    }

    fun uriToFile(uri: Uri, context: Context, fileName: String): File? {
        val file = File(context.cacheDir, fileName)
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        val maxSizeInBytes = 2 * 1024 * 1024 // 2 MB
        if (file.length() > maxSizeInBytes) {
            file.delete()
            errorMessage = "El logo es demasiado grande. El tamaño máximo permitido es 2 MB."
            return null
        }

        return file
    }

    fun submitApp() {
        if (token == null) {
            showAuthDialog = true
            return
        }

        if (name.isBlank() || description.isBlank() || category.isBlank() || logoUri == null) {
            errorMessage = "Por favor, completa todos los campos"
            return
        }

        isLoading = true
        errorMessage = null
        coroutineScope.launch {
            try {
                val logoFile = uriToFile(logoUri!!, context, "logo_${System.currentTimeMillis()}.png")
                if (logoFile == null) {
                    isLoading = false
                    return@launch
                }

                val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
                val categoryBody = category.toRequestBody("text/plain".toMediaTypeOrNull())

                val logoPart = MultipartBody.Part.createFormData(
                    "logo",
                    logoFile.name,
                    logoFile.asRequestBody("image/*".toMediaTypeOrNull())
                )

                val response = apiService.uploadApp(
                    name = nameBody,
                    description = descriptionBody,
                    category = categoryBody,
                    logo = logoPart
                )

                if (response.isSuccessful) {
                    val uploadResponse = response.body()
                    val message = if (uploadResponse?.needs_payment == true) {
                        "App creada exitosamente. Requiere pago para continuar."
                    } else {
                        "App creada exitosamente. ID: ${uploadResponse?.app_id}"
                    }
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()

                    name = ""
                    description = ""
                    category = ""
                    logoUri = null
                    tags = emptyList()
                    newTag = ""

                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage = "Error al crear la app: ${response.message()} - $errorBody"
                }

                logoFile.delete()
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            logoUri = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Nueva App", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2), // Azul principal para la barra superior
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFE3F2FD) // Fondo azul claro
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                errorMessage?.let { message ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFCDD2) // Mantener rojo claro para errores
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .shadow(4.dp, RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = message,
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la App") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1976D2), // Azul principal
                        focusedLabelColor = Color(0xFF1976D2),
                        unfocusedBorderColor = Color.Gray,
                        unfocusedLabelColor = Color.Gray
                    )
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(8.dp),
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1976D2),
                        focusedLabelColor = Color(0xFF1976D2),
                        unfocusedBorderColor = Color.Gray,
                        unfocusedLabelColor = Color.Gray
                    )
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                ExposedDropdownMenuBox(
                    expanded = isCategoryDropdownExpanded,
                    onExpandedChange = { isCategoryDropdownExpanded = !isCategoryDropdownExpanded },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        label = { Text("Categoría") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Expandir",
                                tint = if (isCategoryDropdownExpanded) Color(0xFF1976D2) else Color.Gray
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1976D2),
                            focusedLabelColor = Color(0xFF1976D2),
                            unfocusedBorderColor = Color.Gray,
                            unfocusedLabelColor = Color.Gray
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = isCategoryDropdownExpanded,
                        onDismissRequest = { isCategoryDropdownExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    isCategoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newTag,
                            onValueChange = { newTag = it },
                            label = { Text("Agregar Etiqueta") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1976D2),
                                focusedLabelColor = Color(0xFF1976D2),
                                unfocusedBorderColor = Color.Gray,
                                unfocusedLabelColor = Color.Gray
                            )
                        )
                        IconButton(
                            onClick = {
                                if (newTag.isNotBlank() && tags.size < 5) {
                                    tags = tags + newTag
                                    newTag = ""
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF1976D2), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Agregar Etiqueta",
                                tint = Color.White
                            )
                        }
                    }
                    if (tags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            tags.forEach { tag ->
                                Chip(
                                    label = tag,
                                    onDelete = { tags = tags - tag }
                                )
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (logoUri != null) {
                        AsyncImage(
                            model = logoUri,
                            contentDescription = "Vista previa del logo",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = { logoUri = null },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                        ) {
                            Text("Eliminar Logo")
                        }
                    } else {
                        Button(
                            onClick = {
                                permissionLauncher.launch(permissionToRequest)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1976D2),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Seleccionar Logo", fontSize = 16.sp)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF1976D2)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF1976D2)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancelar", fontSize = 16.sp)
                }
                Button(
                    onClick = { submitApp() },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .shadow(4.dp, RoundedCornerShape(8.dp)),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Agregar App", fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun Chip(label: String, onDelete: () -> Unit) {
    Surface(
        modifier = Modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(16.dp)),
        color = Color(0xFF1976D2).copy(alpha = 0.1f), // Fondo azul claro para chips
        border = BorderStroke(1.dp, Color(0xFF1976D2))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = Color(0xFF1976D2), // Texto azul
                fontSize = 12.sp,
                modifier = Modifier.padding(end = 4.dp)
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Eliminar etiqueta",
                tint = Color(0xFF1976D2),
                modifier = Modifier
                    .size(16.dp)
                    .clickable { onDelete() }
            )
        }
    }
}