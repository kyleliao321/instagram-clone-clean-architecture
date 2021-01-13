package com.example.instagram_clone_clean_architecture.app.domain.model

import com.example.instagram_clone_clean_architecture.BuildConfig
import com.example.instagram_clone_clean_architecture.app.data.model.FeedDataModel

data class FeedDomainModel(
    val userId: String,
    val userName: String,
    val userImage: String?,
    val postId: String,
    val location: String,
    val description: String,
    val timestamp: String,
    val postImage: String
) {
    companion object {
        fun from(dataModel: FeedDataModel): FeedDomainModel {
            val userImageSrc =
                if (dataModel.userImage == null) null else "${BuildConfig.GRADLE_API_STATIC_URL}/${dataModel.userImage}"
            val postImageSrc = "${BuildConfig.GRADLE_API_STATIC_URL}/${dataModel.postImage}"
            return FeedDomainModel(
                userId = dataModel.userId,
                userName = dataModel.userName,
                userImage = userImageSrc,
                postId = dataModel.postId,
                location = dataModel.location,
                description = dataModel.description,
                timestamp = dataModel.timestamp,
                postImage = postImageSrc
            )
        }
    }
}
