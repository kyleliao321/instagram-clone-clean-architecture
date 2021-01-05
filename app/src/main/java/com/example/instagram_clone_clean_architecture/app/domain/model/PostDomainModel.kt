package com.example.instagram_clone_clean_architecture.app.domain.model

import com.example.instagram_clone_clean_architecture.app.data.model.PostDataModel
import java.util.*

data class PostDomainModel(
    val id: String,
    val imageSrc: String,
    val description: String? = null,
    val location: String? = null,
    val date: Date,
    val belongUserId: String
) {
    companion object {
        fun from(dataModel: PostDataModel): PostDomainModel {
            return PostDomainModel(
                id = dataModel.id,
                description = dataModel.description,
                date = Date(), // TODO: should parse from dataModel.timestamp
                location = dataModel.location,
                imageSrc = dataModel.imageSrc,
                belongUserId = dataModel.postedUserId
            )
        }
    }
}