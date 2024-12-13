package com.dicoding.picodiploma.loginwithanimation.view.add

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityAddStoryBinding
import com.dicoding.picodiploma.loginwithanimation.retrofit.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.retrofit.StoryUploadResponse
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null

    private val viewModel: AddStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_story)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener{ uploadImage()}
    }

    private fun startGallery() {
        launchGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launchGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "ShowImage: ${imageFile.path}")
            val description = binding.description.text.toString()
            showLoading(true)

            viewModel.uploadStory(imageFile, description).observe(this) { result ->
                result.onSuccess { response ->
                    showToast(response.message)
                    showLoading(false)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)

                }
                result.onFailure { error ->
                    showToast(error.message ?: getString(R.string.upload_failed))
                    showLoading(false)
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

//    upload
//    private fun uploadImage() {
//        currentImageUri?.let { uri ->
//            val imageFile = uriToFile(uri, this)
//            Log.d("Image file", "showImage: ${imageFile.path}")
//            val description = "Ini adalah deskripsi"
//
//            showLoading(true)
//
////            memanggil ViewModel
//            viewModel.uploadStory(imageFile, description).observe(this) { result ->
//                result.onSuccess { response ->
//                    showToast(response.message)
//                    showLoading(false)
//                }
//                result.onFailure { error ->
//                    showToast(error.message ?: getString(R.string.upload_failed))
//                    showLoading(false)
//                }
//            }
//
////            val requstBody = description.toRequestBody("text/plain".toMediaType())
////            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
////            val multipartBody = MultipartBody.Part.createFormData(
////                "photo",
////                imageFile.name,
////                requestImageFile
////            )
//
////            lifecycleScope.launch {
////                try {
////                    val userPreference = UserPreference.getInstance(dataStore)
////                    val token = userPreference.getToken()
////
////                    if (token != null) {
////                        val apiService = ApiConfig.getApiService(token)
////                        val successResponse = apiService.uploadStory(multipartBody, requstBody)
////                        showToast(successResponse.message)
////                        showLoading(false)
////                    } else {
////                        showToast("Token tidak ditemukan")
////                    }
////                }catch (e: HttpException) {
////                    val errorBody = e.response()?.errorBody()?.string()
////                    val errorResponse = Gson().fromJson(errorBody, StoryUploadResponse::class.java)
////                    showToast(errorResponse.message)
////                    showLoading(false)
////                }
////            }
//
//        } ?: showToast(getString(R.string.empty_image_warning))
//    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}