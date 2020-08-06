package com.example.instagram_clone_clean_architecture.feature.profile.presentation.edit

import androidx.lifecycle.viewModelScope
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetUserProfileUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.UpdateUserProfileUseCase
import com.example.library_base.domain.exception.Failure
import com.example.library_base.presentation.navigation.NavigationManager
import com.example.library_base.presentation.viewmodel.BaseAction
import com.example.library_base.presentation.viewmodel.BaseViewModel
import com.example.library_base.presentation.viewmodel.BaseViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

class ProfileEditViewModel(
    private val navigationManager: NavigationManager,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : BaseViewModel<ProfileEditViewModel.ViewState, ProfileEditViewModel.Action>(ProfileEditViewModel.ViewState()) {

    // TODO: should come from navigation argument
    private val userId = 1

    fun onNavigateToMainProfile() {
        // TODO: Navigate to main profile fragment
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
                        sendAction(Action.UserProfileLoaded(userProfile))
                    },
                    onFail = ::onFailureWhenUpdateUserProfile
                )
            }
        }
    }


    private fun onLoadUserProfile() = viewModelScope.launch(defaultDispatcher) {
        val params = GetUserProfileUseCase.Param(userId)
        getUserProfileUseCase(params) {
            it.fold(
                onSucceed = { userProfile ->
                    sendAction(Action.UserProfileLoaded(userProfile))
                },
                onFail = ::onFailureWhenLoadUserProfile
            )
        }
    }

    override fun onLoadData() {
        onLoadUserProfile()
    }

    override fun onReduceState(action: Action): ViewState = when (action) {
        is Action.UserProfileLoaded -> state.copy(
            isUserProfileLoading = false,
            originalUserProfile = action.userProfile,
            bindingUserProfile = action.userProfile
        )
        is Action.NetworkConnectionErrorWhenLoadUserProfile -> state.copy(
            isUserProfileLoading = false,
            isNetworkConnectionFail = true,
            isServerError = false,
            originalUserProfile = null,
            bindingUserProfile = null
        )
        is Action.NetworkConnectionErrorWhenUpdateUserProfile -> state.copy(
            isUserProfileLoading = false,
            isNetworkConnectionFail = true,
            isServerError = false,
            bindingUserProfile = state.originalUserProfile
        )
        is Action.ServerErrorWhenLoadUserProfile -> state.copy(
            isUserProfileLoading = false,
            isServerError = true,
            isNetworkConnectionFail = false,
            originalUserProfile = null,
            bindingUserProfile = null
        )
        is Action.ServerErrorWhenUpdateUserProfile -> state.copy(
            isUserProfileLoading = false,
            isNetworkConnectionFail = false,
            isServerError = true,
            bindingUserProfile = state.originalUserProfile
        )
        is Action.StartUpdatingUserProfile -> state.copy(
            isUserProfileLoading = true
        )
    }

    private fun onFailureWhenLoadUserProfile(failure: Failure) = when (failure) {
        is Failure.NetworkConnection -> sendAction(Action.NetworkConnectionErrorWhenLoadUserProfile)
        is Failure.ServerError -> sendAction(Action.ServerErrorWhenLoadUserProfile)
        else -> throw Exception("Unknown failure when load user profile $failure")
    }

    private fun onFailureWhenUpdateUserProfile(failure: Failure) = when (failure) {
        is Failure.NetworkConnection -> sendAction(Action.NetworkConnectionErrorWhenUpdateUserProfile)
        is Failure.ServerError -> sendAction(Action.ServerErrorWhenUpdateUserProfile)
        else -> throw Exception("Unknown failure when update user profile $failure")
    }

    data class ViewState(
        val isUserProfileLoading: Boolean = true,
        val isNetworkConnectionFail: Boolean = false,
        val isServerError: Boolean = false,
        val originalUserProfile: UserDomainModel? = null,
        val bindingUserProfile: UserDomainModel? = null
    ) : BaseViewState

    sealed class Action : BaseAction {
        class UserProfileLoaded(val userProfile: UserDomainModel) : Action()
        object StartUpdatingUserProfile : Action()
        object ServerErrorWhenLoadUserProfile : Action()
        object ServerErrorWhenUpdateUserProfile : Action()
        object NetworkConnectionErrorWhenLoadUserProfile : Action()
        object NetworkConnectionErrorWhenUpdateUserProfile : Action()
    }
}