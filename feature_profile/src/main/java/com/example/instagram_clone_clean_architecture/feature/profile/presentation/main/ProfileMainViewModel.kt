package com.example.instagram_clone_clean_architecture.feature.profile.presentation.main

import androidx.lifecycle.viewModelScope
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetUserPostUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetUserProfileUseCase
import com.example.library_base.domain.exception.Failure
import com.example.library_base.presentation.navigation.NavigationManager
import com.example.library_base.presentation.viewmodel.BaseAction
import com.example.library_base.presentation.viewmodel.BaseViewModel
import com.example.library_base.presentation.viewmodel.BaseViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class ProfileMainViewModel(
    private val navigationManager: NavigationManager,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getUserPostUseCase: GetUserPostUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
): BaseViewModel<ProfileMainViewModel.ViewState, ProfileMainViewModel.Action>(ProfileMainViewModel.ViewState()) {

    // TODO: This parameter should come form Navigation Argument
    private val userId: Int = 1

    fun onNavigateToPostDetail(post: PostDomainModel) {
        // TODO: Call navigation manger to navigate to post detail fragment
    }

    fun onNavigateToEditProfile(user: UserDomainModel) {
        // TODO: Call navigation manager to navigate to profile edit fragment
    }

    private fun loadUserProfile() = viewModelScope.launch(defaultDispatcher) {
        val params = GetUserProfileUseCase.Param(userId)
        getUserProfileUseCase(params) {
            it.fold(
                onSucceed = { userProfile ->
                    sendAction(Action.UserProfileLoaded(userProfile))
                },
                onFail = ::onFailure
            )
        }
    }

    private fun loadUserPost() = viewModelScope.launch(defaultDispatcher) {
        val params = GetUserPostUseCase.Param(userId)
        getUserPostUseCase(params) {
            it.fold(
                onSucceed = { userPost ->
                    sendAction(Action.UserPostLoaded(userPost))
                },
                onFail = ::onFailure
            )
        }
    }

    private fun onFailure(failure: Failure) = when (failure) {
        is Failure.NetworkConnection -> sendAction(Action.NetworkConnectionFail)
        else -> sendAction(Action.FailWithUnknownIssue)
    }

    override fun onLoadData() {
        loadUserPost()
        loadUserProfile()
    }

    override fun onReduceState(action: Action): ViewState = when (action) {
        is Action.UserProfileLoaded -> state.copy(
            isProfileLoading = false,
            userProfile = action.userProfile
        )
        is Action.UserPostLoaded -> state.copy(
            isPostLoading = false,
            userPosts = action.userPost
        )
        is Action.NetworkConnectionFail -> state.copy(
            isProfileLoading = false,
            isPostLoading = false,
            isNetworkError = true,
            isUnknownError = false,
            userProfile = null,
            userPosts = listOf()
        )
        is Action.FailWithUnknownIssue -> state.copy(
            isProfileLoading = false,
            isPostLoading = false,
            isNetworkError = false,
            isUserProfileError = false,
            isUnknownError = true,
            userProfile = null,
            userPosts = listOf()
        )
        is Action.FailOnFetchingUserProfile -> state.copy(
            isProfileLoading = false,
            isPostLoading = false,
            isNetworkError = false,
            isUserProfileError = true,
            isUnknownError = false,
            userProfile = null,
            userPosts = listOf()
        )
    }

    data class ViewState(
        val userProfile: UserDomainModel? = null,
        val userPosts: List<PostDomainModel> = listOf(),
        val isProfileLoading: Boolean = true,
        val isPostLoading: Boolean = true,
        val isNetworkError: Boolean = false,
        val isUserProfileError: Boolean = false,
        val isUnknownError: Boolean = false
    ) : BaseViewState

    sealed class Action : BaseAction {
        class UserProfileLoaded(val userProfile: UserDomainModel) : Action()
        class UserPostLoaded(val userPost: List<PostDomainModel>) : Action()
        object NetworkConnectionFail : Action()
        object FailOnFetchingUserProfile : Action()
        object FailWithUnknownIssue : Action()
    }
}