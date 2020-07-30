package com.example.instagram_clone_clean_architecture.app.domain.model

data class UserDomainModel(
    val id: Int,
    val name: String,
    val description: String,
    val postNum: Int,
    val followingNum: Int,
    val followerNum: Int
)