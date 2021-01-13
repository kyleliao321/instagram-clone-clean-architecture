package com.example.instagram_clone_clean_architecture.app.data.retrofit.services

import com.example.instagram_clone_clean_architecture.app.data.retrofit.responses.GetFeedsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FeedServices {
    @GET("/api/v1/feeds/")
    suspend fun getLatestFeeds(
        @Query("userId") userId: String,
        @Query("pageSize") pageSize: String
    ): Response<GetFeedsResponse>

    @GET("/api/v1/feeds/")
    suspend fun getNextFeeds(
        @Query("userId") userId: String,
        @Query("pageSize") pageSize: String,
        @Query("after") breakPoint: String
    ): Response<GetFeedsResponse>

    @GET("/api/v1/feeds/")
    suspend fun getPreviousFeeds(
        @Query("userId") userId: String,
        @Query("pageSize") pageSize: String,
        @Query("before") breakPoint: String
    ): Response<GetFeedsResponse>
}