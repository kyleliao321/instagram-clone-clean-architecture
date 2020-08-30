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
import timber.log.Timber

class MainViewModel(
    private val navManager: NavigationManager,
    private val getLocalLoginUserIdUseCase: GetLocalLoginUserIdUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : BaseViewModel<MainViewModel.ViewState, MainViewModel.Action>(
    ViewState()
) {

    enum class NavGraphDestinations {
        Login, Profile, Search
    }

    fun onNavigateToProfile() {
        if (state.localUserId == null) {
            throw IllegalStateException("Local Login User id should not be null in ${this::class.java}")
        } else {
            sendAction(Action.NavigateToNewDestination(NavGraphDestinations.Profile))
            val navDir = FeatureSearchNavGraphDirections.featureProfileNavGraph(state.localUserId!!)
            navManager.onNavEvent(navDir)
        }
    }

    fun onNavigateToSearch() {
        sendAction(Action.NavigateToNewDestination(NavGraphDestinations.Search))
        val navDir = FeatureProfileNavGraphDirections.featureSearchNavGraph()
        navManager.onNavEvent(navDir)
    }

    private fun loadLocalUserId() = viewModelScope.launch(defaultDispatcher) {
        getLocalLoginUserIdUseCase(Unit) {
            it.fold(
                onSucceed = { id ->
                    sendAction(Action.LocalUserIdLoaded(id))
                    onNavigateToProfile()
                    sendAction(Action.NavigateToNewDestination(NavGraphDestinations.Profile))
                },
                onFail = { failure ->
                    sendAction(Action.LocalUserIdLoaded(null))
                    onFailure(failure)
                }
            )
        }
    }

    override fun onLoadData() {
        sendAction(Action.ReloadData)
        loadLocalUserId()
    }

    override fun onReduceState(action: Action): ViewState = when (action) {
        is Action.NavigateToNewDestination -> state.copy(
            navDestination = action.destination
        )
        is Action.LocalUserIdLoaded -> state.copy(
            isLocalLoginUserIdLoading = false,
            localUserId = action.id
        )
        is Action.FailOnLocalAccountError -> state.copy(
            isLocalAccountError = true
        )
        is Action.ReloadData -> state.copy(
            isLocalLoginUserIdLoading = true,
            isLocalAccountError = false
        )
    }

    private fun onFailure(failure: Failure) = when (failure) {
        is Failure.LocalAccountNotFound -> sendAction(Action.FailOnLocalAccountError)
        else -> throw IllegalStateException("Unknown failure $failure in ${this::class.java}")
    }

    data class ViewState(
        val isLocalLoginUserIdLoading: Boolean = true,
        val isLocalAccountError: Boolean = false,
        val localUserId: Int? = null,
        val navDestination: NavGraphDestinations = NavGraphDestinations.Login
    ) : BaseViewState

    sealed class Action : BaseAction {
        class LocalUserIdLoaded(val id: Int?) : Action()
        class NavigateToNewDestination(val destination: NavGraphDestinations) : Action()
        object FailOnLocalAccountError : Action()
        object ReloadData : Action()
    }

}