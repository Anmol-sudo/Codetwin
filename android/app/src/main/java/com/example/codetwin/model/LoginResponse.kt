package com.example.codetwin.model

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: Long
)