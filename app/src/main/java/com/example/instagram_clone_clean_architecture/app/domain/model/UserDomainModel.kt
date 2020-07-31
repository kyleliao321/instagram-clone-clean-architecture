package com.example.instagram_clone_clean_architecture.app.domain.model

data class UserDomainModel(
    val id: Int,
    val name: String,
    val userName: String,
    val imageSrc: String? = null,
    val description: String? = null,
    val postNum: Int,
    val followingNum: Int,
    val followerNum: Int
)