package com.example.codetwin

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.codetwin.api.RetrofitClient
import com.example.codetwin.model.ApiResponse
import com.example.codetwin.model.Post
import com.example.codetwin.model.UserProfile
import com.example.codetwin.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvAvatar: TextView
    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPostCount: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initViews()
        loadProfile()
    }

    private fun initViews() {
        tvAvatar = findViewById(R.id.tvProfileAvatar)
        tvUsername = findViewById(R.id.tvProfileUsername)
        tvEmail = findViewById(R.id.tvProfileEmail)
        tvPostCount = findViewById(R.id.tvPostCount)
        recyclerView = findViewById(R.id.recyclerUserPosts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        sessionManager = SessionManager(this)
    }

    private fun loadProfile() {
        val userId = intent.getLongExtra("USER_ID", sessionManager.getUserId())
        
        RetrofitClient.getClient(this).getUserProfile(userId).enqueue(object : Callback<ApiResponse<UserProfile>> {
            override fun onResponse(call: Call<ApiResponse<UserProfile>>, response: Response<ApiResponse<UserProfile>>) {
                if (response.isSuccessful && response.body() != null) {
                    val profile = response.body()!!.data
                    updateUI(profile)
                }
            }
            override fun onFailure(call: Call<ApiResponse<UserProfile>>, t: Throwable) {
                Toast.makeText(this@ProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(profile: UserProfile) {
        tvUsername.text = profile.username
        tvEmail.text = profile.email
        tvPostCount.text = profile.postCount.toString()

        val firstChar = profile.username.firstOrNull()?.uppercaseChar() ?: '?'
        tvAvatar.text = firstChar.toString()
        val bg = tvAvatar.background as? GradientDrawable
        val colors = intArrayOf(
            Color.parseColor("#FF5722"), Color.parseColor("#E91E63"),
            Color.parseColor("#9C27B0"), Color.parseColor("#673AB7"),
            Color.parseColor("#3F51B5"), Color.parseColor("#2196F3"),
            Color.parseColor("#009688"), Color.parseColor("#4CAF50")
        )
        bg?.setColor(colors[profile.username.hashCode().coerceAtLeast(0) % colors.size])
    }
}