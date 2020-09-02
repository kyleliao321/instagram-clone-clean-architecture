package com.example.instagram_clone_clean_architecture.feature.post.domain.usecase

import com.example.instagram_clone_clean_architecture.app.domain.model.PostUploadDomainModel
import com.example.instagram_clone_clean_architecture.feature.post.domain.repository.PostRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.usercase.UseCase
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class UploadPostUseCase(
    private val postRepository: PostRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<Unit, UploadPostUseCase.Param>(defaultDispatcher) {

    override suspend fun run(params: Param): Either<Unit, Failure> {
        val post = params.post

        return if (post.isPostReady) {
            postRepository.uploadPostUseCase(params.post)
        } else {
            Either.Failure(Failure.PostNotComplete)
        }
    }

    data class Param(val post: PostUploadDomainModel)

}