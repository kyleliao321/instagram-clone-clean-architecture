package com.example.instagram_clone_clean_architecture.app.data.model

import com.example.instagram_clone_clean_architecture.app.domain.model.PostUploadDomainModel
import java.util.*

data class PostUploadDataModel(
    val image: ByteArray,
    val location: String? = null,
    val date: Date,
    val belongUser: Int,
    val description: String? = null
) {
    companion object {
        fun from(domainModel: PostUploadDomainModel, imageByteArray: ByteArray): PostUploadDataModel {

            if (!domainModel.isPostReady) {
                throw IllegalArgumentException("$domainModel is not complete")
            }

            return  PostUploadDataModel(
                    image = imageByteArray,
                    location = domainModel.location,
                    date = domainModel.date!!,
                    belongUser = domainModel.belongUserId!!,
                    description = domainModel.description
                )
        }
    }
}