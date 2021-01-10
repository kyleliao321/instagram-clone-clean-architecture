package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
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
import timber.log.Timber

class ProfilePostViewModel(
    private val args: ProfilePostFragmentArgs,
    private val getLoginUserUseCase: GetLoginUserUseCase,
    private val getPostUseCase: GetPostUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getLikedUsersUseCase: GetLikedUsersUseCase,
    private val userLikePostUseCase: UserLikePostUseCase,
    private val userUnlikePostUseCase: UserUnlikePostUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : BaseViewModel<ProfilePostViewModel.ViewState, ProfilePostViewModel.Action>(
    ViewState()
) {

    val isLoginUserLikedPost = Transformations.map(stateLiveData) {
        if (it.loginUserProfile != null && !it.isLikedUsersLoading) {
            val likedUsersIds = it.likedUsers.map { user -> user.id }
            return@map it.loginUserProfile.id in likedUsersIds
        } else {
            return@map false
        }
    }

    val isDataLoading = Transformations.map(stateLiveData) {
        it.isLikedUsersLoading || it.isLoginUserProfileLoading || it.isPostLoading || it.isProfileLoading
    }

    val errorMessage: LiveData<String?> = Transformations.map(stateLiveData) {
        if (it.isNetworkError) {
            return@map "Network Connection Fail"
        }
        return@map null
    }

    fun onLikeButtonClicked() = viewModelScope.launch(defaultDispatcher) {
        val userId = state.loginUserProfile!!.id
        val postId = state.post!!.id
        when (isLoginUserLikedPost.value) {
            true -> {
                val params = UserUnlikePostUseCase.Param(userId, postId)
                userUnlikePostUseCase(params) {
                    it.fold(
                        onSucceed = {
                            loadData()
                        },
                        onFail = ::onFailure
                    )
                }
            }
            false -> {
                val params = UserLikePostUseCase.Param(userId, postId)
                userLikePostUseCase(params) {
                    it.fold(
                        onSucceed = {
                            loadData()
                        },
                        onFail = ::onFailure
                    )
                }
            }
        }
    }

    private fun loadLoginUserProfile() = viewModelScope.launch(defaultDispatcher) {
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

    private fun loadPost() = viewModelScope.launch(defaultDispatcher) {
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
                onFail = { failure ->
                    sendAction(Action.PostLoaded(null))
                    onFailure(failure)
                }
            )
        }
    }

    private fun loadLikedUsers() = viewModelScope.launch(defaultDispatcher) {
        val params = GetLikedUsersUseCase.Param(args.postId)
        getLikedUsersUseCase(params) {
            it.fold(
                onSucceed = { userList ->
                    sendAction(Action.LikedUsersLoaded(userList))
                },
                onFail = { failure ->
                    sendAction(Action.LikedUsersLoaded(listOf()))
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
                    sendAction(Action.ProfileLoaded(userProfile))
                },
                onFail = { failure ->
                    sendAction(Action.ProfileLoaded(null))
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
        sendAction(Action.Reload)
        loadPost()
        loadLikedUsers()
        loadUserProfile()
        loadLoginUserProfile()
    }

    override fun onReduceState(action: Action): ViewState = when (action) {
        is Action.LoginUserProfileLoaded -> state.copy(
            isLoginUserProfileLoading = false,
            loginUserProfile = action.userProfile
        )
        is Action.ProfileLoaded -> state.copy(
            isProfileLoading = false,
            userProfile = action.userProfile
        )
        is Action.PostLoaded -> state.copy(
            isPostLoading = false,
            post = action.post
        )
        is Action.LikedUsersLoaded -> state.copy(
            isLikedUsersLoading = false,
            likedUsers = action.likedUsers
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
        is Action.Reload -> ViewState()
    }

    data class ViewState(
        val isLoginUserProfileLoading: Boolean = true,
        val isProfileLoading: Boolean = true,
        val isPostLoading: Boolean = true,
        val isLikedUsersLoading: Boolean = true,
        val isNetworkError: Boolean = false,
        val isServerError: Boolean = false,
        val isLocalAccountError: Boolean = false,
        val loginUserProfile: UserDomainModel? = null,
        val userProfile: UserDomainModel? = null,
        val post: PostDomainModel? = null,
        val likedUsers: List<UserDomainModel> = listOf()
    ) : BaseViewState

    sealed class Action : BaseAction {
        class LoginUserProfileLoaded(val userProfile: UserDomainModel?) : Action()
        class ProfileLoaded(val userProfile: UserDomainModel?) : Action()
        class PostLoaded(val post: PostDomainModel?) : Action()
        class LikedUsersLoaded(val likedUsers: List<UserDomainModel>) : Action()
        object FailOnNetworkConnection : Action()
        object FailOnServerError : Action()
        object FailOnLocalAccountError: Action()
        object Reload : Action()
    }
}