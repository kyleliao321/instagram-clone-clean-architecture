package com.example.instagram_clone_clean_architecture.feature.post.presentation.view.post

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.example.instagram_clone_clean_architecture.FeaturePostNavGraphDirections
import com.example.instagram_clone_clean_architecture.app.domain.model.PostUploadDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.service.IntentService
import com.example.instagram_clone_clean_architecture.feature.post.domain.usecase.GetBitmapUseCase
import com.example.instagram_clone_clean_architecture.feature.post.domain.usecase.GetLoginUserUseCase
import com.example.instagram_clone_clean_architecture.feature.post.domain.usecase.GetUserSelectedImageUseCase
import com.example.instagram_clone_clean_architecture.feature.post.domain.usecase.UploadPostUseCase
import com.example.library_base.domain.exception.Failure
import com.example.library_base.presentation.navigation.NavigationManager
import com.example.library_base.presentation.viewmodel.BaseAction
import com.example.library_base.presentation.viewmodel.BaseViewModel
import com.example.library_base.presentation.viewmodel.BaseViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostViewModel(
    private val intentService: IntentService,
    private val navManager: NavigationManager,
    private val getLoginUserUseCase: GetLoginUserUseCase,
    private val getUserSelectedImageUseCase: GetUserSelectedImageUseCase,
    private val uploadPostUseCase: UploadPostUseCase,
    private val getBitmapUseCase: GetBitmapUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : BaseViewModel<PostViewModel.ViewState, PostViewModel.Action>(ViewState()) {

    val isDataLoading = Transformations.map(stateLiveData) {
        it.isLoginUserLoading || it.isUserSelectedImageLoading
    }

    val errorMessage = Transformations.map(stateLiveData) {
        if (it.isNetworkError) {
            return@map "Network Connection failed"
        }

        return@map null
    }

    fun promptToTakePhotoByCamera() = viewModelScope.launch(defaultDispatcher) {
        intentService.openCamera()
    }

    fun promptToGetPhotoFromGallery() = viewModelScope.launch(defaultDispatcher) {
        intentService.openPhotoGallery()
    }

    fun uploadPost(post: PostUploadDomainModel, belongUser: UserDomainModel) =
        viewModelScope.launch(defaultDispatcher) {
            sendAction(Action.StartUploading)
            val param = UploadPostUseCase.Param(post)
            uploadPostUseCase(param) {
                it.fold(
                    onSucceed = {
                        sendAction(Action.FinishUploading)
                        val navDir =
                            FeaturePostNavGraphDirections.featureProfileNavGraph(belongUser.id)
                        navManager.onNavEvent(navDir)
                    },
                    onFail = { failure ->
                        sendAction(Action.FinishUploading)
                        onFailure(failure)
                    }
                )
            }
        }

    private fun loadLoginUser() = viewModelScope.launch(defaultDispatcher) {
        getLoginUserUseCase(Unit) {
            it.fold(
                onSucceed = { userProfile ->
                    sendAction(Action.LoginUserLoaded(userProfile))
                },
                onFail = { failure ->
                    sendAction(Action.LoginUserLoaded(null))
                    onFailure(failure)
                }
            )
        }
    }

    private fun loadUserSelectedImage() = viewModelScope.launch(defaultDispatcher) {
        getUserSelectedImageUseCase(Unit) {
            it.fold(
                onSucceed = { image ->
                    sendAction(Action.UserSelectedImageLoaded(image))
                    image?.let { uri ->
                        decodeBitmap(uri)
                    }
                },
                onFail = { failure ->
                    sendAction(Action.UserSelectedImageLoaded(null))
                    onFailure(failure)
                }
            )
        }
    }

    private fun decodeBitmap(uri: Uri) = viewModelScope.launch(defaultDispatcher) {
        sendAction(Action.StartDecodeBitmap)
        val param = GetBitmapUseCase.Param(uri)
        getBitmapUseCase(param) {
            it.fold(
                onSucceed = { bitmap ->
                    sendAction(Action.BitmapDecoded(bitmap))
                },
                onFail = { failure ->
                    sendAction(Action.BitmapDecoded(null))
                    onFailure(failure)
                }
            )
        }
    }

    private fun onFailure(failure: Failure) = when (failure) {
        is Failure.PhotoGalleryServiceFail -> sendAction(Action.FailOnPhotoGalleryError)
        is Failure.CameraServiceFail -> sendAction(Action.FailOnCameraError)
        is Failure.LocalAccountNotFound -> sendAction(Action.FailOnLocalAccountError)
        is Failure.NetworkConnection -> sendAction(Action.FailOnNetworkConnection)
        is Failure.ServerError -> sendAction(Action.FailOnServerError)
        is Failure.PostNotComplete -> sendAction(Action.FailOnPostNotComplete)
        else -> IllegalArgumentException("unknown failure $failure in ${this::class.java}")
    }

    override fun onLoadData() {
        loadLoginUser()
        loadUserSelectedImage()
    }

    override fun onReduceState(action: Action): ViewState = when (action) {
        is Action.UserSelectedImageLoaded -> state.copy(
            isUserSelectedImageLoading = false,
            post = state.post.copy(imageUri = action.image)
        )
        is Action.LoginUserLoaded -> state.copy(
            isLoginUserLoading = false,
            loginUser = action.user,
            post = state.post.copy(belongUserId = action.user?.id)
        )
        is Action.BitmapDecoded -> state.copy(
            isBitmapDecoding = false,
            bitmap = action.bitmap
        )
        is Action.StartDecodeBitmap -> state.copy(
            isBitmapDecoding = true
        )
        is Action.StartUploading -> state.copy(
            isUploading = true,
            isNetworkError = false,
            isServerError = false
        )
        is Action.FinishUploading -> state.copy(
            isUploading = false
        )
        is Action.FailOnLocalAccountError -> state.copy(
            isLocalAccountError = true
        )
        is Action.FailOnCameraError -> state.copy(
            isCameraError = true
        )
        is Action.FailOnPhotoGalleryError -> state.copy(
            isPhotoGalleryError = true
        )
        is Action.FailOnNetworkConnection -> state.copy(
            isNetworkError = true
        )
        is Action.FailOnServerError -> state.copy(
            isServerError = true
        )
        is Action.FailOnPostNotComplete -> state.copy(
            isPostNotComplete = true
        )
    }

    data class ViewState(
        val isUserSelectedImageLoading: Boolean = true,
        val isLoginUserLoading: Boolean = true,
        val isBitmapDecoding: Boolean = false,
        val isUploading: Boolean = false,
        val isNetworkError: Boolean = false,
        val isServerError: Boolean = false,
        val isLocalAccountError: Boolean = false,
        val isCameraError: Boolean = false,
        val isPhotoGalleryError: Boolean = false,
        val isPostNotComplete: Boolean = false,
        val loginUser: UserDomainModel? = null,
        val post: PostUploadDomainModel = PostUploadDomainModel(),
        val bitmap: Bitmap? = null
    ) : BaseViewState

    sealed class Action : BaseAction {
        class UserSelectedImageLoaded(val image: Uri?) : Action()
        class LoginUserLoaded(val user: UserDomainModel?) : Action()
        class BitmapDecoded(val bitmap: Bitmap?) : Action()
        object StartUploading : Action()
        object FinishUploading : Action()
        object StartDecodeBitmap : Action()
        object FailOnLocalAccountError : Action()
        object FailOnCameraError : Action()
        object FailOnPhotoGalleryError : Action()
        object FailOnNetworkConnection : Action()
        object FailOnServerError : Action()
        object FailOnPostNotComplete : Action()
    }

}