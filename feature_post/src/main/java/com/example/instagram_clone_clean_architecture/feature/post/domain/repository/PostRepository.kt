package com.example.instagram_clone_clean_architecture.feature.post.domain.repository

import com.example.instagram_clone_clean_architecture.feature.post.domain.model.PostUploadDomainModel
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import java.io.File

interface PostRepository {

    suspend fun getUserSelectedImage(): Either<File, Failure>

    suspend fun uploadPostUseCase(postUploadDomainModel: PostUploadDomainModel): Either<Unit, Failure>

}