package com.example.instagram_clone_clean_architecture.app.domain.model

data class UserDomainModel(
    val id: Int,
    var name: String,
    var userName: String,
    var imageSrc: String? = null,
    var description: String? = null,
    var postNum: Int,
    var followingNum: Int,
    var followerNum: Int
)