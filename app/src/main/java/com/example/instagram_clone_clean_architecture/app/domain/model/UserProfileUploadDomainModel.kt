package com.example.instagram_clone_clean_architecture.app.domain.model

import android.net.Uri
import java.io.File

data class UserProfileUploadDomainModel(
    val imageUri: Uri?,
    val id: String,
    val name: String,
    val userName: String,
    val description: String,
    var imageByteArray: ByteArray? = null,
    var cachedImageFile: File? = null
) {
    companion object {
        fun from(domainModel: UserDomainModel, uri: Uri?): UserProfileUploadDomainModel {
            return UserProfileUploadDomainModel(
                imageUri = uri,
                id = domainModel.id,
                name = domainModel.name,
                userName = domainModel.userName,
                description = domainModel.description ?: ""
            )
        }
    }
}