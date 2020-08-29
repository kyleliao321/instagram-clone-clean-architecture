package com.example.instagram_clone_clean_architecture.feature.login.presentation.view.login

import androidx.lifecycle.viewModelScope
import com.example.instagram_clone_clean_architecture.FeatureLoginNavGraphDirections
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.login.domain.usercase.UpdateLocalLoginUserIdUseCase
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
    private val updateLocalLoginUserIdUseCase: UpdateLocalLoginUserIdUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : BaseViewModel<LoginViewModel.ViewState, LoginViewModel.Action>(ViewState()) {

    fun onNavigateToRegisterFragment() {
        val navDir = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        navManager.onNavEvent(navDir)
    }

    fun onNavigationToUserProfile(userId: Int) {
        val navDir = FeatureLoginNavGraphDirections.featureProfileNavGraph(userId)
        navManager.onNavEvent(navDir)
    }

    fun userLogin() = viewModelScope.launch(defaultDispatcher) {
        if (state.userName != null && state.userPassword != null) {
            if (state.userName!!.isNotBlank() && state.userPassword!!.isNotBlank()) {
                sendAction(Action.StartLogin)
                var loginUserProfile: UserDomainModel? = null

                val loginParam = UserLoginUseCase.Param(state.userName!!, state.userPassword!!)

                userLoginUseCase(loginParam) {
                    it.fold(
                        onSucceed = { userProfile ->
                            loginUserProfile = userProfile
                        },
                        onFail = { failure ->
                            sendAction(Action.FinishLogin(null))
                            onFailure(failure)
                        }
                    )
                }

                loginUserProfile?.let { userProfile ->
                    val localLoginParam = UpdateLocalLoginUserIdUseCase.Param(userProfile.id)
                    updateLocalLoginUserIdUseCase(localLoginParam) {
                        it.fold(
                            onSucceed = {
                                sendAction(Action.FinishLogin(userProfile))
                            },
                            onFail = { failure ->
                                sendAction(Action.FinishLogin(null))
                                onFailure(failure)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun onFailure(failure: Failure) = when (failure) {
        is Failure.ServerError -> sendAction(Action.FailOnServerError)
        is Failure.NetworkConnection -> sendAction(Action.FailOnNetworkConnection)
        is Failure.LoginUserNameOrPasswordNotMatched -> sendAction(Action.FailOnLoginData)
        else -> throw IllegalStateException("Unknown failure $failure in ${this::class.java}")
    }

    override fun onLoadData() { }

    override fun onReduceState(action: Action): ViewState = when (action) {
        is Action.StartLogin -> state.copy(
            isLoginRunning = true,
            isLoginFail = false
        )
        is Action.FinishLogin -> state.copy(
            isLoginRunning = false,
            userName = null,
            userPassword = null,
            loginUserProfile = action.userProfile
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
    }

    data class ViewState(
        val isLoginRunning: Boolean = false,
        val isNetworkError: Boolean = false,
        val isServerError: Boolean = false,
        val isLoginFail: Boolean = false,
        var userName: String? = null,
        var userPassword: String? = null,
        val loginUserProfile: UserDomainModel? = null
    ) : BaseViewState

    sealed class Action : BaseAction {
        object StartLogin : Action()
        class FinishLogin(val userProfile: UserDomainModel?) : Action()
        object FailOnNetworkConnection : Action()
        object FailOnServerError: Action()
        object FailOnLoginData: Action()
    }

}