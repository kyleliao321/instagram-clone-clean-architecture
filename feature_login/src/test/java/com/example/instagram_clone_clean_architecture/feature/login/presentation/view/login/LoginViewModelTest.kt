package com.example.instagram_clone_clean_architecture.feature.login.presentation.view.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.login.domain.repository.LoginRepository
import com.example.instagram_clone_clean_architecture.feature.login.domain.usercase.GetLocalLoginUserDataUseCase
import com.example.instagram_clone_clean_architecture.feature.login.domain.usercase.UserLoginUseCase
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import com.example.library_base.presentation.navigation.NavigationManager
import com.example.library_test_utils.runBlockingTest
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LoginViewModelTest {

    @get:Rule
    val mainCoroutineRule = com.example.library_test_utils.CoroutineTestRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @MockK(relaxed = true)
    internal lateinit var observer: Observer<LoginViewModel.ViewState>

    @MockK(relaxed = true)
    internal lateinit var navigationManager: NavigationManager

    @MockK(relaxed = true)
    internal lateinit var loginRepository: LoginRepository

    private lateinit var getLocalLoginUserDataUseCase: GetLocalLoginUserDataUseCase

    private lateinit var userLoginUseCase: UserLoginUseCase

    private lateinit var testViewModel: LoginViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        getLocalLoginUserDataUseCase =
            spyk(GetLocalLoginUserDataUseCase(loginRepository, mainCoroutineRule.testDispatcher))
        userLoginUseCase = spyk(UserLoginUseCase(loginRepository, mainCoroutineRule.testDispatcher))

        testViewModel = LoginViewModel(
            navigationManager,
            userLoginUseCase,
            getLocalLoginUserDataUseCase,
            mainCoroutineRule.testDispatcher
        )

        testViewModel.stateLiveData.observeForever(observer)
    }

    @After
    fun teardown() {
        testViewModel.stateLiveData.removeObserver(observer)
    }

    @Test
    fun `should initialize with correct view state`() {
        testViewModel.stateLiveData.value shouldBeEqualTo LoginViewModel.ViewState(
            isLocalUserDataLoading = true,
            isServerError = false,
            isNetworkError = false,
            isLoginFail = false,
            isFormValidateFail = false,
            isLoginRunning = false,
            userName = null,
            userPassword = null
        )
    }

    @Test
    fun `verify view state when login succeed`() {
        val mockUserName = "1"
        val mockUserPassword = "2"
        val mockProfile = mockk<UserDomainModel>(relaxed = true)

        // given
        coEvery { userLoginUseCase.run(any()) } returns Either.Success(mockProfile)

        // when
        mainCoroutineRule.runBlockingTest {
            testViewModel.userLogin(
                mockUserName,
                mockUserPassword
            )
        }

        // expect
        verify(exactly = 1) { navigationManager.onNavEvent(any()) }
        verify(exactly = 3) { observer.onChanged(any()) } // init, start, finish
        testViewModel.stateLiveData.value shouldBeEqualTo LoginViewModel.ViewState()
    }

    @Test
    fun `verify view state when login failed`() {
        val mockUserName = "1"
        val mockUserPassword = "2"

        // given
        coEvery { userLoginUseCase.run(any()) } returns Either.Failure(Failure.LoginUserNameOrPasswordNotMatched)

        // when
        mainCoroutineRule.runBlockingTest {
            testViewModel.userLogin(
                mockUserName,
                mockUserPassword
            )
        }

        // expect
        verify(exactly = 4) { observer.onChanged(any()) } // init, start, finish, fail
        testViewModel.stateLiveData.value shouldBeEqualTo LoginViewModel.ViewState(
            isLoginFail = true
        )
    }

    @Test
    fun `verify view state when login failed on network connection`() {
        val mockUserName = "1"
        val mockUserPassword = "2"

        // given
        coEvery { userLoginUseCase.run(any()) } returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest {
            testViewModel.userLogin(
                mockUserName,
                mockUserPassword
            )
        }

        // expect
        verify { observer.onChanged(any()) } // init, start, finish, fail
        testViewModel.stateLiveData.value shouldBeEqualTo LoginViewModel.ViewState(
            isNetworkError = true
        )
    }

    @Test
    fun `verify view state if local user data exist and login succeed`() {
        val mockUserName = "1"
        val mockUserPassword = "2"
        val mockProfile = mockk<UserDomainModel>(relaxed = true)
        val mockLocalUserData = mockk<GetLocalLoginUserDataUseCase.Result>(relaxed = true)

        // given
        every { mockLocalUserData.userName } returns mockUserName
        every { mockLocalUserData.userPassword } returns mockUserPassword
        every { runBlocking { userLoginUseCase.run(any()) } } returns Either.Success(mockProfile)
        every { runBlocking { getLocalLoginUserDataUseCase.run(any()) } } returns Either.Success(
            mockLocalUserData
        )

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        verify(exactly = 1) { navigationManager.onNavEvent(any()) }
        verify(exactly = 4) { observer.onChanged(any()) } // init, start, finish, loaded
        testViewModel.stateLiveData.value shouldBeEqualTo LoginViewModel.ViewState(
            isLoginFail = false,
            isFormValidateFail = false,
            isLoginRunning = false,
            isNetworkError = false,
            isServerError = false,
            isLocalUserDataLoading = false
        )
    }

    @Test
    fun `verify view state if local user data not exist`() {
        // given
        every { runBlocking { getLocalLoginUserDataUseCase.run(any()) } } returns Either.Failure(
            Failure.LocalAccountNotFound
        )

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        verify(exactly = 2) { observer.onChanged(any()) } // init, loaded
        testViewModel.stateLiveData.value shouldBeEqualTo LoginViewModel.ViewState(
            isLoginFail = false,
            isFormValidateFail = false,
            isLoginRunning = false,
            isNetworkError = false,
            isServerError = false,
            isLocalUserDataLoading = false
        )
    }
}