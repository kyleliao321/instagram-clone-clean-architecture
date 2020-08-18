package com.example.instagram_clone_clean_architecture.feature.login.domain

import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either

interface LoginRepository {

    suspend fun userLogin(userName: String, password: String) : Either<UserDomainModel, Failure>

    suspend fun userRegister(userName: String, password: String) : Either<UserDomainModel, Failure>

}