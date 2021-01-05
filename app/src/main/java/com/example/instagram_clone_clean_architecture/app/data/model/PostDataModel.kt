package com.example.instagram_clone_clean_architecture.app.data.model

data class PostDataModel(
    val id: String,
    val description: String,
    val location: String,
    val timestamp: String,
    val imageSrc: String,
    val postedUserId: String
)
