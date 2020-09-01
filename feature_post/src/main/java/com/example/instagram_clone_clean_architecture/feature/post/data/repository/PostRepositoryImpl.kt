package com.example.instagram_clone_clean_architecture.feature.post.data.repository

import com.example.instagram_clone_clean_architecture.app.domain.data_source.LocalDataSource
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.post.domain.model.PostUploadDomainModel
import com.example.instagram_clone_clean_architecture.feature.post.domain.repository.PostRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import java.io.File

class PostRepositoryImpl(
    private val localDataSource: LocalDataSource
) : PostRepository {

    override suspend fun getLoginUserProfile(): Either<UserDomainModel, Failure> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserSelectedImage(): Either<File?, Failure> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadPostUseCase(postUploadDomainModel: PostUploadDomainModel): Either<Unit, Failure> {
        TODO("Not yet implemented")
    }

}