package com.example.instagram_clone_clean_architecture.feature.feeds.data.page_source

import androidx.paging.PagingSource
import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.library_base.domain.utility.Either
import java.net.SocketTimeoutException

class FeedsPagingSource(
    private val remoteDataSource: RemoteDataSource,
    private val query: Query
) : PagingSource<FeedsPagingSource.Cursor, PostDomainModel>() {

    override suspend fun load(params: LoadParams<Cursor>): LoadResult<Cursor, PostDomainModel> =
        when (val key = params.key) {
            is Cursor.NextCursor -> getNextFeeds(key, query)
            is Cursor.PreviousCursor -> getPreviousFeeds(key, query)
            null -> getLatestFeeds(query)
        }

    private suspend fun getLatestFeeds(query: Query): LoadResult<Cursor, PostDomainModel> {
        val getFeedsResult = remoteDataSource.getLatestFeeds(query.userId, query.pageSize)

        return when (getFeedsResult) {
            is Either.Failure -> LoadResult.Error(SocketTimeoutException())
            is Either.Success -> {
                val feeds = getFeedsResult.a
                LoadResult.Page(
                    data = feeds,
                    prevKey = null,
                    nextKey = if (feeds.isEmpty()) null else Cursor.NextCursor(feeds.last().date)
                )
            }
        }
    }

    private suspend fun getNextFeeds(
        key: Cursor.NextCursor,
        query: Query
    ): LoadResult<Cursor, PostDomainModel> {
        val getFeedsResult = remoteDataSource.getNextFeeds(query.userId, query.pageSize, key.after)

        return when (getFeedsResult) {
            is Either.Failure -> LoadResult.Error(SocketTimeoutException())
            is Either.Success -> {
                val feeds = getFeedsResult.a
                LoadResult.Page(
                    data = feeds,
                    prevKey = if (feeds.isEmpty()) Cursor.PreviousCursor(key.after) else Cursor.PreviousCursor(
                        feeds.first().date
                    ),
                    nextKey = if (feeds.isEmpty()) null else Cursor.NextCursor(feeds.last().date)
                )
            }
        }
    }

    private suspend fun getPreviousFeeds(
        key: Cursor.PreviousCursor,
        query: Query
    ): LoadResult<Cursor, PostDomainModel> {
        val getFeedsResult =
            remoteDataSource.getPreviousFeeds(query.userId, query.pageSize, key.before)

        return when (getFeedsResult) {
            is Either.Failure -> LoadResult.Error(SocketTimeoutException())
            is Either.Success -> {
                val feeds = getFeedsResult.a
                LoadResult.Page(
                    data = feeds,
                    prevKey = if (feeds.isEmpty()) null else Cursor.PreviousCursor(feeds.first().date),
                    nextKey = if (feeds.isEmpty()) Cursor.NextCursor(key.before) else Cursor.NextCursor(
                        feeds.last().date
                    )
                )
            }
        }
    }

    data class Query(val userId: String, val pageSize: Int)

    sealed class Cursor {
        data class PreviousCursor(val before: String) : Cursor()
        data class NextCursor(val after: String) : Cursor()
    }
}