package com.example.instagram_clone_clean_architecture.app.data.data_source

import android.net.Uri
import com.example.instagram_clone_clean_architecture.app.domain.data_source.CacheDataSource
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either

class CacheDataSourceImpl : CacheDataSource {

    private var userSelectedImageUri: Uri? = null

    private var loginUser: UserDomainModel? = null

    private var authToken: String? = null

    override fun getAuthToken(): String? = authToken

    override fun cacheAuthToken(token: String) {
        authToken = token
    }

    override suspend fun cacheLoginUserProfile(userProfile: UserDomainModel?): Either<Unit, Failure> {
        loginUser = userProfile
        return Either.Success(Unit)
    }

    override suspend fun getLoginUser(): Either<UserDomainModel, Failure> {
        return if (loginUser == null) {
            Either.Failure(Failure.CacheNotFound)
        } else {
            Either.Success(loginUser!!)
        }
    }

    override suspend fun cacheUserSelectedImageUri(uri: Uri): Either<Unit, Failure> {
        userSelectedImageUri =  uri
        return Either.Success(Unit)
    }

    override suspend fun consumeCachedSelectedImageUri(): Either<Uri, Failure> {
        return if (userSelectedImageUri == null) {
            Either.Failure(Failure.CacheNotFound)
        } else {
            val tmp = userSelectedImageUri
            userSelectedImageUri = null
            Either.Success(tmp!!)
        }

    }
}