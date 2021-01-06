package com.example.instagram_clone_clean_architecture.app.data.model

import android.graphics.Bitmap
import android.util.Base64
import com.example.instagram_clone_clean_architecture.app.domain.model.UserProfileUploadDomainModel
import com.example.library_base.domain.extension.getJpegByteArray

data class UserProfileUploadDataModel(
    val id: String,
    val userName: String? = null,
    val alias: String? = null,
    val description: String? = null
)