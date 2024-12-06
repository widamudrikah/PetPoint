package com.capstone.petpoint.utils

import com.capstone.petpoint.response.ListEmergencyUserResponse
import com.capstone.petpoint.response.PostEmergencyResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

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
    val data: LoginData? // Biasanya server memberikan token untuk autentikasi
)

data class LoginData(
    val token: String,
    val dashboard: String
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

    @Multipart
    @POST("emergency/create")
    suspend fun uploadEmergency(
        @Header("Authorization") token: String,
        @Part pic_pet: MultipartBody.Part,
        @Part("pet_location") location: RequestBody,
        @Part("notes") notes: RequestBody,
        @Part("pet_category") petCategory : RequestBody,
    ): PostEmergencyResponse

    @GET("emergency/userEmergency")
    fun getEmergencyUser(
        @Header("Authorization") token: String
    ) : Call<ListEmergencyUserResponse>
}