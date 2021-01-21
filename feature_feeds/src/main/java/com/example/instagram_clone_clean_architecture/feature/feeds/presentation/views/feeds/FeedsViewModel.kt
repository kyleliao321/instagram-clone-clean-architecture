package com.example.instagram_clone_clean_architecture.feature.feeds.presentation.views.feeds

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.instagram_clone_clean_architecture.FeatureFeedsNavGraphDirections
import com.example.instagram_clone_clean_architecture.app.domain.model.FeedDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.feature.feeds.domain.repository.FeedRepository
import com.example.library_base.presentation.navigation.NavigationManager
import com.example.library_base.presentation.viewmodel.BaseAction
import com.example.library_base.presentation.viewmodel.BaseViewModel
import com.example.library_base.presentation.viewmodel.BaseViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class FeedsViewModel(
    private val args: FeedsFragmentArgs,
    private val feedRepository: FeedRepository,
    private val navManager: NavigationManager,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : BaseViewModel<FeedsViewModel.ViewState, FeedsViewModel.Action>(ViewState()) {

    private val PAGE_SIZE = 10

    fun navigateToProfile(userId: String) {
        val navDir = FeatureFeedsNavGraphDirections.featureProfileNavGraph(userId)
        navManager.onNavEvent(navDir)
    }

    fun getFeeds(): Flow<PagingData<FeedDomainModel>> {
        val feeds = feedRepository.getFeedsFlow(args.userId, PAGE_SIZE)
        feeds.cachedIn(viewModelScope)
        return feeds
    }

    override fun onLoadData() {}

    override fun onReduceState(action: Action): ViewState = when (action) {
        is Action.StartFeedsLoading -> state.copy(
            isFeedsLoading = true,
            feeds = null
        )
        is Action.FeedsLoaded -> state.copy(
            isFeedsLoading = false,
            feeds = action.feeds
        )
    }

    sealed class Action : BaseAction {
        object StartFeedsLoading : Action()
        class FeedsLoaded(val feeds: Flow<PagingData<PostDomainModel>>) : Action()
    }

    data class ViewState(
        val isFeedsLoading: Boolean = false,
        val feeds: Flow<PagingData<PostDomainModel>>? = null
    ) : BaseViewState
}