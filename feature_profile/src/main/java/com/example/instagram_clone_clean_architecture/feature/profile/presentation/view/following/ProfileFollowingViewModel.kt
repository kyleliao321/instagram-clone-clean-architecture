package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.following

import androidx.lifecycle.viewModelScope
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetFollowingUserUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.NavigationUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.following.ProfileFollowingFragmentArgs
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.following.ProfileFollowingFragmentDirections
import com.example.library_base.domain.exception.Failure
import com.example.library_base.presentation.viewmodel.BaseAction
import com.example.library_base.presentation.viewmodel.BaseViewModel
import com.example.library_base.presentation.viewmodel.BaseViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFollowingViewModel(
    private val args: ProfileFollowingFragmentArgs,
    private val getFollowingUserUseCase: GetFollowingUserUseCase,
    private val navigationUseCase: NavigationUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : BaseViewModel<ProfileFollowingViewModel.ViewState, ProfileFollowingViewModel.Action>(
    ViewState()
) {

    fun onNavigateToUserProfile(userProfile: UserDomainModel) = viewModelScope.launch(defaultDispatcher) {
        val navDir =
            ProfileFollowingFragmentDirections.actionProfileFollowingFragmentToProfileMainFragment(
                userProfile.id
            )
        val params = NavigationUseCase.Param(navDir)

        navigationUseCase(params)
    }

    private fun onLoadFollowingList() = viewModelScope.launch(defaultDispatcher) {
        val params = GetFollowingUserUseCase.Param(args.userId)

        getFollowingUserUseCase(params) {
            it.fold(
                onSucceed = { followingList ->
                    sendAction(
                        Action.FollowingListLoaded(
                            followingList
                        )
                    )
                },
                onFail = ::onFailureWhenLoadFollowingList
            )
        }
    }

    private fun onFailureWhenLoadFollowingList(failure: Failure) = when (failure) {
        is Failure.NetworkConnection -> sendAction(Action.NetworkConnectionFailWhenLoadFollowingList)
        is Failure.ServerError -> sendAction(Action.ServerErrorWhenLoadFollowingList)
        else -> throw Exception("Unknown failure when load following list $failure")
    }

    override fun onLoadData() {
        onLoadFollowingList()
    }

    override fun onReduceState(action: Action): ViewState = when (action) {
        is Action.FollowingListLoaded -> state.copy(
            isFollowingListLoading = false,
            followingList = action.followingList
        )
        is Action.NetworkConnectionFailWhenLoadFollowingList -> state.copy(
            isFollowingListLoading = false,
            isServerError = false,
            isNetworkError = true,
            followingList = listOf()
        )
        is Action.ServerErrorWhenLoadFollowingList -> state.copy(
            isFollowingListLoading = false,
            isNetworkError = false,
            isServerError = true,
            followingList = listOf()
        )
    }

    data class ViewState(
        val isFollowingListLoading: Boolean = true,
        val isNetworkError: Boolean = false,
        val isServerError: Boolean = false,
        val followingList: List<UserDomainModel> = listOf()
    ) : BaseViewState

    sealed class Action : BaseAction {
        class FollowingListLoaded(val followingList: List<UserDomainModel>) : Action()
        object NetworkConnectionFailWhenLoadFollowingList : Action()
        object ServerErrorWhenLoadFollowingList : Action()
    }
}