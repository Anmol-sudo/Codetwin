package com.example.codetwin.api

import com.example.codetwin.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

import retrofit2.http.*

interface ApiService {

    @POST("/api/users/login")
    fun loginUser(@Body request: LoginRequest): Call<ApiResponse<LoginResponse>>

    @POST("/api/users/register")
    fun registerUser(@Body request: RegisterRequest): Call<Void>

    @POST("/api/users/refresh")
    fun refreshToken(@Body request: Map<String, String>): Call<ApiResponse<LoginResponse>>

    @GET("/api/posts")
    fun getPosts(): Call<ApiResponse<List<Post>>>

    @GET("/api/posts/{id}")
    fun getPostById(@Path("id") id: Long): Call<ApiResponse<Post>>

    @POST("/api/posts")
    fun createPost(@Body post: Post): Call<ApiResponse<Post>>

    @POST("/api/posts/{id}/like")
    fun toggleLike(@Path("id") id: Long): Call<ApiResponse<Void>>

    @POST("/api/posts/{id}/comment")
    fun addComment(@Path("id") id: Long, @Body comment: Comment): Call<ApiResponse<Comment>>
}