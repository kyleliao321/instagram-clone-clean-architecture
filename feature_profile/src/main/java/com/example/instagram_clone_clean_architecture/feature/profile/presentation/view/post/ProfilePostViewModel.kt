package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.post

import androidx.lifecycle.viewModelScope
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetPostUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetUserProfileUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.post.ProfilePostFragmentArgs
import com.example.library_base.domain.exception.Failure
import com.example.library_base.presentation.viewmodel.BaseAction
import com.example.library_base.presentation.viewmodel.BaseViewModel
import com.example.library_base.presentation.viewmodel.BaseViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfilePostViewModel(
    private val args: ProfilePostFragmentArgs,
    private val getPostUseCase: GetPostUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : BaseViewModel<ProfilePostViewModel.ViewState, ProfilePostViewModel.Action>(
    ViewState()
) {

    private fun onLoadPost() = viewModelScope.launch(defaultDispatcher) {
        val params = GetPostUseCase.Param(args.postId)
        getPostUseCase(params) {
            it.fold(
                onSucceed = { post ->
                    sendAction(
                        Action.PostLoaded(
                            post
                        )
                    )
                },
                onFail = ::onFailureWhenLoadPost
            )
        }
    }

    private fun onLoadUserProfile() = viewModelScope.launch(defaultDispatcher) {
        val params = GetUserProfileUseCase.Param(args.userId)
        getUserProfileUseCase(params) {
            it.fold(
                onSucceed = { userProfile ->
                    sendAction(Action.ProfileLoaded(userProfile))
                },
                onFail = ::onFailureWhenLoadProfile
            )
        }
    }

    private fun onFailureWhenLoadPost(failure: Failure) = when (failure) {
        is Failure.NetworkConnection -> sendAction(Action.NetworkConnectionFailWhenLoadPost)
        is Failure.ServerError -> sendAction(Action.ServerErrorWhenLoadPost)
        else -> throw Exception("Unknown failure when load post $failure")
    }

    private fun onFailureWhenLoadProfile(failure: Failure) = when (failure) {
        is Failure.NetworkConnection -> sendAction(Action.NetworkConnectionFailWhenLoadProfile)
        is Failure.ServerError -> sendAction(Action.ServerErrorWhenLoadProfile)
        else -> throw Exception("Unknown failure when load post $failure")
    }

    override fun onLoadData() {
        onLoadPost()
        onLoadUserProfile()
    }

    override fun onReduceState(action: Action): ViewState = when (action) {
        is Action.ProfileLoaded -> state.copy(
            isProfileLoading = false,
            userProfile = action.userProfile
        )
        is Action.PostLoaded -> state.copy(
            isPostLoading = false,
            post = action.post
        )
        is Action.NetworkConnectionFailWhenLoadProfile -> state.copy(
            isProfileLoading = false,
            isNetworkError = true,
            isServerError = false,
            userProfile = null
        )
        is Action.ServerErrorWhenLoadProfile -> state.copy(
            isProfileLoading = false,
            isServerError = true,
            isNetworkError = false,
            userProfile = null
        )
        is Action.NetworkConnectionFailWhenLoadPost -> state.copy(
            isPostLoading = false,
            isNetworkError = true,
            isServerError = false,
            post = null
        )
        is Action.ServerErrorWhenLoadPost -> state.copy(
            isPostLoading = false,
            isServerError = true,
            isNetworkError = false,
            post = null
        )
    }

    data class ViewState(
        val isProfileLoading: Boolean = true,
        val isPostLoading: Boolean = true,
        val isNetworkError: Boolean = false,
        val isServerError: Boolean = false,
        val userProfile: UserDomainModel? = null,
        val post: PostDomainModel? = null
    ) : BaseViewState

    sealed class Action : BaseAction {
        class ProfileLoaded(val userProfile: UserDomainModel) : Action()
        class PostLoaded(val post: PostDomainModel) : Action()
        object NetworkConnectionFailWhenLoadProfile : Action()
        object ServerErrorWhenLoadProfile : Action()
        object NetworkConnectionFailWhenLoadPost : Action()
        object ServerErrorWhenLoadPost : Action()
    }
}