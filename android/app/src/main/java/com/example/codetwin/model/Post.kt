package com.example.codetwin.model

data class Post(
    val id: Long? = null,
    val title: String,
    val content: String,
    val imageUrl: String? = null,
    val createdAt: String? = null,
    val user: User? = null,
    val likes: List<Like> = emptyList(),
    val comments: List<Comment> = emptyList()
)