package com.example.instagram_clone_clean_architecture.feature.login.presentation.view.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.login.domain.repository.LoginRepository
import com.example.instagram_clone_clean_architecture.feature.login.domain.usercase.UpdateLocalLoginUserIdUseCase
import com.example.instagram_clone_clean_architecture.feature.login.domain.usercase.UserLoginUseCase
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.CoroutineTestRule
import com.example.library_base.domain.utility.Either
import com.example.library_base.domain.utility.runBlockingTest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LoginViewModelTest {

    @get:Rule
    val mainCoroutineRule = CoroutineTestRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @MockK(relaxed = true)
    internal lateinit var observer: Observer<LoginViewModel.ViewState>

    @MockK(relaxed = true)
    internal lateinit var loginRepository: LoginRepository

    private lateinit var userLoginUseCase: UserLoginUseCase

    private lateinit var updateLocalLoginUserIdUseCase: UpdateLocalLoginUserIdUseCase

    private lateinit var testViewModel: LoginViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        userLoginUseCase = UserLoginUseCase(loginRepository, mainCoroutineRule.testDispatcher)
        updateLocalLoginUserIdUseCase = UpdateLocalLoginUserIdUseCase(loginRepository, mainCoroutineRule.testDispatcher)

        testViewModel = LoginViewModel(userLoginUseCase, updateLocalLoginUserIdUseCase, mainCoroutineRule.testDispatcher)

        testViewModel.stateLiveData.observeForever(observer)
    }

    @After
    fun teardown() {
        testViewModel.stateLiveData.removeObserver(observer)
    }

    /**
     * ViewState typical test
     */
    @Test
    fun `loginUserViewModel should initialize with correct view state`() {
        testViewModel.stateLiveData.value shouldBeEqualTo LoginViewModel.ViewState(
            isLoginRunning = false,
            isServerError = false,
            isNetworkError = false,
            userPassword = null,
            userName = null,
            loginUserProfile = null
        )
    }

    @Test
    fun `verify view state when login successfully`() {
        val mockUserName = "userName"
        val mockUserPassword = "userPassword"
        val mockLoginUserProfile = mockk<UserDomainModel>(relaxed = true)

        // given
        testViewModel.stateLiveData.value!!.userName = mockUserName
        testViewModel.stateLiveData.value!!.userPassword = mockUserPassword
        every { runBlocking { loginRepository.updateLocalLoginUserId(any()) } } returns Either.Success(Unit)
        every { runBlocking { loginRepository.userLogin(any(), any()) } } returns Either.Success(mockLoginUserProfile)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.userLogin() }

        // expect
        verify(exactly = 3) { observer.onChanged(any()) } // init, startLogin, finishLogin
        testViewModel.stateLiveData.value shouldBeEqualTo LoginViewModel.ViewState(
            isLoginRunning = false,
            isNetworkError = false,
            isServerError = false,
            userName = null,
            userPassword = null,
            loginUserProfile = mockLoginUserProfile
        )
    }

    @Test
    fun `verify view state when login fail`() {
        val mockUserName = "userName"
        val mockUserPassword = "userPassword"

        // given
        testViewModel.stateLiveData.value!!.userName = mockUserName
        testViewModel.stateLiveData.value!!.userPassword = mockUserPassword
        every { runBlocking { loginRepository.updateLocalLoginUserId(any()) } } returns Either.Success(Unit)
        every { runBlocking { loginRepository.userLogin(any(), any()) } } returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.userLogin() }

        // expect
        verify(exactly = 4) { observer.onChanged(any()) } // init, startLogin, finishLogin, fail
        testViewModel.stateLiveData.value shouldBeEqualTo LoginViewModel.ViewState(
            isLoginRunning = false,
            isNetworkError = true,
            isServerError = false,
            userName = null,
            userPassword = null,
            loginUserProfile = null
        )
    }

}