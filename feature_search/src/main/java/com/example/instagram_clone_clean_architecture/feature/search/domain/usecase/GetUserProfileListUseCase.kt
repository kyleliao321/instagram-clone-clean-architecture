package com.example.instagram_clone_clean_architecture.feature.search.domain.usecase

import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.search.domain.repository.SearchRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.usercase.UseCase
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class GetUserProfileListUseCase(
    private val searchRepository: SearchRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<List<UserDomainModel>, GetUserProfileListUseCase.Param>(defaultDispatcher) {

    override suspend fun run(params: Param): Either<List<UserDomainModel>, Failure> {
        if (params.keyword == null || params.keyword.isBlank()) {
            return Either.Failure(Failure.FormDataNotComplete)
        }

        return searchRepository.getUserProfileListByKeyword(params.keyword)
    }

    data class Param(val keyword: String?)
}