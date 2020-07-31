package com.example.instagram_clone_clean_architecture.feature.profile.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetUserPostUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetUserProfileUseCase
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.extension.asLiveData
import com.example.library_base.domain.utility.Event
import com.example.library_base.presentation.viewmodel.BaseAction
import com.example.library_base.presentation.viewmodel.BaseViewModel
import com.example.library_base.presentation.viewmodel.BaseViewState
import kotlinx.coroutines.launch
import timber.log.Timber

class ProfileMainViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getUserPostUseCase: GetUserPostUseCase
): BaseViewModel<ProfileMainViewModel.ViewState, ProfileMainViewModel.Action>(ProfileMainViewModel.ViewState()) {

    // TODO: This parameter should come form Navigation Argument
    private val userId: Int = 1

    /**
     * Triggering navigation to particular post.
     */
    private val _navigateToPostEvent = MutableLiveData<Event<Int>>()
    val navigateToPostEvent = _navigateToPostEvent.asLiveData()

    /**
     * Triggering navigation to user profile edit view.
     */
    private val _navigateToEditProfileEvent = MutableLiveData<Event<Int>>()
    val navigateToEditProfileEvent = _navigateToEditProfileEvent.asLiveData()

    private fun loadUserProfile() = viewModelScope.launch {
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

    private fun loadUserPost() = viewModelScope.launch {
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
            isUnknownError = true,
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
        val isUnknownError: Boolean = false
    ) : BaseViewState

    sealed class Action : BaseAction {
        class UserProfileLoaded(val userProfile: UserDomainModel) : Action()
        class UserPostLoaded(val userPost: List<PostDomainModel>) : Action()
        object NetworkConnectionFail : Action()
        object FailWithUnknownIssue : Action()
    }
}