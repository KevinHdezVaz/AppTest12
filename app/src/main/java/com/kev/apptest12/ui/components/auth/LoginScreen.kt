package com.kev.apptest12.ui.components.auth

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kev.apptest12.Screen
import com.kev.apptest12.data.remote.ApiService
import com.kev.apptest12.data.remote.LoginRequest
import com.kev.apptest12.data.remote.RetrofitClient
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.kev.apptest12.data.remote.GoogleAuthRequest
import com.kev.apptest12.utils.handleSignInResult
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(apiService: ApiService, context: Context, navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isGoogleLoading by remember { mutableStateOf(false) }
    val localContext = LocalContext.current

    // Configura Google Sign-In
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestIdToken("146415761706-ggdu14anrtef6mh4uvufpldtaftotss9.apps.googleusercontent.com") // Reemplaza con tu client ID
        .build()

    val googleSignInClient = GoogleSignIn.getClient(localContext, gso)

    val errorMessageState = remember { mutableStateOf<String?>(null) }
    var errorMessage by errorMessageState

    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        handleSignInResult(
            task,
            apiService,
            context,
            navController,
            coroutineScope,
            setErrorMessage = { message -> errorMessage = message },
            setGoogleLoading = { isGoogleLoading = it },
            popUpRoute = Screen.Login.route
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Iniciar Sesión",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2), // Azul principal
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mensaje de error
            errorMessage?.let { message ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFCDD2) // Fondo rojo claro para el mensaje de error
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = message,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp
                    )
                }
            }

            // Campo de email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF1976D2)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1976D2),
                    focusedLabelColor = Color(0xFF1976D2),
                    unfocusedBorderColor = Color.Gray,
                    unfocusedLabelColor = Color.Gray
                )
            )

            // Campo de contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFF1976D2)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1976D2),
                    focusedLabelColor = Color(0xFF1976D2),
                    unfocusedBorderColor = Color.Gray,
                    unfocusedLabelColor = Color.Gray
                )
            )

            // Botón de inicio de sesión tradicional
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Por favor, completa todos los campos"
                        return@Button
                    }

                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        errorMessage = "Por favor, ingresa un email válido"
                        return@Button
                    }

                    isLoading = true
                    errorMessage = null
                    coroutineScope.launch {
                        try {
                            val response = apiService.login(LoginRequest(email, password))
                            if (response.isSuccessful) {
                                val loginResponse = response.body()
                                if (loginResponse != null) {
                                    // Guardar el token y los datos del usuario
                                    val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                                    sharedPreferences.edit()
                                        .putString("access_token", loginResponse.access_token)
                                        .putString("user_name", loginResponse.user.name)
                                        .putString("user_email", loginResponse.user.email)
                                        .apply()
                                    RetrofitClient.saveToken(context, loginResponse.access_token)
                                    navController.navigate(Screen.Upload.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                } else {
                                    errorMessage = "Error: Respuesta del servidor vacía"
                                }
                            } else {
                                errorMessage = "Error al iniciar sesión: ${response.message()}"
                            }
                        } catch (e: Exception) {
                            errorMessage = "Error: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(vertical = 8.dp),
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
                    Text("Iniciar Sesión", fontSize = 16.sp)
                }
            }

            // Separador "O"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    color = Color.Gray
                )
                Text(
                    text = "O",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
                Divider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    color = Color.Gray
                )
            }

            // Botón de inicio de sesión con Google
            Button(
                onClick = {
                    isGoogleLoading = true
                    val signInIntent = googleSignInClient.signInIntent
                    signInLauncher.launch(signInIntent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(vertical = 8.dp),
                enabled = !isGoogleLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4285F4), // Color azul de Google
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isGoogleLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Ícono de Google (puedes usar una imagen personalizada o un ícono de Material)
                        Text(
                            text = "G",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Iniciar Sesión con Google",
                            fontSize = 16.sp
                        )
                    }
                }
            }

            // Botón para ir a la pantalla de registro
            TextButton(
                onClick = {
                    navController.navigate(Screen.Register.route)
                },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "¿No tienes cuenta? Regístrate",
                    color = Color(0xFF1976D2)
                )
            }
        }
    }
}