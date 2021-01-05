package com.example.instagram_clone_clean_architecture.app.data.retrofit.responses

import com.example.instagram_clone_clean_architecture.app.data.model.UserProfileDataModel

data class GetFollowingsResponse(
    val followings: List<UserProfileDataModel>
)
