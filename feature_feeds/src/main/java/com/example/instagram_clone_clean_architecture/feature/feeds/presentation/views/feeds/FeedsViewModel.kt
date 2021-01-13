package com.example.instagram_clone_clean_architecture.feature.feeds.presentation.views.feeds

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.feature.feeds.domain.GetFeedsUserCase
import com.example.library_base.presentation.viewmodel.BaseAction
import com.example.library_base.presentation.viewmodel.BaseViewModel
import com.example.library_base.presentation.viewmodel.BaseViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FeedsViewModel(
    private val args: FeedsFragmentArgs,
    private val getFeedsUserCase: GetFeedsUserCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : BaseViewModel<FeedsViewModel.ViewState, FeedsViewModel.Action>(ViewState()) {

    private val PAGE_SIZE = 10

    private fun loadFeeds() = viewModelScope.launch(defaultDispatcher) {
        sendAction(Action.StartFeedsLoading)
        val param = GetFeedsUserCase.Param(args.userId, PAGE_SIZE)
        getFeedsUserCase(param) {
            it.fold(
                onSucceed = { feeds ->
                    sendAction(Action.FeedsLoaded(feeds))
                }
            )
        }
    }

    override fun onLoadData() {
        loadFeeds()
    }

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