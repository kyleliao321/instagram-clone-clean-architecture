package com.example.instagram_clone_clean_architecture.feature.post.data.repository

import android.graphics.Bitmap
import android.net.Uri
import com.example.instagram_clone_clean_architecture.app.data.model.PostUploadDataModel
import com.example.instagram_clone_clean_architecture.app.domain.data_source.LocalDataSource
import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.PostUploadDomainModel
import com.example.instagram_clone_clean_architecture.feature.post.domain.repository.PostRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.extension.getJpegByteArray
import com.example.library_base.domain.utility.Either

class PostRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : PostRepository {

    override suspend fun getLoginUserProfile(): Either<UserDomainModel, Failure> {
        var result: Either<UserDomainModel, Failure>? = null

        var userId: Int? = null

        localDataSource.getLocalLoginUserId().fold(
            onSucceed = { id -> userId = id},
            onFail = { failure -> result = Either.Failure(failure) }
        )

        userId?.let {
            remoteDataSource.getUserProfileById(it).fold(
                onSucceed = { userProfile ->
                    result = when (userProfile) {
                        null -> Either.Failure(Failure.LocalAccountNotFound)
                        else -> Either.Success(userProfile)
                    }
                },
                onFail = { failure -> result = Either.Failure(failure) }
            )
        }

        return result!!
    }

    override suspend fun getUserSelectedImage(): Either<Uri?, Failure> =
        localDataSource.consumeLoadedImage()

    override suspend fun uploadPostUseCase(postUploadDomainModel: PostUploadDomainModel): Either<PostDomainModel, Failure> {
        val imageUri = postUploadDomainModel.imageFile!!
        var bitmap: Bitmap? = null

        getBitmap(imageUri).fold(
            onSucceed = { bitmap = it },
            onFail = {}
        )

        return if (bitmap == null) {
            Either.Failure(Failure.PhotoGalleryServiceFail)
        } else {
            val imageByteArray = bitmap!!.getJpegByteArray()
            val dataModel = PostUploadDataModel.from(postUploadDomainModel, imageByteArray)
            remoteDataSource.uploadPost(dataModel)
        }
    }

    override suspend fun getBitmap(uri: Uri): Either<Bitmap, Failure> =
        localDataSource.getBitmap(uri)

}