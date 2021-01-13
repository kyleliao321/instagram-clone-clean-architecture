package com.example.instagram_clone_clean_architecture.app.data.model

data class FeedDataModel(
    val userId: String,
    val userName: String,
    val userImage: String?,
    val postId: String,
    val location: String,
    val description: String,
    val timestamp: String,
    val postImage: String
)
