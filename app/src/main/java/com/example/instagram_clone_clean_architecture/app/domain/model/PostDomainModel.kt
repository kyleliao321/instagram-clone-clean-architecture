package com.example.instagram_clone_clean_architecture.app.domain.model

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
            // TODO: should not show the hostname explicitly
            val fullImageSrc = "http://10.0.2.2:8080/static/${dataModel.imageSrc}"
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