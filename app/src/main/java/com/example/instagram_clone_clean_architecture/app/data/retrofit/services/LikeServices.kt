package com.example.instagram_clone_clean_architecture.app.data.retrofit.services

import com.example.instagram_clone_clean_architecture.app.data.retrofit.requests.LikePostRequest
import com.example.instagram_clone_clean_architecture.app.data.retrofit.responses.DislikePostResponse
import com.example.instagram_clone_clean_architecture.app.data.retrofit.responses.GetLikesResponse
import com.example.instagram_clone_clean_architecture.app.data.retrofit.responses.LikePostResponse
import retrofit2.Response
import retrofit2.http.*

interface LikeServices {
    @GET("/api/v1/likes/")
    suspend fun getLikes(
        @Query("postId") postId: String
    ): Response<GetLikesResponse>

    @POST("/api/v1/likes")
    suspend fun likePost(
        @Body body: LikePostRequest
    ): Response<LikePostResponse>

    @DELETE("/api/v1/likes/user/{userId}/post/{postId}")
    suspend fun dislikePost(
        @Path("userId") userId: String,
        @Path("postId") postId: String
    ): Response<DislikePostResponse>
}