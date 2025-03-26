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
    popUpRoute: String
) {
    if (task.isSuccessful) {
        val account = task.result
        val idToken = account.idToken
        if (idToken != null) {
            coroutineScope.launch {
                try {
                    val response = apiService.loginWithGoogle(GoogleAuthRequest(idToken))
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse != null) {
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
                        setErrorMessage("Error del servidor: ${response.code()} - ${response.message()}")
                    }
                } catch (e: Exception) {
                    setErrorMessage("Excepción: ${e.message}")
                } finally {
                    setGoogleLoading(false)
                }
            }
        } else {
            setErrorMessage("Error: No se obtuvo el token de Google")
            setGoogleLoading(false)
        }
    } else {
        val exception = task.exception
        setErrorMessage("Error al iniciar sesión con Google: ${exception?.message ?: "Desconocido"}")
        setGoogleLoading(false)
    }
}