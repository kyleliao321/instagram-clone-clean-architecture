package com.example.instagram_clone_clean_architecture.app.presentation.activity

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.app.domain.repository.AppRepository
import com.example.instagram_clone_clean_architecture.app.domain.usecase.CacheUserSelectedImageUseCase
import com.example.instagram_clone_clean_architecture.app.domain.usecase.GetCachedLoginUserUseCase
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
import okhttp3.Cache
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

    private lateinit var cacheUserSelectedImageUseCase: CacheUserSelectedImageUseCase

    private lateinit var getCachedLoginUserUseCase: GetCachedLoginUserUseCase

    private lateinit var testViewModel: MainViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        cacheUserSelectedImageUseCase = CacheUserSelectedImageUseCase(appRepository, mainCoroutineRule.testDispatcher)
        getCachedLoginUserUseCase = GetCachedLoginUserUseCase(appRepository, mainCoroutineRule.testDispatcher)

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