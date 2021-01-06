package com.example.instagram_clone_clean_architecture.feature.login.domain.repository

import com.example.instagram_clone_clean_architecture.app.domain.model.LoginCredentialDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either

interface LoginRepository {

    suspend fun userLogin(userName: String, password: String) : Either<LoginCredentialDomainModel, Failure>

    suspend fun userRegister(userName: String, password: String) : Either<Unit, Failure>

    suspend fun getLocalLoginUserName() : Either<String, Failure>

    suspend fun getLocalLoginUserPassword() : Either<String, Failure>

    suspend fun updateLocalLoginUserName(userName: String) : Either<Unit, Failure>

    suspend fun updateLocalLoginUserPassword(userPassword: String) : Either<Unit, Failure>

    suspend fun updateLocalAuthToken(token: String) : Either<Unit, Failure>

    suspend fun cacheLoginUserProfile(userProfile: UserDomainModel) : Either<Unit, Failure>

    suspend fun cacheAuthToken(token: String) : Either<Unit, Failure>
}