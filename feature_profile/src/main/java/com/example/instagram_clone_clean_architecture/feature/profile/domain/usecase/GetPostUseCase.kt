package com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase

import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.usercase.UseCase
import com.example.library_base.domain.utility.Either

class GetPostUseCase(
    private val profileRepository: ProfileRepository
) : UseCase<PostDomainModel, GetPostUseCase.Param>() {

    override suspend fun run(params: Param): Either<PostDomainModel, Failure> =
        profileRepository.getPostByPostId(params.id)

    data class Param(val id: Int)
}