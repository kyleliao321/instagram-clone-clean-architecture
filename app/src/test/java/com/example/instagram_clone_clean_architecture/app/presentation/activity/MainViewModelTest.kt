package com.example.instagram_clone_clean_architecture.app.presentation.activity

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.app.domain.repository.AppRepository
import com.example.instagram_clone_clean_architecture.app.domain.usecase.GetLocalLoginUserIdUseCase
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.CoroutineTestRule
import com.example.library_base.domain.utility.Either
import com.example.library_base.domain.utility.runBlockingTest
import com.example.library_base.presentation.navigation.NavigationManager
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.invoking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldThrow
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MainViewModelTest {

    @get:Rule
    val mainCoroutineRule = CoroutineTestRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @MockK(relaxed = true)
    internal lateinit var observer: Observer<MainViewModel.ViewState>

    @MockK(relaxed = true)
    internal lateinit var navManager: NavigationManager

    @MockK(relaxed = true)
    internal lateinit var appRepository: AppRepository

    private lateinit var getLocalLoginUserIdUseCase: GetLocalLoginUserIdUseCase

    private lateinit var testViewModel: MainViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        getLocalLoginUserIdUseCase = GetLocalLoginUserIdUseCase(appRepository, mainCoroutineRule.testDispatcher)

        testViewModel = MainViewModel(navManager, getLocalLoginUserIdUseCase, mainCoroutineRule.testDispatcher)

        testViewModel.stateLiveData.observeForever(observer)
    }

    @After
    fun teardown() {
        testViewModel.stateLiveData.removeObserver(observer)
    }

    /**
     * Navigation Test
     */
    @Test
    fun `navigate to user profile should succeed when user id in view state is not null`() {
        val mockId = 1

        // given
        every { runBlocking { appRepository.getLocalLoginUserId() } } returns Either.Success(mockId)

        // when
        mainCoroutineRule.runBlockingTest {
            testViewModel.loadData()
            testViewModel.onNavigateToProfile()
        }

        // expect
        verify(exactly = 1) { navManager.onNavEvent(any()) }
    }

    @Test
    fun `navigate to user profile should throw exception when user id in view state is null`() {
        // given
        every { runBlocking { appRepository.getLocalLoginUserId() } } returns Either.Failure(Failure.LocalAccountNotFound)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }
        invoking { testViewModel.onNavigateToProfile() } shouldThrow(IllegalStateException()::class)

    }

    /**
     * ViewState typical test
     */
    @Test
    fun `viewModel should initialize with correct view state`() {
        testViewModel.stateLiveData.value shouldBeEqualTo MainViewModel.ViewState(
            isLocalAccountError = false,
            isLocalLoginUserIdLoading = true,
            localUserId = null
        )
    }

}