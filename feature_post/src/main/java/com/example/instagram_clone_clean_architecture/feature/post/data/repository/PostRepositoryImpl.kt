package com.example.instagram_clone_clean_architecture.feature.post.data.repository

import android.graphics.Bitmap
import android.net.Uri
import com.example.instagram_clone_clean_architecture.app.domain.data_source.CacheDataSource
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
    private val remoteDataSource: RemoteDataSource,
    private val cacheDataSource: CacheDataSource
) : PostRepository {

    override suspend fun getLoginUserProfile(): Either<UserDomainModel, Failure> {
        return cacheDataSource.getLoginUser()
    }

    override suspend fun getUserSelectedImage(): Either<Uri?, Failure> =
        cacheDataSource.consumeCachedSelectedImageUri()

    // TODO: get bitmap and put into uploadPost should be done inside UseCase
    override suspend fun uploadPostUseCase(postUploadDomainModel: PostUploadDomainModel): Either<PostDomainModel, Failure> {
        return if (postUploadDomainModel.imageFile !== null) {
            var bitmap: Bitmap? = null

            getBitmap(postUploadDomainModel.imageFile!!).fold(
                onSucceed = { bitmap = it }
            )

            postUploadDomainModel.imageByteArray = bitmap?.getJpegByteArray()

            remoteDataSource.uploadPost(postUploadDomainModel)
        } else {
            remoteDataSource.uploadPost(postUploadDomainModel)
        }
    }

    override suspend fun getBitmap(uri: Uri): Either<Bitmap, Failure> =
        localDataSource.getBitmap(uri)

}