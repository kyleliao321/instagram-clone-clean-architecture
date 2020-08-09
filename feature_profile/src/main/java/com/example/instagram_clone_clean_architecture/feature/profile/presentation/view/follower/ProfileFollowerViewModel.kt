package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.follower

import androidx.lifecycle.viewModelScope
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetFollowerUserUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.NavigationUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.follower.ProfileFollowerFragmentArgs
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.follower.ProfileFollowerFragmentDirections
import com.example.library_base.domain.exception.Failure
import com.example.library_base.presentation.viewmodel.BaseAction
import com.example.library_base.presentation.viewmodel.BaseViewModel
import com.example.library_base.presentation.viewmodel.BaseViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFollowerViewModel(
    private val args: ProfileFollowerFragmentArgs,
    private val getFollowerUserUseCase: GetFollowerUserUseCase,
    private val navigationUseCase: NavigationUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : BaseViewModel<ProfileFollowerViewModel.ViewState, ProfileFollowerViewModel.Action>(
    ViewState()
) {

    fun onNavigateToUserProfile(userProfile: UserDomainModel) = viewModelScope.launch(defaultDispatcher) {
        val navDir =
            ProfileFollowerFragmentDirections.actionProfileFollowerFragmentToProfileMainFragment(
                userProfile.id
            )
        val params = NavigationUseCase.Param(navDir)

        navigationUseCase(params)
    }

    private fun onLoadFollowerList() = viewModelScope.launch(defaultDispatcher) {
        val userId = args.userId
        val params = GetFollowerUserUseCase.Param(userId)

        getFollowerUserUseCase(params) {
            it.fold(
                onSucceed = { followerList ->
                    sendAction(
                        Action.FollowerListLoaded(
                            followerList
                        )
                    )
                },
                onFail = ::onFailureWhenLoadFollowerList
            )
        }
    }

    private fun onFailureWhenLoadFollowerList(failure: Failure) = when (failure) {
        is Failure.NetworkConnection -> sendAction(Action.NetworkConnectionFailWhenLoadFollowerList)
        is Failure.ServerError -> sendAction(Action.ServerErrorWhenLoadFollowerList)
        else -> throw Exception("Unknown failure when load follower list $failure")
    }

    override fun onLoadData() {
        onLoadFollowerList()
    }

    override fun onReduceState(action: Action): ViewState = when (action) {
        is Action.FollowerListLoaded -> state.copy(
            isFollowerLoading = false,
            followerList = action.followerList
        )
        is Action.NetworkConnectionFailWhenLoadFollowerList -> state.copy(
            isServerError = false,
            isNetworkError = true,
            isFollowerLoading = false,
            followerList = listOf()
        )
        is Action.ServerErrorWhenLoadFollowerList -> state.copy(
            isFollowerLoading = false,
            isNetworkError = false,
            isServerError = true,
            followerList = listOf()
        )
    }

    data class ViewState(
        val isFollowerLoading: Boolean = true,
        val isNetworkError: Boolean = false,
        val isServerError: Boolean = false,
        val followerList: List<UserDomainModel> = listOf()
    ) : BaseViewState

    sealed class Action : BaseAction {
        class FollowerListLoaded(val followerList: List<UserDomainModel>) : Action()
        object NetworkConnectionFailWhenLoadFollowerList : Action()
        object ServerErrorWhenLoadFollowerList : Action()
    }
}