package com.example.instagram_clone_clean_architecture.app.data.retrofit.services

import com.example.instagram_clone_clean_architecture.app.data.retrofit.requests.AccountRequest
import com.example.instagram_clone_clean_architecture.app.data.retrofit.responses.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountServices {
    @POST("/api/v1/accounts/register")
    suspend fun registerNewAccountAsync(
        @Body accountInfo: AccountRequest
    ): Response<Unit>

    @POST("/api/v1/accounts/login")
    suspend fun loginAsync(
        @Body accountInfo: AccountRequest
    ): Response<LoginResponse>
}