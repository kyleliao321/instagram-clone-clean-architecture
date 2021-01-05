package com.example.instagram_clone_clean_architecture.app.presentation.activity

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.example.instagram_clone_clean_architecture.FeatureProfileNavGraphDirections
import com.example.instagram_clone_clean_architecture.FeatureSearchNavGraphDirections
import com.example.instagram_clone_clean_architecture.app.domain.usecase.*
import com.example.library_base.domain.exception.Failure
import com.example.library_base.presentation.navigation.NavigationManager
import com.example.library_base.presentation.viewmodel.BaseAction
import com.example.library_base.presentation.viewmodel.BaseViewModel
import com.example.library_base.presentation.viewmodel.BaseViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(
    private val navManager: NavigationManager,
    private val cacheUserSelectedImageUseCase: CacheUserSelectedImageUseCase,
    private val getCachedLoginUserUseCase: GetCachedLoginUserUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : BaseViewModel<MainViewModel.ViewState, MainViewModel.Action>(
    ViewState()
) {

    val isLogin: LiveData<Boolean> = Transformations.map(stateLiveData) {
        it.localUserId != null
    }

    fun onNavigateToProfile() {
        if (state.localUserId == null) {
            throw IllegalStateException("Local Login User id should not be null in ${this::class.java}")
        } else {
            val navDir = FeatureSearchNavGraphDirections.featureProfileNavGraph(state.localUserId!!)
            navManager.onNavEvent(navDir)
        }
    }

    fun onNavigateToSearch() {
        val navDir = FeatureProfileNavGraphDirections.featureSearchNavGraph()
        navManager.onNavEvent(navDir)
    }

    fun cacheUserSelectedImage(imageUri: Uri) = viewModelScope.launch(defaultDispatcher) {
        val param = CacheUserSelectedImageUseCase.Param(imageUri)

        cacheUserSelectedImageUseCase(param)
    }

    private fun loadLocalUserData() = viewModelScope.launch(defaultDispatcher) {
        getCachedLoginUserUseCase(Unit) {
            it.fold(
                onSucceed = { userProfile ->
                    sendAction(Action.LocalUserDataLoaded(userProfile.id))
                },
                onFail = { failure ->
                    sendAction(Action.LocalUserDataLoaded(null))
                    onFailure(failure)
                }
            )
        }
    }

    override fun onLoadData() {
        sendAction(Action.ReloadData)
        loadLocalUserData()
    }

    override fun onReduceState(action: Action): ViewState = when (action) {
        is Action.LocalUserDataLoaded -> state.copy(
            isLocalUserDataLoading = false,
            localUserId = action.userId
        )
        is Action.FailOnLocalAccountError -> state.copy(
            isLocalAccountError = true
        )
        is Action.ReloadData -> state.copy(
            isLocalUserDataLoading = true,
            isLocalAccountError = false
        )
    }

    private fun onFailure(failure: Failure) = when (failure) {
        is Failure.LocalAccountNotFound -> sendAction(Action.FailOnLocalAccountError)
        is Failure.CacheNotFound -> sendAction(Action.FailOnLocalAccountError)
        else -> throw IllegalStateException("Unknown failure $failure in ${this::class.java}")
    }

    data class ViewState(
        val isLocalUserDataLoading: Boolean = true,
        val isLocalAccountError: Boolean = false,
        val localUserId: String? = null
    ) : BaseViewState

    sealed class Action : BaseAction {
        class LocalUserDataLoaded(val userId: String?): Action()
        object FailOnLocalAccountError : Action()
        object ReloadData : Action()
    }

}