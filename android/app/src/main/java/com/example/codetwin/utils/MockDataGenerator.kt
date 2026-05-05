package com.example.codetwin.utils

import android.content.Context
import com.example.codetwin.api.RetrofitClient
import com.example.codetwin.model.ApiResponse
import com.example.codetwin.model.Post
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object MockDataGenerator {

    fun generateMockPosts(context: Context) {
        val mockPosts = listOf(
            Pair("Welcome to Codetwin!", "This is your first post. Codetwin is a platform for developers to share their journey."),
            Pair("Tips for Spring Boot", "Always use Constructor Injection for better testability and immutability."),
            Pair("Kotlin vs Java", "Kotlin's null safety is a game changer for Android development.")
        )

        val apiService = RetrofitClient.getClient(context)

        mockPosts.forEach { (title, content) ->
            val titleRB = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val contentRB = content.toRequestBody("text/plain".toMediaTypeOrNull())
            
            apiService.createPost(titleRB, contentRB, null).enqueue(object : Callback<ApiResponse<Post>> {
                override fun onResponse(call: Call<ApiResponse<Post>>, response: Response<ApiResponse<Post>>) {
                    // Silent success
                }

                override fun onFailure(call: Call<ApiResponse<Post>>, t: Throwable) {
                    // Silent failure
                }
            })
        }
    }
}
