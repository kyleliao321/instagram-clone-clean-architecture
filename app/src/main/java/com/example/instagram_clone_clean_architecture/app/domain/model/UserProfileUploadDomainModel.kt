package com.example.instagram_clone_clean_architecture.app.domain.model

import android.net.Uri

data class UserProfileUploadDomainModel(
    val image: Uri?,
    val id: Int,
    val name: String,
    val userName: String,
    val description: String
) {
    companion object {
        fun from(domainModel: UserDomainModel, uri: Uri?):UserProfileUploadDomainModel {
            return UserProfileUploadDomainModel(
                image = uri,
                id = domainModel.id,
                name = domainModel.name,
                userName = domainModel.userName,
                description = domainModel.description ?: ""
            )
        }
    }
}