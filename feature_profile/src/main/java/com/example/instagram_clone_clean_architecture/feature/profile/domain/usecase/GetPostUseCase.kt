package com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase

import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.usercase.UseCase
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class GetPostUseCase(
    private val profileRepository: ProfileRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<PostDomainModel, GetPostUseCase.Param>(defaultDispatcher) {

    override suspend fun run(params: Param): Either<PostDomainModel, Failure> {
        var result: Either<PostDomainModel, Failure>? = null

        profileRepository.getPostByPostId(params.id).fold(
            onSucceed = { post ->
                result = when (post) {
                    null -> Either.Failure(Failure.ServerError)
                    else -> Either.Success(post)
                }
            },
            onFail = { failure ->
                result = Either.Failure(failure)
            }
        )

        return result!!
    }

    data class Param(val id: String)
}