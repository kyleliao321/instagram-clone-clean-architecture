package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.edit

import androidx.lifecycle.viewModelScope
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.service.IntentService
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetLoginUserUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetUserProfileUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.NavigationUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.UpdateUserProfileUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.edit.ProfileEditFragmentArgs
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.edit.ProfileEditFragmentDirections
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.follower.ProfileFollowerViewModel
import com.example.library_base.domain.exception.Failure
import com.example.library_base.presentation.viewmodel.BaseAction
import com.example.library_base.presentation.viewmodel.BaseViewModel
import com.example.library_base.presentation.viewmodel.BaseViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import java.util.*

class ProfileEditViewModel(
    private val args: ProfileEditFragmentArgs,
    private val intentService: IntentService,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val navigationUseCase: NavigationUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : BaseViewModel<ProfileEditViewModel.ViewState, ProfileEditViewModel.Action>(
    ViewState()
) {

    fun promptToTakePhotoFromGallery() {
        intentService.openPhotoGallery()
    }

    fun onNavigateToMainProfile() = viewModelScope.launch(defaultDispatcher) {
        val navDir =
            ProfileEditFragmentDirections.actionProfileEditFragmentToProfileMainFragment(
                args.userId
            )
        val params = NavigationUseCase.Param(navDir)

        navigationUseCase(params)
    }

    fun onUpdateUserProfile() = viewModelScope.launch(defaultDispatcher) {
        state.bindingUserProfile?.let { updatedUserProfile ->
            // Start updating animation
            sendAction(Action.StartUpdatingUserProfile)
            // forward update action to UseCase
            val params = UpdateUserProfileUseCase.Param(updatedUserProfile)
            updateUserProfileUseCase(params) { result ->
                result.fold(
                    onSucceed = { userProfile ->
                        sendAction(
                            Action.FinishUpdatingUserProfile(
                                userProfile
                            )
                        )
                    },
                    onFail = { failure ->
                        sendAction(Action.FinishUpdatingUserProfile(state.originalUserProfile!!))
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

    private fun onFailure(failure: Failure) = when (failure) {
        is Failure.NetworkConnection -> sendAction(Action.FailOnNetworkConnection)
        is Failure.ServerError -> sendAction(Action.FailOnServerError)
        else -> throw Exception("Unknown failure type in ${this.javaClass} : $failure")
    }


    override fun onLoadData() {
        loadUserProfile()
    }

    override fun onReduceState(action: Action): ViewState = when (action) {
        is Action.UserProfileLoaded -> state.copy(
            isUserProfileLoading = false,
            originalUserProfile = action.userProfile,
            bindingUserProfile = if (action.userProfile == null) null else action.userProfile.copy()
        )
        is Action.FailOnNetworkConnection -> state.copy(
            isNetworkError = true
        )
        is Action.FailOnServerError -> state.copy(
            isServerError = true
        )
        is Action.StartUpdatingUserProfile -> state.copy(
            isUserProfileUpdating = true
        )
        is Action.FinishUpdatingUserProfile -> state.copy(
            isUserProfileUpdating = false,
            originalUserProfile = action.userProfile,
            bindingUserProfile = action.userProfile.copy()
        )
    }

    data class ViewState(
        val isUserProfileLoading: Boolean = true,
        val isUserProfileUpdating: Boolean = false,
        val isNetworkError: Boolean = false,
        val isServerError: Boolean = false,
        val originalUserProfile: UserDomainModel? = null,
        val bindingUserProfile: UserDomainModel? = null
    ) : BaseViewState

    sealed class Action : BaseAction {
        class UserProfileLoaded(val userProfile: UserDomainModel?) : Action()
        class FinishUpdatingUserProfile(val userProfile: UserDomainModel) : Action()
        object StartUpdatingUserProfile : Action()
        object FailOnNetworkConnection : Action()
        object FailOnServerError : Action()
    }
}