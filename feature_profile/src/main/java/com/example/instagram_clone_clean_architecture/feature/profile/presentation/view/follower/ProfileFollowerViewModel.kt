package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.follower

import androidx.lifecycle.viewModelScope
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetFollowerUserUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetLoginUserUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.NavigationUseCase
import com.example.library_base.domain.exception.Failure
import com.example.library_base.presentation.viewmodel.BaseAction
import com.example.library_base.presentation.viewmodel.BaseViewModel
import com.example.library_base.presentation.viewmodel.BaseViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFollowerViewModel(
    private val args: ProfileFollowerFragmentArgs,
    private val getLoginUserUseCase: GetLoginUserUseCase,
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

    private fun loadFollowerList() = viewModelScope.launch(defaultDispatcher) {
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
                onFail = { failure ->
                    sendAction(
                        Action.FollowerListLoaded(listOf())
                    )
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
        loadFollowerList()
        loadLoginUser()
    }

    override fun onReduceState(action: Action): ViewState = when (action) {
        is Action.LoginUserProfileLoaded -> state.copy(
            isLoginUserLoading = false,
            loginUser = action.userProfile
        )
        is Action.FollowerListLoaded -> state.copy(
            isFollowerListLoading = false,
            followerList = action.followerList
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
        val isLoginUserLoading: Boolean = true,
        val isFollowerListLoading: Boolean = true,
        val isNetworkError: Boolean = false,
        val isServerError: Boolean = false,
        val isLocalAccountError: Boolean = false,
        val loginUser: UserDomainModel? = null,
        val followerList: List<UserDomainModel> = listOf()
    ) : BaseViewState

    sealed class Action : BaseAction {
        class LoginUserProfileLoaded(val userProfile: UserDomainModel?) : Action()
        class FollowerListLoaded(val followerList: List<UserDomainModel>) : Action()
        object FailOnNetworkConnection : Action()
        object FailOnServerError : Action()
        object FailOnLocalAccountError : Action()
    }
}