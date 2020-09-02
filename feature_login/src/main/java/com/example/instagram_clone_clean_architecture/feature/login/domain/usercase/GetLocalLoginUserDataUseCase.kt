package com.example.instagram_clone_clean_architecture.feature.login.domain.usercase

import com.example.instagram_clone_clean_architecture.feature.login.domain.repository.LoginRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.usercase.UseCase
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class GetLocalLoginUserDataUseCase(
    private val loginRepository: LoginRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<GetLocalLoginUserDataUseCase.Result, Unit>(defaultDispatcher) {

    override suspend fun run(params: Unit): Either<Result, Failure> {
        var userName: String? = null
        var userPassword: String? = null

        loginRepository.getLocalLoginUserName().fold(onSucceed = { userName = it })
        loginRepository.getLocalLoginUserPassword().fold(onSucceed = { userPassword = it })

        return if (userName != null && userPassword != null) {
            Either.Success(Result(userName!!, userPassword!!))
        } else {
            Either.Failure(Failure.LocalAccountNotFound)
        }
    }

    data class Result(val userName: String, val userPassword: String)

}