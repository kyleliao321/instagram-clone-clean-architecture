package com.example.instagram_clone_clean_architecture.app.data.retrofit.responses

import com.example.instagram_clone_clean_architecture.app.data.model.PostDataModel

data class GetPostsResponse(
    val posts: List<PostDataModel>
)
