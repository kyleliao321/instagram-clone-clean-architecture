package com.example.instagram_clone_clean_architecture.app.domain.model

import java.util.*

data class PostDomainModel(
    val id: String,
    val imageSrc: String,
    val description: String? = null,
    val location: String? = null,
    val date: Date,
    val belongUserId: String
)