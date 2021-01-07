package com.example.instagram_clone_clean_architecture.app.data.retrofit.services

import com.example.instagram_clone_clean_architecture.app.data.retrofit.responses.AddNewPostResponse
import com.example.instagram_clone_clean_architecture.app.data.retrofit.responses.GetPostResponse
import com.example.instagram_clone_clean_architecture.app.data.retrofit.responses.GetPostsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface PostServices {
    @GET("/api/v1/posts/{postId}")
    suspend fun getPostAsync(
        @Path("postId") postId: String
    ): Response<GetPostResponse>

    @GET("/api/v1/posts/")
    suspend fun getPostsAsync(
        @Query("userId") userId: String
    ): Response<GetPostsResponse>

    @Multipart
    @POST("/api/v1/posts/")
    suspend fun addNewPostAsync(
        @Part("description") description: RequestBody? = null,
        @Part("location") location: RequestBody? = null,
        @Part("timestamp") timestamp: RequestBody,
        @Part("postedUserId") userId: RequestBody,
        @Part postImage: MultipartBody.Part
    ): Response<AddNewPostResponse>
}