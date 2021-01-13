package com.example.instagram_clone_clean_architecture.feature.feeds.domain

import androidx.paging.PagingData
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.feature.feeds.domain.repository.FeedRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.usercase.UseCase
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class GetFeedsUserCase(
    private val feedsRepository: FeedRepository,
    defaultDispatcher: CoroutineDispatcher
) : UseCase<Flow<PagingData<PostDomainModel>>, GetFeedsUserCase.Param>(defaultDispatcher) {

    override suspend fun run(params: Param): Either<Flow<PagingData<PostDomainModel>>, Failure> {
        val feedsFlow = feedsRepository.getFeedsFlow(params.userId, params.pageSize)
        return Either.Success(feedsFlow)
    }

    data class Param(val userId: String, val pageSize: Int)
}