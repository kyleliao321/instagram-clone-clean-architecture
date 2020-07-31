package com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase

import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.usercase.UseCase
import com.example.library_base.domain.utility.Either

class GetPostUseCase(
    private val profileRepository: ProfileRepository
) : UseCase<PostDomainModel, GetPostUseCase.Param>() {

    override suspend fun run(params: Param): Either<PostDomainModel, Failure> {
        var result: Either<PostDomainModel, Failure>? = null

        profileRepository.getPostByPostId(params.id).fold(
            onSucceed = { post ->
                if (post == null) result = Either.Failure(Failure.NullValue)
                else result = Either.Success(post)
            },
            onFail = { failure ->
                { result = Either.Failure(failure) }
            }
        )

        return result!!
    }

    data class Param(val id: Int)
}