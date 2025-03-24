package com.kev.apptest12.data.remote

import com.kev.apptest12.data.model.App
import com.kev.apptest12.data.model.MessageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("apps")
    suspend fun getApps(): List<App>

    @POST("apps/{appId}/test")
    suspend fun testApp(
        @Path("appId") appId: String,
        @Body testRequest: TestRequest
    ): Response<MessageResponse>

    @GET("all-apps")
    suspend fun getAllApps(): List<App> // Nuevo método para todas las apps registradas

    @GET("apps/{id}")
    suspend fun getAppById(@Path("id") id: String): Response<App>

    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<LoginResponse>

    @GET("user")
    suspend fun getUser(): Response<UserResponse>

    @Multipart
    @POST("apps")
    suspend fun uploadApp(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("category") category: RequestBody,
        @Part logo: MultipartBody.Part
    ): Response<UploadResponse>

    @POST("apps/{appId}/publish")
    suspend fun publishApp(@Path("appId") appId: String): Response<MessageResponse>

    // Nuevo método para autenticación con Google
    @POST("auth/google")
    suspend fun loginWithGoogle(@Body googleAuthRequest: GoogleAuthRequest): Response<LoginResponse>
}

// Data classes
data class TestRequest(val tester_id: String)
data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String)
data class UserData(val name: String, val email: String)
data class LoginResponse(val access_token: String, val token_type: String, val user: UserData)
data class UserResponse(val user: UserData)
data class UploadResponse(val app_id: String, val needs_payment: Boolean)

// Nueva data class para la autenticación con Google
data class GoogleAuthRequest(val id_token: String)
