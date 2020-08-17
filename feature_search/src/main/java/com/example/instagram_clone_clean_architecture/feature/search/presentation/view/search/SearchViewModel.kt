package com.example.instagram_clone_clean_architecture.feature.search.presentation.view.search

import androidx.lifecycle.viewModelScope
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.search.domain.usecase.GetUserProfileListUseCase
import com.example.library_base.domain.exception.Failure
import com.example.library_base.presentation.viewmodel.BaseAction
import com.example.library_base.presentation.viewmodel.BaseViewModel
import com.example.library_base.presentation.viewmodel.BaseViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(
    private val getUserProfileListUseCase: GetUserProfileListUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : BaseViewModel<SearchViewModel.ViewState, SearchViewModel.Action>(SearchViewModel.ViewState()) {

    fun loadUserProfileList() = viewModelScope.launch(defaultDispatcher) {
        state.keyword?.let {
            if (it.isNotBlank()) {
                sendAction(Action.StartUserProfileListLoading)

                val param = GetUserProfileListUseCase.Param(it)

                getUserProfileListUseCase(param) { result ->
                    result.fold(
                        onSucceed = { userProfileList ->
                            sendAction(Action.UserProfileListLoaded(userProfileList))
                        },
                        onFail = { failure ->
                            sendAction(Action.UserProfileListLoaded(listOf()))
                            onFailure(failure)
                        }
                    )
                }
            }
        }
    }

    private fun onFailure(failure: Failure) = when (failure) {
        is Failure.NetworkConnection -> sendAction(Action.FailOnNetworkConnection)
        is Failure.ServerError -> sendAction(Action.FailOnServerError)
        else -> throw IllegalStateException("Unknown failure type in ${this.javaClass} : $failure")
    }

    override fun onLoadData() { }

    override fun onReduceState(action: Action): ViewState = when (action) {
        is Action.UserProfileListLoaded -> state.copy(
            isUserProfileListLoading = false,
            userProfileList = action.userProfileList
        )
        is Action.FailOnNetworkConnection -> state.copy(
            isNetworkError = true
        )
        is Action.FailOnServerError -> state.copy(
            isServerError = true
        )
        is Action.StartUserProfileListLoading -> state.copy(
            isUserProfileListLoading = true,
            userProfileList = listOf()
        )
    }

    data class ViewState(
        val isUserProfileListLoading: Boolean = false,
        val isNetworkError: Boolean = false,
        val isServerError: Boolean = false,
        val userProfileList: List<UserDomainModel> = listOf(),
        var keyword: String? = null
    ) : BaseViewState

    sealed class Action : BaseAction {
        class UserProfileListLoaded(val userProfileList: List<UserDomainModel>) : Action()
        object StartUserProfileListLoading : Action()
        object FailOnNetworkConnection : Action()
        object FailOnServerError : Action()
    }
}