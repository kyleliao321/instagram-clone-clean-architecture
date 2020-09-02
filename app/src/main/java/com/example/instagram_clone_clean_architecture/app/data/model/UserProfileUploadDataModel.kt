package com.example.instagram_clone_clean_architecture.app.data.model

import android.graphics.Bitmap
import com.example.instagram_clone_clean_architecture.app.domain.model.UserProfileUploadDomainModel
import com.example.library_base.domain.extension.getJpegByteArray

data class UserProfileUploadDataModel(
    val image: ByteArray?,
    val id: Int,
    val name: String,
    val userName: String,
    val description: String
) {
    companion object {
        fun from(domainModel: UserProfileUploadDomainModel, image: Bitmap?): UserProfileUploadDataModel {
            val imageByteArray = image?.getJpegByteArray()

            return UserProfileUploadDataModel(
                image = imageByteArray,
                id = domainModel.id,
                name = domainModel.name,
                userName = domainModel.userName,
                description = domainModel.description
            )
        }
    }
}