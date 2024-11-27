package com.capstone.petpoint.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.petpoint.R
import com.capstone.petpoint.utils.RegisterRequest
import com.capstone.petpoint.utils.RegisterResponse
import com.capstone.petpoint.utils.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btn_register = findViewById<Button>(R.id.btn_sign_up)
        val et_name = findViewById<EditText>(R.id.tv_username)
        val et_email = findViewById<EditText>(R.id.tv_email)
        val et_pass = findViewById<EditText>(R.id.tv_password)
        val et_role = findViewById<EditText>(R.id.tv_role)

        btn_register.setOnClickListener {
            val username = et_name.text.toString()
            val email = et_email.text.toString()
            val password = et_pass.text.toString()
            val role = et_role.text.toString()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                val request = RegisterRequest(username, email, password, role )
                RetrofitInstance.api.registerUser(request)
                    .enqueue(object : Callback<RegisterResponse> {
                        override fun onResponse(
                            call: Call<RegisterResponse>,
                            response: Response<RegisterResponse>
                        ) {
                            if (response.isSuccessful) {
                                val token = response.body()?.token

                                // Simpan token di SharedPreferences
                                val sharedPreferences =
                                    getSharedPreferences("UserSession", MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("token", token)
                                editor.apply()

                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Register successful!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Pindah ke MainActivity
                                startActivity(
                                    Intent(
                                        this@RegisterActivity,
                                        LoginActivity::class.java
                                    )
                                )
                                finish()
                            } else {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Register failed!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            Toast.makeText(
                                this@RegisterActivity,
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