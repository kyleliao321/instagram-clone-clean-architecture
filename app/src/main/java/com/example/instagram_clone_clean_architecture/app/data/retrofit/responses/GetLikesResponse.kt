package com.example.instagram_clone_clean_architecture.app.data.retrofit.responses

import com.example.instagram_clone_clean_architecture.app.data.model.UserProfileDataModel

data class GetLikesResponse(
    val likedUsers: List<UserProfileDataModel>
)