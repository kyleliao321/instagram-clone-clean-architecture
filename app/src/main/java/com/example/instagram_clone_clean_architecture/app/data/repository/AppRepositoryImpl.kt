package com.example.instagram_clone_clean_architecture.app.data.repository

import android.net.Uri
import com.example.instagram_clone_clean_architecture.app.domain.data_source.CacheDataSource
import com.example.instagram_clone_clean_architecture.app.domain.data_source.LocalDataSource
import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.repository.AppRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either

class AppRepositoryImpl(
    private val cacheDataSource: CacheDataSource
) : AppRepository {

    override suspend fun cacheUserSelectedImage(imageUri: Uri): Either<Unit, Failure> =
        cacheDataSource.cacheUserSelectedImageUri(imageUri)

    override suspend fun cacheLoginUser(userProfile: UserDomainModel): Either<Unit, Failure> {
        return cacheDataSource.cacheLoginUserProfile(userProfile)
    }

    override suspend fun getLoginUser(): Either<UserDomainModel, Failure> {
        return cacheDataSource.getLoginUser()
    }

}