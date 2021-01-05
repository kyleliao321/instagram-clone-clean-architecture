package com.example.instagram_clone_clean_architecture.app.data.model

data class UserProfileDataModel(
    val id: String,
    val userName: String,
    val alias: String,
    val description: String,
    val imageSrc: String,
    val postNum: Int,
    val followerNum: Int,
    val followingNum: Int
)