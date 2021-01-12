package com.example.instagram_clone_clean_architecture.feature.search.domain.repository

import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either

interface SearchRepository {

    suspend fun getUserProfileListByKeyword(keyword: String): Either<List<UserDomainModel>, Failure>

}