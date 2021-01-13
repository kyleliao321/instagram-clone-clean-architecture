package com.example.instagram_clone_clean_architecture.feature.feeds.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.feature.feeds.data.page_source.FeedsPagingSource
import com.example.instagram_clone_clean_architecture.feature.feeds.domain.repository.FeedRepository
import kotlinx.coroutines.flow.Flow

class FeedRepositoryImpl(
    private val remoteDataSource: RemoteDataSource
) : FeedRepository {

    override fun getFeedsFlow(
        userId: String,
        pageSize: Int
    ): Flow<PagingData<PostDomainModel>> {
        val query = FeedsPagingSource.Query(userId, pageSize)
        return Pager(
            config = PagingConfig(
                pageSize = query.pageSize
            ),
            pagingSourceFactory = { FeedsPagingSource(remoteDataSource, query) }
        ).flow
    }
}