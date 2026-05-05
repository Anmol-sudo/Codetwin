package com.example.codetwin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.example.codetwin.api.RetrofitClient
import com.example.codetwin.model.LoginRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        val email = findViewById<TextInputEditText>(R.id.etEmail)
        val password = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val tvRegisterLink = findViewById<android.widget.TextView>(R.id.tvRegisterLink)

        tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {

            val emailText = email.text.toString()
            val passwordText = password.text.toString()

            val request = LoginRequest(emailText, passwordText)

            RetrofitClient.getClient(this).loginUser(request)
                .enqueue(object : Callback<com.example.codetwin.model.ApiResponse<com.example.codetwin.model.LoginResponse>> {

                    override fun onResponse(
                        call: Call<com.example.codetwin.model.ApiResponse<com.example.codetwin.model.LoginResponse>>,
                        response: Response<com.example.codetwin.model.ApiResponse<com.example.codetwin.model.LoginResponse>>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            val apiResponse = response.body()!!
                            if (apiResponse.success) {
                                val data = apiResponse.data
                                val sessionManager = com.example.codetwin.utils.SessionManager(this@LoginActivity)
                                sessionManager.saveTokens(data.accessToken, data.refreshToken, data.userId)

                                val isNewUser = intent.getBooleanExtra("IS_NEW_USER", false)
                                if (isNewUser) {
                                    com.example.codetwin.utils.MockDataGenerator.generateMockPosts(this@LoginActivity)
                                }

                                Toast.makeText(this@LoginActivity, "Login Success!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this@LoginActivity, apiResponse.message, Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val errorMsg = try {
                                val errorBody = response.errorBody()?.string()
                                val apiResponse = com.google.gson.Gson().fromJson(errorBody, com.example.codetwin.model.ApiResponse::class.java)
                                apiResponse.message
                            } catch (e: Exception) {
                                "Invalid email or password"
                            }
                            Toast.makeText(this@LoginActivity, errorMsg, Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<com.example.codetwin.model.ApiResponse<com.example.codetwin.model.LoginResponse>>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}