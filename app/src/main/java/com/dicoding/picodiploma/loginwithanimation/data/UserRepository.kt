package com.dicoding.picodiploma.loginwithanimation.data

import android.content.Context
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.retrofit.ApiService
import com.dicoding.picodiploma.loginwithanimation.retrofit.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.retrofit.RegisterResponse
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    //fungsi untuk memanggil api register
    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    //fungsi untuk memnaggil api login dan menyimpan token
    suspend fun login(email: String, password: String): LoginResponse {
        val response = apiService.login(email, password)
        if (response.loginResult?.token != null) {
            //menyimpan token
            saveToken(response.loginResult.token)
        }
            return response
    }

    suspend fun saveToken(token: String) {
        userPreference.saveToken(token)
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}