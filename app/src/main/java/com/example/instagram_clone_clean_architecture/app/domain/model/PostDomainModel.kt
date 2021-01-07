package com.example.instagram_clone_clean_architecture.app.domain.model

import com.example.instagram_clone_clean_architecture.BuildConfig
import com.example.instagram_clone_clean_architecture.app.data.model.PostDataModel

data class PostDomainModel(
    val id: String,
    val imageSrc: String,
    val description: String? = null,
    val location: String? = null,
    val date: String,
    val belongUserId: String
) {
    companion object {
        fun from(dataModel: PostDataModel): PostDomainModel {
            val fullImageSrc = "${BuildConfig.GRADLE_API_STATIC_URL}/${dataModel.imageSrc}"
            return PostDomainModel(
                id = dataModel.id,
                description = dataModel.description,
                date = dataModel.timestamp,
                location = dataModel.location,
                imageSrc = fullImageSrc,
                belongUserId = dataModel.postedUserId
            )
        }
    }
}