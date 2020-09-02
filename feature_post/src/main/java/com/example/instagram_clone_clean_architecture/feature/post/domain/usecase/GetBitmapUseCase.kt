package com.example.instagram_clone_clean_architecture.feature.post.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.example.instagram_clone_clean_architecture.feature.post.domain.repository.PostRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.usercase.UseCase
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class GetBitmapUseCase(
    private val postRepository: PostRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<Bitmap, GetBitmapUseCase.Param>(defaultDispatcher) {

    override suspend fun run(params: Param): Either<Bitmap, Failure> =
        postRepository.getBitmap(params.uri)

    data class Param(val uri: Uri)
}