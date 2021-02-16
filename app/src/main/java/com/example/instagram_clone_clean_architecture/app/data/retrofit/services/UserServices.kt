package com.example.instagram_clone_clean_architecture.app.data.retrofit.services

import com.example.instagram_clone_clean_architecture.app.data.retrofit.responses.GetUserProfileResponse
import com.example.instagram_clone_clean_architecture.app.data.retrofit.responses.SearchUserProfilesResponse
import com.example.instagram_clone_clean_architecture.app.data.retrofit.responses.UpdateUserProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface UserServices {
    @GET("/server/api/v1/users/{userId}")
    suspend fun getUserProfileAsync(
        @Path("userId") userId: String
    ): Response<GetUserProfileResponse>

    @GET("/server/api/v1/users/")
    suspend fun searchUserProfilesAsync(
        @Query("userName") keyword: String
    ): Response<SearchUserProfilesResponse>

    @Multipart
    @PUT("/server/api/v1/users/{userId}")
    suspend fun updateUserProfileAsync(
        @Path("userId") userId: String,
        @Part("id") id: RequestBody,
        @Part("userName") userName: RequestBody? = null,
        @Part("alias") alias: RequestBody? = null,
        @Part("description") description: RequestBody? = null,
        @Part userImage: MultipartBody.Part? = null
    ): Response<UpdateUserProfileResponse>
}