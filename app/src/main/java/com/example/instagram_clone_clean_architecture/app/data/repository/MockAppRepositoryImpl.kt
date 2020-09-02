package com.example.instagram_clone_clean_architecture.app.data.repository

import android.net.Uri
import com.example.instagram_clone_clean_architecture.app.domain.data_source.CacheDataSource
import com.example.instagram_clone_clean_architecture.app.domain.data_source.LocalDataSource
import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.repository.AppRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either

class MockAppRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val cacheDataSource: CacheDataSource
) : AppRepository {

    override suspend fun cacheUserSelectedImage(imageUri: Uri): Either<Unit, Failure> =
        cacheDataSource.cacheUserSelectedImageUri(imageUri)

    override suspend fun getLocalLoginUserId(): Either<Int, Failure> =
        localDataSource.getLocalLoginUserId()

    override suspend fun getLocalLoginUserName(): Either<String, Failure> =
        localDataSource.getLocalLoginUserName()

    override suspend fun getLocalLoginUserPassword(): Either<String, Failure> =
        localDataSource.getLocalLoginUserPassword()

    override suspend fun login(userName: String, password: String): Either<UserDomainModel, Failure> =
        remoteDataSource.userLogin(userName, password)

}