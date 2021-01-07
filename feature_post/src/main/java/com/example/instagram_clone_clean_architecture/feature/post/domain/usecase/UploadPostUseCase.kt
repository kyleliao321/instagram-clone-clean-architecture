package com.example.instagram_clone_clean_architecture.feature.post.domain.usecase

import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.PostUploadDomainModel
import com.example.instagram_clone_clean_architecture.feature.post.domain.repository.PostRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.extension.getJpegByteArray
import com.example.library_base.domain.extension.resizeAndCrop
import com.example.library_base.domain.usercase.UseCase
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.util.*

class UploadPostUseCase(
    private val postRepository: PostRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<PostDomainModel, UploadPostUseCase.Param>(defaultDispatcher) {

    override suspend fun run(params: Param): Either<PostDomainModel, Failure> {
        val post = params.post

        if (!post.isPostReady) {
            return Either.Failure(Failure.PostNotComplete)
        }

        val getBitmapResult = postRepository.getBitmap(post.imageUri!!)

        if (getBitmapResult is Either.Failure) {
            return Either.Failure(getBitmapResult.b)
        }

        val bitmap = (getBitmapResult as Either.Success).a
        val resizedBitmap = bitmap.resizeAndCrop(400, 400)
        val byteArray = resizedBitmap.getJpegByteArray()
        val randomFileName = UUID.randomUUID().toString()
        val cacheFileResult = postRepository.cacheCompressedImageFile(randomFileName, byteArray)

        if (cacheFileResult is Either.Failure) {
            return Either.Failure(cacheFileResult.b)
        }

        val cachedFile = (cacheFileResult as Either.Success).a
        post.cachedImageFile = cachedFile
        post.date = Date()

        return postRepository.uploadPost(post).also {
            cachedFile.delete()
        }
    }

    data class Param(val post: PostUploadDomainModel)

}