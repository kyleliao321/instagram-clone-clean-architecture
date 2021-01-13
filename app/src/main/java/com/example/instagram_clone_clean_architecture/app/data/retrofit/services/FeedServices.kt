package com.example.instagram_clone_clean_architecture.app.data.retrofit.services

import com.example.instagram_clone_clean_architecture.app.data.retrofit.responses.GetFeedsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FeedServices {
    @GET("/api/v1/feeds/")
    suspend fun getFeeds(
        @Query("userId") userId: String,
        @Query("pageSize") pageSize: String,
        @Query("after") next: String?,
        @Query("before") previous: String?
    ): Response<GetFeedsResponse>
}