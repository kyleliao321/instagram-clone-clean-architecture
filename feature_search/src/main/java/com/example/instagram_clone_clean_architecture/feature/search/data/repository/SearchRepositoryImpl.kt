package com.example.instagram_clone_clean_architecture.feature.search.data.repository

import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.search.domain.repository.SearchRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either

class SearchRepositoryImpl(
    private val remoteDataSource: RemoteDataSource
) : SearchRepository {

    override suspend fun getUserProfileListByKeyword(keyword: String): Either<List<UserDomainModel>, Failure> {
        return remoteDataSource.getUserProfileListByUserName(keyword)
    }
}