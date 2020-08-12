package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.main

import androidx.lifecycle.viewModelScope
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetLoginUserUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetUserPostUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetUserProfileUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.NavigationUseCase
import com.example.library_base.domain.exception.Failure
import com.example.library_base.presentation.viewmodel.BaseAction
import com.example.library_base.presentation.viewmodel.BaseViewModel
import com.example.library_base.presentation.viewmodel.BaseViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileMainViewModel(
    private val args: ProfileMainFragmentArgs,
    private val getLoginUserUseCase: GetLoginUserUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getUserPostUseCase: GetUserPostUseCase,
    private val navigationUseCase: NavigationUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
): BaseViewModel<ProfileMainViewModel.ViewState, ProfileMainViewModel.Action>(
    ViewState()
) {

    fun onNavigateToPostDetail(post: PostDomainModel) = viewModelScope.launch(defaultDispatcher) {
        val navDir = ProfileMainFragmentDirections.actionProfileMainFragmentToProfilePostFragment(post.belongUserId, post.id)
        val params = NavigationUseCase.Param(navDir)

        navigationUseCase(params)
    }

    fun onNavigateToEditProfile() = viewModelScope.launch(defaultDispatcher) {
        val navDir =
            ProfileMainFragmentDirections.actionProfileMainFragmentToProfileEditFragment(
                args.userId
            )
        val params = NavigationUseCase.Param(navDir)

        navigationUseCase(params)
    }

    fun onNavigateToFollowerProfile() = viewModelScope.launch(defaultDispatcher) {
        val navDir =
            ProfileMainFragmentDirections.actionProfileMainFragmentToProfileFollowerFragment(
                args.userId
            )
        val params = NavigationUseCase.Param(navDir)

        navigationUseCase(params)
    }

    fun onNavigateToFollowingProfile() = viewModelScope.launch(defaultDispatcher) {
        val navDir =
            ProfileMainFragmentDirections.actionProfileMainFragmentToProfileFollowingFragment(
                args.userId
            )
        val params = NavigationUseCase.Param(navDir)

        navigationUseCase(params)
    }

    private fun loadLoginUser() = viewModelScope.launch(defaultDispatcher) {
        getLoginUserUseCase(Unit) {
            it.fold(
                onSucceed = { userProfile ->
                    sendAction(Action.LoginUserProfileLoaded(userProfile))
                },
                onFail = { failure ->
                    sendAction(Action.LoginUserProfileLoaded(null))
                    onFailure(failure)
                }
            )
        }
    }

    private fun loadUserProfile() = viewModelScope.launch(defaultDispatcher) {
        val params = GetUserProfileUseCase.Param(args.userId)
        getUserProfileUseCase(params) {
            it.fold(
                onSucceed = { userProfile ->
                    sendAction(
                        Action.UserProfileLoaded(
                            userProfile
                        )
                    )
                },
                onFail = { failure ->
                    sendAction(Action.UserProfileLoaded(null))
                    onFailure(failure)
                }
            )
        }
    }

    private fun loadUserPost() = viewModelScope.launch(defaultDispatcher)  {
        val params = GetUserPostUseCase.Param(args.userId)
        getUserPostUseCase(params) {
            it.fold(
                onSucceed = { userPost ->
                    sendAction(
                        Action.UserPostLoaded(
                            userPost
                        )
                    )
                },
                onFail = { failure ->
                    sendAction(Action.UserPostLoaded(listOf()))
                    onFailure(failure)
                }
            )
        }
    }

    private fun onFailure(failure: Failure) = when (failure) {
        is Failure.NetworkConnection -> sendAction(Action.FailOnNetworkConnection)
        is Failure.ServerError -> sendAction(Action.FailOnServerError)
        is Failure.LocalAccountNotFound -> sendAction(Action.FailOnLocalAccountError)
        else -> throw Exception("Unknown failure type in ${this.javaClass} : $failure")
    }

    override fun onLoadData() {
        loadUserPost()
        loadUserProfile()
        loadLoginUser()
    }

    override fun onReduceState(action: Action): ViewState = when (action) {
        is Action.LoginUserProfileLoaded -> state.copy(
            isLoginUserLoading = false,
            loginUserProfile = action.userProfile
        )
        is Action.UserProfileLoaded -> state.copy(
            isProfileLoading = false,
            userProfile = action.userProfile
        )
        is Action.UserPostLoaded -> state.copy(
            isPostLoading = false,
            userPosts = action.userPost
        )
        is Action.FailOnNetworkConnection -> state.copy(
            isNetworkError = true
        )
        is Action.FailOnServerError -> state.copy(
            isServerError = true
        )
        is Action.FailOnLocalAccountError -> state.copy(
            isLocalAccountError = true
        )
    }

    data class ViewState(
        val loginUserProfile: UserDomainModel? = null,
        val userProfile: UserDomainModel? = null,
        val userPosts: List<PostDomainModel> = listOf(),
        val isLoginUserLoading: Boolean = true,
        val isProfileLoading: Boolean = true,
        val isPostLoading: Boolean = true,
        val isNetworkError: Boolean = false,
        val isServerError: Boolean = false,
        val isLocalAccountError: Boolean = false
    ) : BaseViewState

    sealed class Action : BaseAction {
        class LoginUserProfileLoaded(val userProfile: UserDomainModel?) : Action()
        class UserProfileLoaded(val userProfile: UserDomainModel?) : Action()
        class UserPostLoaded(val userPost: List<PostDomainModel>) : Action()
        object FailOnNetworkConnection : Action()
        object FailOnServerError : Action()
        object FailOnLocalAccountError : Action()
    }
}