package com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase

import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.usercase.UseCase
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class GetLoginUserUseCase(
    private val profileRepository: ProfileRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<UserDomainModel, Unit>(defaultDispatcher) {

    override suspend fun run(params: Unit): Either<UserDomainModel, Failure> {
        var result: Either<UserDomainModel, Failure>? = null

        profileRepository.getLoginUserProfile().fold(
            onSucceed = { userProfile ->
                result = when (userProfile) {
                    null -> Either.Failure(Failure.LocalAccountNotFound)
                    else -> Either.Success(userProfile)
                }
            },
            onFail = { failure ->
                result = Either.Failure(failure)
            }
        )

        return result!!
    }
}