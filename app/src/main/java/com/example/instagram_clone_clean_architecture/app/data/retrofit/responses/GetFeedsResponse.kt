package com.example.instagram_clone_clean_architecture.app.data.retrofit.responses

import com.example.instagram_clone_clean_architecture.app.data.model.FeedDataModel

data class GetFeedsResponse(
    val feeds: List<FeedDataModel>
)
