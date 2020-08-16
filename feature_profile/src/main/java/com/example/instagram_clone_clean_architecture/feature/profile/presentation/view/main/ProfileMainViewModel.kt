package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.main

import androidx.lifecycle.viewModelScope
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.*
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
    private val getFollowingUserUseCase: GetFollowingUserUseCase,
    private val addUserRelationUseCase: AddUserRelationUseCase,
    private val removeUserRelationUseCase: RemoveUserRelationUseCase,
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

    fun addUserRelation() = viewModelScope.launch(defaultDispatcher) {
        val param = AddUserRelationUseCase.Param(state.loginUserProfile!!.id, state.userProfile!!.id)

        addUserRelationUseCase(param) {
            it.fold(
                onSucceed = {
                    sendAction(Action.ReloadData)
                    loadData()
                },
                onFail = ::onFailure
            )
        }
    }

    fun removeUserRelation() = viewModelScope.launch(defaultDispatcher) {
        val param = RemoveUserRelationUseCase.Param(state.loginUserProfile!!.id, state.userProfile!!.id)

        removeUserRelationUseCase(param) {
            it.fold(
                onSucceed = {
                    sendAction(Action.ReloadData)
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
                    loginUser = userProfile
                    sendAction(Action.LoginUserProfileLoaded(userProfile))
                },
                onFail = { failure ->
                    sendAction(Action.LoginUserProfileLoaded(null))
                    sendAction(Action.LoginUserFollowingListLoaded(listOf()))
                    onFailure(failure)
                }
            )
        }

        loginUser?.let { loginUserProfile ->
            val param = GetFollowingUserUseCase.Param(loginUserProfile.id)

            getFollowingUserUseCase(param) {
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
        loadLoginUserData()
    }

    override fun onReduceState(action: Action): ViewState = when (action) {
        is Action.LoginUserProfileLoaded -> state.copy(
            isLoginUserLoading = false,
            loginUserProfile = action.userProfile
        )
        is Action.LoginUserFollowingListLoaded -> state.copy(
            isLoginFollowingListLoading = false,
            loginUserFollowingList = action.followingList
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
        is Action.ReloadData -> ViewState()
    }

    data class ViewState(
        val loginUserProfile: UserDomainModel? = null,
        val loginUserFollowingList: List<UserDomainModel> = listOf(),
        val userProfile: UserDomainModel? = null,
        val userPosts: List<PostDomainModel> = listOf(),
        val isLoginFollowingListLoading: Boolean = true,
        val isLoginUserLoading: Boolean = true,
        val isProfileLoading: Boolean = true,
        val isPostLoading: Boolean = true,
        val isNetworkError: Boolean = false,
        val isServerError: Boolean = false,
        val isLocalAccountError: Boolean = false
    ) : BaseViewState

    sealed class Action : BaseAction {
        class LoginUserProfileLoaded(val userProfile: UserDomainModel?) : Action()
        class LoginUserFollowingListLoaded(val followingList: List<UserDomainModel>) : Action()
        class UserProfileLoaded(val userProfile: UserDomainModel?) : Action()
        class UserPostLoaded(val userPost: List<PostDomainModel>) : Action()
        object FailOnNetworkConnection : Action()
        object FailOnServerError : Action()
        object FailOnLocalAccountError : Action()
        object ReloadData : Action()
    }
}