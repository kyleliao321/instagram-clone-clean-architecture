package com.example.instagram_clone_clean_architecture.feature.post.data.repository

import android.net.Uri
import com.example.instagram_clone_clean_architecture.app.domain.data_source.LocalDataSource
import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.post.domain.model.PostUploadDomainModel
import com.example.instagram_clone_clean_architecture.feature.post.domain.repository.PostRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import java.io.File

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

    override suspend fun uploadPostUseCase(postUploadDomainModel: PostUploadDomainModel): Either<Unit, Failure> {
        TODO("Not yet implemented")
    }

}