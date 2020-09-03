package com.example.instagram_clone_clean_architecture.feature.login.presentation.view.login

import android.view.View
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.example.instagram_clone_clean_architecture.FeatureLoginNavGraphDirections
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.login.domain.usercase.GetLocalLoginUserDataUseCase
import com.example.instagram_clone_clean_architecture.feature.login.domain.usercase.UserLoginUseCase
import com.example.library_base.domain.exception.Failure
import com.example.library_base.presentation.navigation.NavigationManager
import com.example.library_base.presentation.viewmodel.BaseAction
import com.example.library_base.presentation.viewmodel.BaseViewModel
import com.example.library_base.presentation.viewmodel.BaseViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(
    private val navManager: NavigationManager,
    private val userLoginUseCase: UserLoginUseCase,
    private val getLocalLoginUserDataUseCase: GetLocalLoginUserDataUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : BaseViewModel<LoginViewModel.ViewState, LoginViewModel.Action>(ViewState()) {

    val isDataLoading = Transformations.map(stateLiveData) {
        it.isLocalUserDataLoading || it.isLoginRunning
    }

    val errorMessage = Transformations.map(stateLiveData) {
        if (it.isNetworkError) {
            return@map "Network Connection failed"
        }

        return@map null
    }

    fun onNavigateToRegisterFragment() {
        val navDir = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        navManager.onNavEvent(navDir)
    }

    fun userLogin(userName: String?, userPassword: String?) = viewModelScope.launch(defaultDispatcher) {
        if (userName != null && userPassword != null) {
            if (userName!!.isNotBlank() && userPassword!!.isNotBlank()) {
                sendAction(Action.StartLogin)
                val loginParam = UserLoginUseCase.Param(userName!!, userPassword!!)

                userLoginUseCase(loginParam) {
                    it.fold(
                        onSucceed = { userProfile ->
                            val navDir = FeatureLoginNavGraphDirections.featureProfileNavGraph(userProfile.id)
                            navManager.onNavEvent(navDir)
                            sendAction(Action.FinishLogin)
                        },
                        onFail = { failure ->
                            sendAction(Action.FinishLogin)
                            onFailure(failure)
                        }
                    )
                }
            }
        }
    }

    private fun loadLocalUserData() = viewModelScope.launch(defaultDispatcher) {
        getLocalLoginUserDataUseCase(Unit) {
            it.fold(
                onSucceed = { result ->
                    userLogin(result.userName, result.userPassword)
                    sendAction(Action.LocalUserDataLoaded)
                },
                onFail = { sendAction(Action.LocalUserDataLoaded) }
            )
        }
    }

    private fun onFailure(failure: Failure) = when (failure) {
        is Failure.ServerError -> sendAction(Action.FailOnServerError)
        is Failure.NetworkConnection -> sendAction(Action.FailOnNetworkConnection)
        is Failure.LoginUserNameOrPasswordNotMatched -> sendAction(Action.FailOnLoginData)
        else -> throw IllegalStateException("Unknown failure $failure in ${this::class.java}")
    }

    override fun onLoadData() {
        sendAction(Action.StartDataLoading)
        loadLocalUserData()
    }

    override fun onReduceState(action: Action): ViewState = when (action) {
        is Action.StartLogin -> state.copy(
            isLoginRunning = true,
            isLoginFail = false,
            isServerError = false,
            isNetworkError = false
        )
        is Action.FinishLogin -> state.copy(
            isLoginRunning = false,
            userName = null,
            userPassword = null
        )
        is Action.FailOnNetworkConnection -> state.copy(
            isNetworkError = true
        )
        is Action.FailOnServerError -> state.copy(
            isServerError = true
        )
        is Action.FailOnLoginData -> state.copy(
            isLoginFail = true
        )
        is Action.LocalUserDataLoaded -> state.copy(
            isLocalUserDataLoading = false
        )
        is Action.StartDataLoading -> ViewState()
    }

    data class ViewState(
        val isLocalUserDataLoading: Boolean = true,
        val isLoginRunning: Boolean = false,
        val isNetworkError: Boolean = false,
        val isServerError: Boolean = false,
        val isLoginFail: Boolean = false,
        var userName: String? = null,
        var userPassword: String? = null
    ) : BaseViewState

    sealed class Action : BaseAction {
        object StartLogin : Action()
        object FinishLogin : Action()
        object FailOnNetworkConnection : Action()
        object FailOnServerError: Action()
        object FailOnLoginData: Action()
        object LocalUserDataLoaded: Action()
        object StartDataLoading: Action()
    }

}