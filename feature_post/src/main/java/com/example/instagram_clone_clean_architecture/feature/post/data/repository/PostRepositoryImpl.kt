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
import java.io.File

class PostRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val cacheDataSource: CacheDataSource
) : PostRepository {

    override suspend fun cacheCompressedImageFile(fileName: String, byteArray: ByteArray): Either<File, Failure> {
        return cacheDataSource.cacheCompressedUploadImage(fileName, byteArray)
    }

    override suspend fun getLoginUserProfile(): Either<UserDomainModel, Failure> {
        return cacheDataSource.getLoginUser()
    }

    override suspend fun getUserSelectedImage(): Either<Uri?, Failure> =
        cacheDataSource.consumeCachedSelectedImageUri()

    // TODO: get bitmap and put into uploadPost should be done inside UseCase
    override suspend fun uploadPost(post: PostUploadDomainModel): Either<PostDomainModel, Failure> {
        return remoteDataSource.uploadPost(post)
    }

    override suspend fun getBitmap(uri: Uri): Either<Bitmap, Failure> =
        localDataSource.getBitmap(uri)

}