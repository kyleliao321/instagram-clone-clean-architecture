package com.example.instagram_clone_clean_architecture.app.data.retrofit.services

import com.example.instagram_clone_clean_architecture.app.data.retrofit.responses.GetUserProfileResponse
import com.example.instagram_clone_clean_architecture.app.data.retrofit.responses.SearchUserProfilesResponse
import com.example.instagram_clone_clean_architecture.app.data.retrofit.responses.UpdateUserProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part

interface UserServices {
    @GET("/api/v1/users/{userId}")
    suspend fun getUserProfileAsync(
        @Path("userId") userId: String
    ): Response<GetUserProfileResponse>

    @GET("/api/v1/users/")
    suspend fun searchUserProfilesAsync(
        @Query("userName") keyword: String
    ): Response<SearchUserProfilesResponse>

    @Multipart
    @PUT("/api/v1/users/{userId}")
    suspend fun updateUserProfileAsync(
        @Path("userId") userId: String,
        @Part("id") id: RequestBody,
        @Part("userName") userName: RequestBody? = null,
        @Part("alias") alias: RequestBody? = null,
        @Part("description") description: RequestBody? = null,
        @Part("userImage") userImage: RequestBody? = null
    ): Response<UpdateUserProfileResponse>
}