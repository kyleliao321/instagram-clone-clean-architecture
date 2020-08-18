package com.example.instagram_clone_clean_architecture.feature.login.presentation.view.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.login.domain.repository.LoginRepository
import com.example.instagram_clone_clean_architecture.feature.login.domain.usercase.UserRegisterUseCase
import com.example.instagram_clone_clean_architecture.feature.login.presentation.view.login.LoginViewModel
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
class RegisterViewModelTest {

    @get:Rule
    val mainCoroutineRule = CoroutineTestRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @MockK(relaxed = true)
    internal lateinit var observer: Observer<RegisterViewModel.ViewState>

    @MockK(relaxed = true)
    internal lateinit var loginRepository: LoginRepository

    private lateinit var userRegisterUseCase: UserRegisterUseCase

    private lateinit var testViewModel: RegisterViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        userRegisterUseCase = UserRegisterUseCase(loginRepository, mainCoroutineRule.testDispatcher)

        testViewModel = RegisterViewModel(userRegisterUseCase, mainCoroutineRule.testDispatcher)

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
    fun `registerViewModel should initialize with correct view state`() {
        testViewModel.stateLiveData.value shouldBeEqualTo  RegisterViewModel.ViewState(
            isRegistering = false,
            isServerError = false,
            isNetworkError = false,
            userName = null,
            userPassword = null,
            registerUserProfile = null
        )
    }

    @Test
    fun `verify view state when register successfully`() {
        val mockUserName = "userName"
        val mockUserPassword = "userPassword"
        val mockRegisterUserProfile = mockk<UserDomainModel>(relaxed = true)

        // given
        testViewModel.stateLiveData.value!!.userName = mockUserName
        testViewModel.stateLiveData.value!!.userPassword = mockUserPassword
        every { runBlocking { loginRepository.userRegister(any(), any()) } } returns Either.Success(mockRegisterUserProfile)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.userRegister() }

        // expect
        verify(exactly = 3) { observer.onChanged(any()) } // init, startLogin, finishLogin
        testViewModel.stateLiveData.value shouldBeEqualTo RegisterViewModel.ViewState(
            isRegistering = false,
            isNetworkError = false,
            isServerError = false,
            userName = null,
            userPassword = null,
            registerUserProfile = mockRegisterUserProfile
        )
    }

    @Test
    fun `verify view state when login fail`() {
        val mockUserName = "userName"
        val mockUserPassword = "userPassword"

        // given
        testViewModel.stateLiveData.value!!.userName = mockUserName
        testViewModel.stateLiveData.value!!.userPassword = mockUserPassword
        every { runBlocking { loginRepository.userRegister(any(), any()) } } returns Either.Failure(
            Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.userRegister() }

        // expect
        verify(exactly = 4) { observer.onChanged(any()) } // init, startLogin, finishLogin, fail
        testViewModel.stateLiveData.value shouldBeEqualTo RegisterViewModel.ViewState(
            isRegistering = false,
            isNetworkError = true,
            isServerError = false,
            userName = null,
            userPassword = null,
            registerUserProfile = null
        )
    }

}