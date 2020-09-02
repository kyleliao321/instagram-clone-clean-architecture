package com.example.instagram_clone_clean_architecture.feature.login.data.repository

import com.example.instagram_clone_clean_architecture.app.domain.data_source.CacheDataSource
import com.example.instagram_clone_clean_architecture.app.domain.data_source.LocalDataSource
import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.login.domain.repository.LoginRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either

class LoginRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val cacheDataSource: CacheDataSource,
    private val remoteDataSource: RemoteDataSource
) : LoginRepository {

    override suspend fun userLogin(
        userName: String,
        password: String
    ): Either<UserDomainModel, Failure> {
        return remoteDataSource.userLogin(userName, password)
    }

    override suspend fun userRegister(
        userName: String,
        password: String
    ): Either<UserDomainModel, Failure> {
        return remoteDataSource.userRegister(userName, password)
    }

    override suspend fun getLocalLoginUserName(): Either<String, Failure> {
        return localDataSource.getLocalLoginUserName()
    }

    override suspend fun getLocalLoginUserPassword(): Either<String, Failure> {
        return localDataSource.getLocalLoginUserPassword()
    }

    override suspend fun cacheLoginUserProfile(userProfile: UserDomainModel): Either<Unit, Failure> {
        return cacheDataSource.cacheLoginUserProfile(userProfile)
    }

    override suspend fun updateLocalLoginUserName(userName: String): Either<Unit, Failure> {
        return localDataSource.updateLocalLoginUserName(userName)
    }

    override suspend fun updateLocalLoginUserPassword(userPassword: String): Either<Unit, Failure> {
        return localDataSource.updateLocalLoginUserPassword(userPassword)
    }
}