package com.example.instagram_clone_clean_architecture.feature.post.domain.usecase

import com.example.instagram_clone_clean_architecture.feature.post.domain.repository.PostRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.usercase.UseCase
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.io.File

class GetUserSelectedImageUseCase(
    private val postRepository: PostRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<File?, Unit>(defaultDispatcher) {

    override suspend fun run(params: Unit): Either<File?, Failure> =
        postRepository.getUserSelectedImage()

}