package com.example.instagram_clone_clean_architecture.app.data.retrofit.intercetors

import com.example.instagram_clone_clean_architecture.app.domain.data_source.CacheDataSource
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizeInterceptor(
    private val cacheDataSource: CacheDataSource
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = chain.request().let {
        val authToken = cacheDataSource.getAuthToken()

        val newRequest = it.newBuilder()
            .addHeader("Authorization", "Bearer $authToken")
            .build()

        chain.proceed(newRequest)
    }
}