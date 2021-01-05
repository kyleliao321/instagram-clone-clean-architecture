package com.example.instagram_clone_clean_architecture.app.data.retrofit.responses

import com.example.instagram_clone_clean_architecture.app.data.model.UserProfileDataModel

data class SearchUserProfilesResponse(
    val users: List<UserProfileDataModel>
)