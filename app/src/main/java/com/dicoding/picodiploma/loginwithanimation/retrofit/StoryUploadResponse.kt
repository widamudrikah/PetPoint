package com.dicoding.picodiploma.loginwithanimation.retrofit

import com.google.gson.annotations.SerializedName

data class StoryUploadResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)
