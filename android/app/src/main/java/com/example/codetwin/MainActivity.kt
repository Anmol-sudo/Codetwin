package com.example.codetwin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("auth", MODE_PRIVATE)
        val token = sharedPref.getString("accessToken", null)

        if (token != null) {
            // User already logged in → go to Home
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } else {
            // First time user → show welcome screen
            setContentView(R.layout.activity_main)

            val btnGetStarted = findViewById<MaterialButton>(R.id.btnGetStarted)

            btnGetStarted.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }
}