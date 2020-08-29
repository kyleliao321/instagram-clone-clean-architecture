package com.example.instagram_clone_clean_architecture.feature.login.presentation.view.register

import androidx.lifecycle.viewModelScope
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.login.domain.usercase.UserRegisterUseCase
import com.example.library_base.domain.exception.Failure
import com.example.library_base.presentation.navigation.NavigationManager
import com.example.library_base.presentation.viewmodel.BaseAction
import com.example.library_base.presentation.viewmodel.BaseViewModel
import com.example.library_base.presentation.viewmodel.BaseViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val navManager: NavigationManager,
    private val userRegisterUseCase: UserRegisterUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : BaseViewModel<RegisterViewModel.ViewState, RegisterViewModel.Action>(ViewState()) {

    private fun navigateToLoginFragment() {
        val navDir = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
        navManager.onNavEvent(navDir)
    }

    fun userRegister() = viewModelScope.launch(defaultDispatcher) {
        if (state.userName != null && state.userPassword != null) {
            if (state.userName!!.isNotBlank() && state.userPassword!!.isNotBlank()) {
                sendAction(Action.StartRegister)
                val param = UserRegisterUseCase.Param(state.userName!!, state.userPassword!!)

                userRegisterUseCase(param) {
                    it.fold(
                        onSucceed = { userProfile ->
                            sendAction(Action.FinishRegister(userProfile))
                            navigateToLoginFragment()
                        },
                        onFail = { failure ->
                            sendAction(Action.FinishRegister(null))
                            onFailure(failure)
                        }
                    )
                }
            }
        }
    }

    private fun onFailure(failure: Failure) = when (failure) {
        is Failure.NetworkConnection -> sendAction(Action.FailOnNetworkConnection)
        is Failure.ServerError -> sendAction(Action.FailOnServerError)
        else -> throw IllegalStateException("Unknown failure $failure in ${this::class.java}")
    }

    override fun onLoadData() {}

    override fun onReduceState(action: Action): ViewState = when (action) {
        is Action.FinishRegister -> state.copy(
            isRegistering = false,
            userPassword = null,
            userName = null,
            registerUserProfile = action.userProfile
        )
        is Action.StartRegister -> state.copy(
            isRegistering = true
        )
        is Action.FailOnNetworkConnection -> state.copy(
            isNetworkError = true
        )
        is Action.FailOnServerError -> state.copy(
            isServerError = true
        )
    }

    data class ViewState(
        val isRegistering: Boolean = false,
        val isNetworkError: Boolean = false,
        val isServerError: Boolean = false,
        var userName: String? = null,
        var userPassword: String? = null,
        val registerUserProfile: UserDomainModel? = null
    ) : BaseViewState

    sealed class Action : BaseAction {
        class FinishRegister(val userProfile: UserDomainModel?) : Action()
        object StartRegister : Action()
        object FailOnNetworkConnection : Action()
        object FailOnServerError : Action()
    }
}