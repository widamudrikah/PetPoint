package com.capstone.petpoint.utils

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class RegisterRequest(
    val name_user: String,
    val email_user: String,
    val password_user: String,
    val role: String,
)

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val token: String
)
data class LoginRequest(
    val email_user: String,
    val password_user: String,
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? // Biasanya server memberikan token untuk autentikasi
)

data class LogoutResponse(
    val status: String,
    val message: String
)

interface ApiService {
    @POST("/register") // Path endpoint (contoh saja, sesuaikan dengan API kamu)
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("login") // Sesuaikan dengan endpoint login API kamu
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("logout")
    fun logoutUser(@Header("Authorization") token: String): Call<LogoutResponse>
}