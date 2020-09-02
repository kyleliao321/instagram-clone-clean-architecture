package com.example.instagram_clone_clean_architecture.feature.post.domain.model

import android.net.Uri
import java.util.*

data class PostUploadDomainModel(
    val imageFile: Uri? = null,
    val description: String? = null,
    val location: String? = null,
    val date: Date? = null,
    val belongUserId: Int? = null
) {

    val isPostReady get() =
        imageFile != null && description != null && location != null && date != null && belongUserId != null

}