package com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase

import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.usercase.UseCase
import com.example.library_base.domain.utility.Either

class UpdateUserProfileUseCase(
    private val profileRepository: ProfileRepository
) : UseCase<UserDomainModel, UpdateUserProfileUseCase.Param>() {

    override suspend fun run(params: Param): Either<UserDomainModel, Failure> =
        profileRepository.updateUserProfile(params.userProfile)

    data class Param(val userProfile: UserDomainModel)
}