package com.example.instagram_clone_clean_architecture.app.data.retrofit.services

import com.example.instagram_clone_clean_architecture.app.data.retrofit.responses.GetPostResponse
import com.example.instagram_clone_clean_architecture.app.data.retrofit.responses.GetPostsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PostServices {
    @GET("/api/v1/posts/{postId}")
    suspend fun getPostAsync(
        @Path("postId") postId: String
    ): Response<GetPostResponse>

    @GET("/api/v1/posts/")
    suspend fun getPostsAsync(
        @Query("userId") userId: String
    ): Response<GetPostsResponse>
}