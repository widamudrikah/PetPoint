package com.dicoding.picodiploma.loginwithanimation.data

import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.retrofit.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.retrofit.ApiService
import com.dicoding.picodiploma.loginwithanimation.retrofit.StoryUploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
){
    suspend fun getStories() = apiService.getStories()

    suspend fun uploadStory(token: String, file: MultipartBody.Part, description: RequestBody) : StoryUploadResponse {
        val apiService = ApiConfig.getApiService(token)
        return apiService.uploadStory(file, description)
    }

    suspend fun getStoriesWithLocation() = apiService.getStoriesWithLocation()

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService, userPreference: UserPreference): StoryRepository {
            return instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference)
            }.also { instance = it }
        }
    }
}