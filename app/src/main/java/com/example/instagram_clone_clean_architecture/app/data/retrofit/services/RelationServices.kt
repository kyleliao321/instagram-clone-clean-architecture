package com.example.instagram_clone_clean_architecture.app.data.retrofit.services

import com.example.instagram_clone_clean_architecture.app.data.retrofit.requests.AddRelationRequest
import com.example.instagram_clone_clean_architecture.app.data.retrofit.responses.AddRelationResponse
import com.example.instagram_clone_clean_architecture.app.data.retrofit.responses.GetFollowersResponse
import com.example.instagram_clone_clean_architecture.app.data.retrofit.responses.GetFollowingsResponse
import com.example.instagram_clone_clean_architecture.app.data.retrofit.responses.RemoveRelationResponse
import retrofit2.Response
import retrofit2.http.*

interface RelationServices {
    @GET("/api/v1/relations/followers/{userId}")
    suspend fun getFollowers(
        @Path("userId") userId: String
    ): Response<GetFollowersResponse>

    @GET("/api/v1/relations/followings/{userId}")
    suspend fun getFollowings(
        @Path("userId") userId: String
    ): Response<GetFollowingsResponse>

    @POST("/api/v1/relations/")
    suspend fun addRelation(
        @Body addRelationRequest: AddRelationRequest
    ): Response<AddRelationResponse>

    @DELETE("/api/v1/relations/follower/{followerId}/following/{followingId}")
    suspend fun removeRelation(
        @Path("followerId") followerId: String,
        @Path("followingId") followingId: String
    ): Response<RemoveRelationResponse>
}