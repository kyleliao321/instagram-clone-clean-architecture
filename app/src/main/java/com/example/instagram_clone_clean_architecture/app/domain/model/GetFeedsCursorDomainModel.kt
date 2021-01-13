package com.example.instagram_clone_clean_architecture.app.domain.model

data class GetFeedsCursorDomainModel(
    val userId: String,
    val pageSize: Int,
    val previous: String?,
    val next: String?
)