package com.dicoding.picodiploma.loginwithanimation.view.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.retrofit.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.retrofit.ApiService
import com.dicoding.picodiploma.loginwithanimation.retrofit.StoryUploadResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryViewModel(
    private val userPreference: UserPreference,
    private val storyRepository: StoryRepository
): ViewModel() {
    fun uploadStory(imageFile: File, description: String): LiveData<Result<StoryUploadResponse>> {
        val result = MutableLiveData<Result<StoryUploadResponse>>()

        viewModelScope.launch {
            try {
                val token = userPreference.getToken()
                if (token != null) {
                    val requestDescription = description.toRequestBody("text/plain".toMediaType())
                    val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                    val multipartBody = MultipartBody.Part.createFormData(
                        "photo",
                        imageFile.name,
                        requestImageFile
                    )

                    val response = storyRepository.uploadStory(token, multipartBody, requestDescription)
                    result.postValue(Result.success(response))
                } else {
                    result.postValue(Result.failure(Exception("Token tidak ditemukan")))
                }
            } catch (e: Exception) {
                result.postValue(Result.failure(e))
            }
        }
        return result
    }

}