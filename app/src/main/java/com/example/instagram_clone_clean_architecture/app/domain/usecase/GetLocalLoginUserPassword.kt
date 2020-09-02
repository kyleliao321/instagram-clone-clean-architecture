package com.example.instagram_clone_clean_architecture.app.domain.usecase

import com.example.instagram_clone_clean_architecture.app.domain.repository.AppRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.usercase.UseCase
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class GetLocalLoginUserPassword(
    private val appRepository: AppRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<String, Unit>(defaultDispatcher) {

    override suspend fun run(params: Unit): Either<String, Failure> =
        appRepository.getLocalLoginUserPassword()

}