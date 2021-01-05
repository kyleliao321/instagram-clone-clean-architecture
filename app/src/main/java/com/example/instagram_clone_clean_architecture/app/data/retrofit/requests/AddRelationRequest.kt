package com.example.instagram_clone_clean_architecture.app.data.retrofit.requests

data class AddRelationRequest(
    val followerId: String,
    val followingId: String
)
