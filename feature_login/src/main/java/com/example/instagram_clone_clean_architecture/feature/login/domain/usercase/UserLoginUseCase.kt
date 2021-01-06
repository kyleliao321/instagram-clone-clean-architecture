package com.example.instagram_clone_clean_architecture.feature.login.domain.usercase

import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.login.domain.repository.LoginRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.usercase.UseCase
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

// TODO: caching should be done in repository layer
class UserLoginUseCase(
    private val loginRepository: LoginRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<UserDomainModel, UserLoginUseCase.Param>(defaultDispatcher) {

    override suspend fun run(params: Param): Either<UserDomainModel, Failure> {
        val result = loginRepository.userLogin(params.userName, params.password)

        return when (result) {
            is Either.Failure -> result
            is Either.Success -> {
                val credential = result.a
                loginRepository.updateLocalLoginUserName(params.userName)
                loginRepository.updateLocalLoginUserPassword(params.password)
                loginRepository.updateLocalAuthToken(credential.jwt)
                loginRepository.cacheAuthToken(credential.jwt)
                loginRepository.cacheLoginUserProfile(credential.userProfile)
                Either.Success(credential.userProfile)
            }
        }
    }

    data class Param(val userName: String, val password: String)
}