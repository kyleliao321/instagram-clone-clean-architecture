package com.example.instagram_clone_clean_architecture.feature.post.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.post.domain.model.PostUploadDomainModel
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import java.io.File

interface PostRepository {

    suspend fun getLoginUserProfile(): Either<UserDomainModel, Failure>

    suspend fun getUserSelectedImage(): Either<Uri?, Failure>

    suspend fun uploadPostUseCase(postUploadDomainModel: PostUploadDomainModel): Either<Unit, Failure>

    suspend fun getBitmap(uri: Uri) : Either<Bitmap, Failure>

}