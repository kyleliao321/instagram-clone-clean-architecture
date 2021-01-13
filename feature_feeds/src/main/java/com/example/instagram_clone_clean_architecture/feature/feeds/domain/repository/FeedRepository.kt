package com.example.instagram_clone_clean_architecture.feature.feeds.domain.repository

import androidx.paging.PagingData
import com.example.instagram_clone_clean_architecture.app.domain.model.FeedDomainModel
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    fun getFeedsFlow(userId: String, pageSize: Int): Flow<PagingData<FeedDomainModel>>
}