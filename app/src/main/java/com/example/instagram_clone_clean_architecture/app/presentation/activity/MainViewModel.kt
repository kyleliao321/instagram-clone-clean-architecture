package com.example.instagram_clone_clean_architecture.app.presentation.activity

import androidx.lifecycle.viewModelScope
import com.example.instagram_clone_clean_architecture.FeatureProfileNavGraphDirections
import com.example.instagram_clone_clean_architecture.FeatureSearchNavGraphDirections
import com.example.instagram_clone_clean_architecture.app.domain.usecase.GetLocalLoginUserIdUseCase
import com.example.library_base.domain.exception.Failure
import com.example.library_base.presentation.navigation.NavigationManager
import com.example.library_base.presentation.viewmodel.BaseAction
import com.example.library_base.presentation.viewmodel.BaseViewModel
import com.example.library_base.presentation.viewmodel.BaseViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val navManager: NavigationManager,
    private val getLocalLoginUserIdUseCase: GetLocalLoginUserIdUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : BaseViewModel<MainViewModel.ViewState, MainViewModel.Action>(
    ViewState()
) {

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

    private fun loadLocalUserId() = viewModelScope.launch(defaultDispatcher) {
        getLocalLoginUserIdUseCase(Unit) {
            it.fold(
                onSucceed = { id ->
                    sendAction(
                        Action.LocalUserIdLoaded(
                            id
                        )
                    )
                },
                onFail = { failure ->
                    sendAction(
                        Action.LocalUserIdLoaded(
                            null
                        )
                    )
                    onFailure(failure)
                }
            )
        }
    }

    override fun onLoadData() {
        loadLocalUserId()
    }

    override fun onReduceState(action: Action): ViewState = when (action) {
        is Action.LocalUserIdLoaded -> state.copy(
            isLocalLoginUserIdLoading = false,
            localUserId = action.id
        )
        is Action.FailOnLocalAccountError -> state.copy(
            isLocalAccountError = true
        )
    }

    private fun onFailure(failure: Failure) = when (failure) {
        is Failure.LocalAccountNotFound -> sendAction(Action.FailOnLocalAccountError)
        else -> throw IllegalStateException("Unknown failure $failure in ${this::class.java}")
    }

    data class ViewState(
        val isLocalLoginUserIdLoading: Boolean = true,
        val isLocalAccountError: Boolean = false,
        val localUserId: Int? = null
    ) : BaseViewState

    sealed class Action : BaseAction {
        class LocalUserIdLoaded(val id: Int?) : Action()
        object FailOnLocalAccountError : Action()
    }

}