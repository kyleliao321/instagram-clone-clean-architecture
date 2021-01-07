package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.edit

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserProfileUploadDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.service.IntentService
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.*
import com.example.library_base.domain.exception.Failure
import com.example.library_base.presentation.viewmodel.BaseAction
import com.example.library_base.presentation.viewmodel.BaseViewModel
import com.example.library_base.presentation.viewmodel.BaseViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileEditViewModel(
    private val args: ProfileEditFragmentArgs,
    private val intentService: IntentService,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val consumeUserSelectedImageUseCase: ConsumeUserSelectedImageUseCase,
    private val getBitmapUseCase: GetBitmapUseCase,
    private val navigationUseCase: NavigationUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : BaseViewModel<ProfileEditViewModel.ViewState, ProfileEditViewModel.Action>(
    ViewState()
) {

    val isDataLoading = Transformations.map(stateLiveData) {
        it.isCachedImageLoading || it.isUserProfileLoading
    }

    val errorMessage = Transformations.map(stateLiveData) {
        if (it.isNetworkError) {
            return@map "Network Connection failed"
        }

        return@map null
    }

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

    // TODO: updated user data should be validate inside updateUserProfileUseCase
    fun onUpdateUserProfile() = viewModelScope.launch(defaultDispatcher) {
        state.bindingUserProfile?.let { updatedUserProfile ->
            // Start updating animation
            sendAction(Action.StartUpdatingUserProfile)
            // forward update action to UseCase
            val uploadProfile = UserProfileUploadDomainModel.from(updatedUserProfile, state!!.cacheImageUri)
            val params = UpdateUserProfileUseCase.Param(uploadProfile)
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

    private fun loadCachedUserSelectedImage() = viewModelScope.launch(defaultDispatcher) {
        consumeUserSelectedImageUseCase(Unit) {
            it.fold(
                onSucceed = { uri ->
                    decodeBitmap(uri)
                },
                onFail = { _ ->
                    sendAction(Action.CachedImageLoaded(null, null))
                }
            )
        }
    }

    private fun decodeBitmap(uri: Uri) = viewModelScope.launch(defaultDispatcher) {
        val param = GetBitmapUseCase.Param(uri)
        getBitmapUseCase(param) {
            it.fold(
                onSucceed = { bitmap ->
                    sendAction(Action.CachedImageLoaded(uri, bitmap))
                },
                onFail = { _ ->
                    sendAction(Action.CachedImageLoaded(null,null))
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
        sendAction(Action.Reload)
        loadUserProfile()
        loadCachedUserSelectedImage()
    }

    override fun onReduceState(action: Action): ViewState = when (action) {
        is Action.UserProfileLoaded -> state.copy(
            isUserProfileLoading = false,
            originalUserProfile = action.userProfile,
            bindingUserProfile = if (action.userProfile == null) null else action.userProfile.copy()
        )
        is Action.CachedImageLoaded -> state.copy(
            isCachedImageLoading = false,
            cacheImageUri = action.uri,
            cacheImage = action.bitmap
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
        is Action.Reload -> ViewState()
    }

    data class ViewState(
        val isUserProfileLoading: Boolean = true,
        val isUserProfileUpdating: Boolean = false,
        val isCachedImageLoading: Boolean = true,
        val isNetworkError: Boolean = false,
        val isServerError: Boolean = false,
        val originalUserProfile: UserDomainModel? = null,
        val bindingUserProfile: UserDomainModel? = null,
        val cacheImageUri: Uri? = null,
        val cacheImage: Bitmap? = null
    ) : BaseViewState

    sealed class Action : BaseAction {
        class UserProfileLoaded(val userProfile: UserDomainModel?) : Action()
        class FinishUpdatingUserProfile(val userProfile: UserDomainModel) : Action()
        class CachedImageLoaded(val uri: Uri?, val bitmap: Bitmap?) : Action()
        object StartUpdatingUserProfile : Action()
        object FailOnNetworkConnection : Action()
        object FailOnServerError : Action()
        object Reload: Action()
    }
}