package com.example.instagram_clone_clean_architecture.feature.login.domain.usercase

import com.example.instagram_clone_clean_architecture.feature.login.domain.repository.LoginRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.usercase.UseCase
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class UserRegisterUseCase(
    private val loginRepository: LoginRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<Unit, UserRegisterUseCase.Param>(defaultDispatcher) {

    override suspend fun run(params: Param): Either<Unit, Failure> {
        if (params.userName == null || params.password == null) {
            return Either.Failure(Failure.FormDataNotComplete)
        }

        if (params.userName.isBlank() || params.password.isBlank()) {
            return Either.Failure(Failure.FormDataNotComplete)
        }

        return loginRepository.userRegister(params.userName, params.password)
    }

    data class Param(val userName: String?, val password: String?)
}