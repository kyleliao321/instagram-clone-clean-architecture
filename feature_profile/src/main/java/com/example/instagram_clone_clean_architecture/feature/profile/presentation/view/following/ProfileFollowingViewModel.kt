package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.following

import android.view.View
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.*
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.follower.ProfileFollowerViewModel
import com.example.library_base.domain.exception.Failure
import com.example.library_base.presentation.viewmodel.BaseAction
import com.example.library_base.presentation.viewmodel.BaseViewModel
import com.example.library_base.presentation.viewmodel.BaseViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.log

class ProfileFollowingViewModel(
    private val args: ProfileFollowingFragmentArgs,
    private val getLoginUserUseCase: GetLoginUserUseCase,
    private val getFollowingUserUseCase: GetFollowingUserUseCase,
    private val addUserRelationUseCase: AddUserRelationUseCase,
    private val removeUserRelationUseCase: RemoveUserRelationUseCase,
    private val navigationUseCase: NavigationUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : BaseViewModel<ProfileFollowingViewModel.ViewState, ProfileFollowingViewModel.Action>(
    ViewState()
) {

    val isDataLoading = Transformations.map(stateLiveData) {
        it.isFollowingListLoading || it.isLoginUserFollowingLoading || it.isLoginUserLoading
    }

    val errorMessage = Transformations.map(stateLiveData) {
        if (it.isNetworkError) {
            return@map "Network Connection failed"
        }

        return@map null
    }

    fun onNavigateToUserProfile(userProfile: UserDomainModel) = viewModelScope.launch(defaultDispatcher) {
        val navDir =
            ProfileFollowingFragmentDirections.actionProfileFollowingFragmentToProfileMainFragment(
                userProfile.id
            )
        val params = NavigationUseCase.Param(navDir)

        navigationUseCase(params)
    }

    fun addUserRelation(following: UserDomainModel) = viewModelScope.launch(defaultDispatcher) {
        val params = AddUserRelationUseCase.Param(state.loginUser!!.id, following.id)

        addUserRelationUseCase(params) {
            it.fold(
                onSucceed = {
                    loadData()
                },
                onFail = ::onFailure
            )
        }
    }

    fun removeUserRelation(following: UserDomainModel) = viewModelScope.launch(defaultDispatcher) {
        val params = RemoveUserRelationUseCase.Param(state.loginUser!!.id, following.id)

        removeUserRelationUseCase(params) {
            it.fold(
                onSucceed = {
                    loadData()
                },
                onFail = ::onFailure
            )
        }
    }

    private fun loadLoginUserData() = viewModelScope.launch(defaultDispatcher) {
        var loginUser: UserDomainModel? = null

        getLoginUserUseCase(Unit) {
            it.fold(
                onSucceed = { userProfile ->
                    sendAction(Action.LoginUserProfileLoaded(userProfile))
                    loginUser = userProfile
                },
                onFail = { failure ->
                    sendAction(Action.LoginUserProfileLoaded(null))
                    sendAction(Action.LoginUserFollowingListLoaded(listOf()))
                    onFailure(failure)
                }
            )
        }

        loginUser?.let { loginUserProfile ->
            val params = GetFollowingUserUseCase.Param(loginUserProfile.id)

            getFollowingUserUseCase(params) {
                it.fold(
                    onSucceed = { followingList ->
                        sendAction(Action.LoginUserFollowingListLoaded(followingList))
                    },
                    onFail = { failure ->
                        sendAction(Action.LoginUserFollowingListLoaded(listOf()))
                        onFailure(failure)
                    }
                )
            }
        }
    }

    private fun loadFollowingList() = viewModelScope.launch(defaultDispatcher) {
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
                onFail = { failure ->
                    sendAction(
                        Action.FollowingListLoaded(
                            listOf()
                        )
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
        sendAction(Action.ReloadData)
        loadFollowingList()
        loadLoginUserData()
    }

    override fun onReduceState(action: Action): ViewState = when (action) {
        is Action.LoginUserProfileLoaded -> state.copy(
            isLoginUserLoading = false,
            loginUser = action.userProfile
        )
        is Action.LoginUserFollowingListLoaded -> state.copy(
            isLoginUserFollowingLoading = false,
            loginUserFollowingList = action.followingList
        )
        is Action.FollowingListLoaded -> state.copy(
            isFollowingListLoading = false,
            followingList = action.followingList
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
        is Action.ReloadData -> ViewState()
    }

    data class ViewState(
        val isLoginUserLoading: Boolean = true,
        val isLoginUserFollowingLoading: Boolean = true,
        val isFollowingListLoading: Boolean = true,
        val isNetworkError: Boolean = false,
        val isServerError: Boolean = false,
        val isLocalAccountError: Boolean = false,
        val loginUser: UserDomainModel? = null,
        val loginUserFollowingList: List<UserDomainModel> = listOf(),
        val followingList: List<UserDomainModel> = listOf()
    ) : BaseViewState

    sealed class Action : BaseAction {
        class LoginUserProfileLoaded(val userProfile: UserDomainModel?) : Action()
        class LoginUserFollowingListLoaded(val followingList: List<UserDomainModel>) : Action()
        class FollowingListLoaded(val followingList: List<UserDomainModel>) : Action()
        object FailOnNetworkConnection : Action()
        object FailOnServerError : Action()
        object FailOnLocalAccountError : Action()
        object ReloadData : Action()
    }
}