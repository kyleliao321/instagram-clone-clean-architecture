package com.example.instagram_clone_clean_architecture.app.domain.model

import java.util.*

data class PostDomainModel(
    val id: Int,
    val imageSrc: String,
    val description: String?,
    val location: String?,
    val date: Date,
    val belongUserId: Int
)