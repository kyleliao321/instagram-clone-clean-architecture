package com.example.instagram_clone_clean_architecture.feature.login.data.repository

import com.example.instagram_clone_clean_architecture.app.domain.data_source.LocalDataSource
import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.login.domain.repository.LoginRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either

class MockLoginRepositoryImpl(
    private val localDataSource: LocalDataSource,
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

    override suspend fun updateLocalLoginUserId(userId: Int): Either<Unit, Failure> {
        return localDataSource.updateLocalLoginUserId(userId)
    }
}