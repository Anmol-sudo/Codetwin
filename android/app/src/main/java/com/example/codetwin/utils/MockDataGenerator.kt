package com.example.codetwin.utils

import android.content.Context
import com.example.codetwin.api.RetrofitClient
import com.example.codetwin.model.ApiResponse
import com.example.codetwin.model.Post
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object MockDataGenerator {

    fun generateMockPosts(context: Context) {
        val mockPosts = listOf(
            Post(title = "Welcome to Codetwin!", content = "This is your first post. Codetwin is a platform for developers to share their journey."),
            Post(title = "Tips for Spring Boot", content = "Always use Constructor Injection for better testability and immutability."),
            Post(title = "Kotlin vs Java", content = "Kotlin's null safety is a game changer for Android development.")
        )

        val apiService = RetrofitClient.getClient(context)

        mockPosts.forEach { post ->
            apiService.createPost(post).enqueue(object : Callback<ApiResponse<Post>> {
                override fun onResponse(call: Call<ApiResponse<Post>>, response: Response<ApiResponse<Post>>) {
                    // Silent success or log it
                }

                override fun onFailure(call: Call<ApiResponse<Post>>, t: Throwable) {
                    // Silent failure
                }
            })
        }
    }
}
