package com.kev.apptest12.utils


import android.content.Context
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.kev.apptest12.Screen
import com.kev.apptest12.data.remote.ApiService
import com.kev.apptest12.data.remote.GoogleAuthRequest
import com.kev.apptest12.data.remote.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun handleSignInResult(
    task: Task<GoogleSignInAccount>,
    apiService: ApiService,
    context: Context,
    navController: NavHostController,
    coroutineScope: CoroutineScope,
    setErrorMessage: (String?) -> Unit,
    setGoogleLoading: (Boolean) -> Unit,
    popUpRoute: String // Nuevo parámetro para especificar la ruta a eliminar del stack
) {
    if (task.isSuccessful) {
        val account = task.result
        val idToken = account.idToken // Token de Google
        if (idToken != null) {
            // Envía el token a tu backend de Laravel para autenticar al usuario
            coroutineScope.launch {
                try {
                    // Envuelve el idToken en un objeto GoogleAuthRequest
                    val response = apiService.loginWithGoogle(GoogleAuthRequest(idToken))
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
                                popUpTo(popUpRoute) { inclusive = true }
                            }
                        } else {
                            setErrorMessage("Error: Respuesta del servidor vacía")
                        }
                    } else {
                        setErrorMessage("Error al iniciar sesión con Google: ${response.message()}")
                    }
                } catch (e: Exception) {
                    setErrorMessage("Error: ${e.message}")
                } finally {
                    setGoogleLoading(false) // Desactiva el estado de carga
                }
            }
        } else {
            setErrorMessage("Error: No se pudo obtener el token de Google")
            setGoogleLoading(false) // Desactiva el estado de carga
        }
    } else {
        setErrorMessage("Error al iniciar sesión con Google")
        setGoogleLoading(false) // Desactiva el estado de carga
    }
}