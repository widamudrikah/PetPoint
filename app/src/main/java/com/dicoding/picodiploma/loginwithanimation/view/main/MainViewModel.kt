package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.retrofit.StoryResponse
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    private val _stories = MutableLiveData<StoryResponse>()
    val stories: LiveData<StoryResponse> get() = _stories

    fun fetchStories(context: Context) {
        viewModelScope.launch {
            try {
                val result = storyRepository.getStories()
                _stories.postValue(result)
            } catch (e: Exception) {
                Toast.makeText(context, "error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}