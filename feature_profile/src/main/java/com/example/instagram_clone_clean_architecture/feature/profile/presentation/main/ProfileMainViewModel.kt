package com.example.instagram_clone_clean_architecture.feature.profile.presentation.main

import androidx.lifecycle.viewModelScope
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetUserPostUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetUserProfileUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.NavigationUseCase
import com.example.library_base.domain.exception.Failure
import com.example.library_base.presentation.navigation.NavigationManager
import com.example.library_base.presentation.viewmodel.BaseAction
import com.example.library_base.presentation.viewmodel.BaseViewModel
import com.example.library_base.presentation.viewmodel.BaseViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.bindings.ArgSetBinding

class ProfileMainViewModel(
    private val args: ProfileMainFragmentArgs,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getUserPostUseCase: GetUserPostUseCase,
    private val navigationUseCase: NavigationUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
): BaseViewModel<ProfileMainViewModel.ViewState, ProfileMainViewModel.Action>(ProfileMainViewModel.ViewState()) {

    fun onNavigateToPostDetail(post: PostDomainModel) {
        // TODO: Call navigation manger to navigate to post detail fragment
    }

    fun onNavigateToEditProfile() = viewModelScope.launch(defaultDispatcher) {
        val navDir = ProfileMainFragmentDirections.actionProfileMainFragmentToProfileEditFragment(args.userId)
        val params = NavigationUseCase.Param(navDir)

        navigationUseCase(params)
    }

    fun onNavigateToFollowerProfile() = viewModelScope.launch(defaultDispatcher) {
        val navDir = ProfileMainFragmentDirections.actionProfileMainFragmentToProfileFollowerFragment(args.userId)
        val params = NavigationUseCase.Param(navDir)

        navigationUseCase(params)
    }

    fun onNavigateToFollowingProfile() = viewModelScope.launch(defaultDispatcher) {
        val navDir = ProfileMainFragmentDirections.actionProfileMainFragmentToProfileFollowingFragment(args.userId)
        val params = NavigationUseCase.Param(navDir)

        navigationUseCase(params)
    }

    private fun loadUserProfile() = viewModelScope.launch(defaultDispatcher) {
        val params = GetUserProfileUseCase.Param(args.userId)
        getUserProfileUseCase(params) {
            it.fold(
                onSucceed = { userProfile ->
                    sendAction(Action.UserProfileLoaded(userProfile))
                },
                onFail = ::onFailureWhenLoadUserProfile
            )
        }
    }

    private fun loadUserPost() = viewModelScope.launch(defaultDispatcher)  {
        val params = GetUserPostUseCase.Param(args.userId)
        getUserPostUseCase(params) {
            it.fold(
                onSucceed = { userPost ->
                    sendAction(Action.UserPostLoaded(userPost))
                },
                onFail = ::onFailureWhenLoadUserPost
            )
        }
    }

    private fun onFailureWhenLoadUserProfile(failure: Failure) = when (failure) {
        is Failure.NetworkConnection -> sendAction(Action.NetworkConnectionFailWhenLoadUserProfile)
        else -> sendAction(Action.ServerErrorWhenLoadUserProfile)
    }

    private fun onFailureWhenLoadUserPost(failure: Failure) = when (failure) {
        is Failure.NetworkConnection -> sendAction(Action.NetworkConnectionFailWhenLoadUserPost)
        is Failure.ServerError -> sendAction(Action.ServerErrorWhenLoadUserPosts)
        else -> throw Exception("Unknown failure when load user posts : $failure")
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
        is Action.NetworkConnectionFailWhenLoadUserProfile -> state.copy(
            isProfileLoading = false,
            isPostLoading = false,
            isNetworkError = true,
            isServerError = false,
            userProfile = null
        )
        is Action.NetworkConnectionFailWhenLoadUserPost -> state.copy(
            isProfileLoading = false,
            isPostLoading = false,
            isNetworkError = true,
            isServerError = false,
            userPosts = listOf()
        )
        is Action.ServerErrorWhenLoadUserProfile -> state.copy(
            isProfileLoading = false,
            isPostLoading = false,
            isNetworkError = false,
            isServerError = true,
            userProfile = null
        )
        is Action.ServerErrorWhenLoadUserPosts -> state.copy(
            isProfileLoading = false,
            isPostLoading = false,
            isNetworkError = false,
            isServerError = true,
            userPosts = listOf()
        )
    }

    data class ViewState(
        val userProfile: UserDomainModel? = null,
        val userPosts: List<PostDomainModel> = listOf(),
        val isProfileLoading: Boolean = true,
        val isPostLoading: Boolean = true,
        val isNetworkError: Boolean = false,
        val isServerError: Boolean = false
    ) : BaseViewState

    sealed class Action : BaseAction {
        class UserProfileLoaded(val userProfile: UserDomainModel) : Action()
        class UserPostLoaded(val userPost: List<PostDomainModel>) : Action()
        object NetworkConnectionFailWhenLoadUserProfile : Action()
        object NetworkConnectionFailWhenLoadUserPost : Action()
        object ServerErrorWhenLoadUserProfile : Action()
        object ServerErrorWhenLoadUserPosts : Action()
    }
}