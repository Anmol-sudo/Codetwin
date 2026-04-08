package com.example.codetwin.model

data class Comment(
    val id: Long? = null,
    val content: String,
    val createdAt: String? = null,
    val user: User? = null
)