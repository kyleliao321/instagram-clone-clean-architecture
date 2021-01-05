package com.example.instagram_clone_clean_architecture.app.data.retrofit.services

import com.example.instagram_clone_clean_architecture.app.data.retrofit.responses.GetUserProfileResponse
import com.example.instagram_clone_clean_architecture.app.data.retrofit.responses.SearchUserProfilesResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface UserServices {
    @GET("/api/v1/users/{userId}")
    suspend fun getUserProfileAsync(
        @Path("userId") userId: String
    ): Response<GetUserProfileResponse>

    @GET("/api/v1/users/")
    suspend fun searchUserProfilesAsync(
        @Query("userName") keyword: String
    ): Response<SearchUserProfilesResponse>
}