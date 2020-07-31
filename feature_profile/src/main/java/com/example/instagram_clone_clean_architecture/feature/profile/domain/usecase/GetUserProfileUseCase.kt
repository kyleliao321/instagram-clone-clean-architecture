package com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase

import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.usercase.UseCase
import com.example.library_base.domain.utility.Either

class GetUserProfileUseCase(
    private val profileRepository: ProfileRepository
): UseCase<UserDomainModel, GetUserProfileUseCase.Param>() {

    override suspend fun run(params: Param): Either<UserDomainModel, Failure> {
        var result: Either<UserDomainModel, Failure>? = null

        profileRepository.getUserProfileById(params.id).fold(
            onSucceed = { userProfile ->
                if (userProfile == null) {
                    result = Either.Failure(Failure.NullValue)
                } else {
                    result = Either.Success(userProfile)
                }
            },
            onFail = { failure ->
                { result = Either.Failure(Failure.NullValue) }
            }
        )

        return result!!
    }

    data class Param(val id: Int)

}