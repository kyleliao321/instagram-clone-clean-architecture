package com.example.instagram_clone_clean_architecture.feature.login.presentation.view.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.feature.login.domain.repository.LoginRepository
import com.example.instagram_clone_clean_architecture.feature.login.domain.usercase.UserRegisterUseCase
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import com.example.library_base.presentation.navigation.NavigationManager
import com.example.library_test_utils.runBlockingTest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RegisterViewModelTest {

    @get:Rule
    val mainCoroutineRule = com.example.library_test_utils.CoroutineTestRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @MockK(relaxed = true)
    internal lateinit var observer: Observer<RegisterViewModel.ViewState>

    @MockK(relaxed = true)
    internal lateinit var navManager: NavigationManager

    @MockK(relaxed = true)
    internal lateinit var loginRepository: LoginRepository

    private lateinit var userRegisterUseCase: UserRegisterUseCase

    private lateinit var testViewModel: RegisterViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        userRegisterUseCase =
            spyk(UserRegisterUseCase(loginRepository, mainCoroutineRule.testDispatcher))

        testViewModel =
            RegisterViewModel(navManager, userRegisterUseCase, mainCoroutineRule.testDispatcher)

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
        testViewModel.stateLiveData.value shouldBeEqualTo RegisterViewModel.ViewState(
            isRegistering = false,
            isServerError = false,
            isNetworkError = false,
            userName = null,
            userPassword = null
        )
    }

    @Test
    fun `verify view state when register successfully`() {
        val mockUserName = "userName"
        val mockUserPassword = "userPassword"

        // given
        every { runBlocking { userRegisterUseCase.run(any()) } } returns Either.Success(Unit)

        // when
        mainCoroutineRule.runBlockingTest {
            testViewModel.userRegister(
                mockUserName,
                mockUserPassword
            )
        }

        // expect
        verify(exactly = 3) { observer.onChanged(any()) } // init, startRegister, finishRegister
        testViewModel.stateLiveData.value shouldBeEqualTo RegisterViewModel.ViewState(
            isRegistering = false,
            isNetworkError = false,
            isServerError = false,
            userName = null,
            userPassword = null
        )
    }

    @Test
    fun `verify view state when register fail on network connection`() {
        val mockUserName = "userName"
        val mockUserPassword = "userPassword"

        // given
        every { runBlocking { userRegisterUseCase.run(any()) } } returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest {
            testViewModel.userRegister(
                mockUserName,
                mockUserPassword
            )
        }

        // expect
        verify(exactly = 4) { observer.onChanged(any()) } // init, startRegister, finishRegister, fail
        testViewModel.stateLiveData.value shouldBeEqualTo RegisterViewModel.ViewState(
            isRegistering = false,
            isNetworkError = true,
            isServerError = false,
            userName = null,
            userPassword = null
        )
    }

    @Test
    fun `verify view state when userRegisterUseCase return form data not complete failure`() {
        // given
        val mockUserName = null
        val mockUserPassword = null

        every { runBlocking { userRegisterUseCase.run(any()) } } returns Either.Failure(Failure.FormDataNotComplete)

        // when
        mainCoroutineRule.runBlockingTest {
            testViewModel.userRegister(
                mockUserName,
                mockUserPassword
            )
        }

        // expect
        verify(exactly = 4) { observer.onChanged(any()) } // init, startRegister, finishRegister, fail
        testViewModel.stateLiveData.value shouldBeEqualTo RegisterViewModel.ViewState(
            isRegistering = false,
            isNetworkError = false,
            isServerError = false,
            isFormValidateFail = true,
            userName = null,
            userPassword = null
        )
    }

}