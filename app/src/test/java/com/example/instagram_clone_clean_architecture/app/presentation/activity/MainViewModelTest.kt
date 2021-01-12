package com.example.instagram_clone_clean_architecture.app.presentation.activity

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.app.domain.repository.AppRepository
import com.example.instagram_clone_clean_architecture.app.domain.usecase.CacheUserSelectedImageUseCase
import com.example.instagram_clone_clean_architecture.app.domain.usecase.GetCachedLoginUserUseCase
import com.example.library_base.presentation.navigation.NavigationManager
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.spyk
import org.amshove.kluent.shouldBeEqualTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MainViewModelTest {

    @get:Rule
    val mainCoroutineRule = com.example.library_test_utils.CoroutineTestRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @MockK(relaxed = true)
    internal lateinit var observer: Observer<MainViewModel.ViewState>

    @MockK(relaxed = true)
    internal lateinit var navManager: NavigationManager

    @MockK(relaxed = true)
    internal lateinit var appRepository: AppRepository

    private lateinit var cacheUserSelectedImageUseCase: CacheUserSelectedImageUseCase

    private lateinit var getCachedLoginUserUseCase: GetCachedLoginUserUseCase

    private lateinit var testViewModel: MainViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        cacheUserSelectedImageUseCase =
            spyk(CacheUserSelectedImageUseCase(appRepository, mainCoroutineRule.testDispatcher))
        getCachedLoginUserUseCase =
            spyk(GetCachedLoginUserUseCase(appRepository, mainCoroutineRule.testDispatcher))

        testViewModel = MainViewModel(
            navManager,
            cacheUserSelectedImageUseCase,
            getCachedLoginUserUseCase,
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
        testViewModel.stateLiveData.value shouldBeEqualTo MainViewModel.ViewState(
            isLocalUserDataLoading = true,
            isLocalAccountError = false,
            localUserId = null
        )
    }

}