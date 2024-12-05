package com.capstone.petpoint.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.petpoint.MainActivity
import com.capstone.petpoint.R
import com.capstone.petpoint.utils.LoginRequest
import com.capstone.petpoint.utils.LoginResponse
import com.capstone.petpoint.utils.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btn_login = findViewById<Button>(R.id.btn_login)
        val et_pass = findViewById<EditText>(R.id.tv_pass_login)
        val et_email = findViewById<EditText>(R.id.tv_email_login)

        btn_login.setOnClickListener {
            val email = et_email.text.toString()
            val password = et_pass.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val request = LoginRequest(email, password)
                RetrofitInstance.api.loginUser(request).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful) {

                            val loginResponse = response.body()
                            Log.d("LoginActivity", "Response body: $loginResponse")

                            val token = response.body()?.data?.token

                            // Simpan token di SharedPreferences
                            val sharedPreferences =
                                getSharedPreferences("UserSession", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("token", token)
                            editor.apply()

                            Toast.makeText(
                                this@LoginActivity,
                                "Login successful!",
                                Toast.LENGTH_SHORT
                            ).show()
                            // Pindah ke MainActivity
                            startActivity(
                                Intent(
                                this@LoginActivity,
                                MainActivity::class.java)
                            )
                            finish()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Invalid email or password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            } else {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}