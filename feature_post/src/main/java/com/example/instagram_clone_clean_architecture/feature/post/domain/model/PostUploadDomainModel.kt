package com.example.instagram_clone_clean_architecture.feature.post.domain.model

import java.io.File
import java.util.*

data class PostUploadDomainModel(
    val imageFile: File,
    val description: String? = null,
    val location: String? = null,
    val date: Date,
    val belongUserId: Int
) {
}