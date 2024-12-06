package com.capstone.petpoint.ui.myreport

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.petpoint.response.DataItem
import com.capstone.petpoint.response.ListEmergencyUserResponse
import com.capstone.petpoint.utils.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyReportViewModel: ViewModel() {
    private val _listEmergency = MutableLiveData<List<DataItem>>()
    val listEmergency: LiveData<List<DataItem>> = _listEmergency
    private var token: String? = null

    fun setContext(context: Context) {
        token = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            .getString("token", null)
        Log.d("EmergencyFragment", "Token retrieved: $token")

        if (token.isNullOrEmpty()) {
            Log.e("uploadEmergency", "Token Not Found")
        }
    }

    fun findEmergency() {
        if (token.isNullOrEmpty()) {
            Log.e("MyReportViewModel", "Token is null or empty")
            return
        }

        val client = RetrofitInstance.api.getEmergencyUser("Bearer $token")
        client.enqueue(object : Callback<ListEmergencyUserResponse> {
            override fun onResponse(
                call: Call<ListEmergencyUserResponse>,
                response: Response<ListEmergencyUserResponse>
            ) {
                if (response.isSuccessful) {
                    val emergencyRespon = response.body()
                    _listEmergency.value = emergencyRespon?.data ?: emptyList()
                } else {
                    Log.e(TAG, "Response error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ListEmergencyUserResponse>, t: Throwable) {
                Log.e(TAG, "Request failed: ${t.message}")
            }
        })
    }
}