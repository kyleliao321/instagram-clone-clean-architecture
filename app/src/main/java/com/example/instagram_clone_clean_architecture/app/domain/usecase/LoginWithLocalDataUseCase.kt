package com.example.instagram_clone_clean_architecture.app.domain.usecase

import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.repository.AppRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.usercase.UseCase
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class LoginWithLocalDataUseCase(
    private val appRepository: AppRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<UserDomainModel, Unit>(defaultDispatcher) {

    override suspend fun run(params: Unit): Either<UserDomainModel, Failure> {
        var userName: String? = null
        var userPassword: String? = null

        appRepository.getLocalLoginUserName().fold(onSucceed = {userName = it})
        appRepository.getLocalLoginUserPassword().fold(onSucceed = {userPassword = it})

        return if (userName != null && userPassword != null) {
            appRepository.login(userName!!, userPassword!!)
        } else {
            Either.Failure(Failure.LocalAccountNotFound)
        }
    }
}