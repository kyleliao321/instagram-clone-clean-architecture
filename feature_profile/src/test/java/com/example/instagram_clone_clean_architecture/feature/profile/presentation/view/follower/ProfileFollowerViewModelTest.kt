package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.follower

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetFollowerUserUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.NavigationUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.follower.ProfileFollowerFragmentArgs
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.CoroutineTestRule
import com.example.library_base.domain.utility.Either
import com.example.library_base.domain.utility.runBlockingTest
import com.example.library_base.presentation.navigation.NavigationManager
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
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
class ProfileFollowerViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = CoroutineTestRule()

    @MockK(relaxed = true)
    internal lateinit var profileFollowerFragmentArgs: ProfileFollowerFragmentArgs

    @MockK(relaxed = true)
    internal lateinit var navigationManager: NavigationManager

    @MockK(relaxed = true)
    internal lateinit var profileRepository: ProfileRepository

    @MockK(relaxed = true)
    internal lateinit var observer: Observer<ProfileFollowerViewModel.ViewState>

    private lateinit var getFollowerUserUseCase: GetFollowerUserUseCase

    private lateinit var navigationUseCase: NavigationUseCase

    private lateinit var testViewModel: ProfileFollowerViewModel

    /**
     * Mock data
     */
    private val targetUserProfile = UserDomainModel(
        id = 2, name = "Kyle", userName = "kyle", followerNum = 1, followingNum = 1, postNum = 1
    )

    private val correctFollowerList = listOf(
        UserDomainModel(id = 1, name = "anna", userName = "Anna", followingNum = 1, followerNum = 2, postNum = 1)
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        getFollowerUserUseCase = GetFollowerUserUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        navigationUseCase = NavigationUseCase(navigationManager, mainCoroutineRule.testDispatcher)

        testViewModel =
            ProfileFollowerViewModel(
                profileFollowerFragmentArgs,
                getFollowerUserUseCase,
                navigationUseCase,
                mainCoroutineRule.testDispatcher
            )

        testViewModel.stateLiveData.observeForever(observer)
    }

    @After
    fun teardown() {
        testViewModel.stateLiveData.removeObserver(observer)
    }

    @Test
    fun `should invoke onNavEvent inside navigationManager when navigate to main profile fragment`() {
        // when
        mainCoroutineRule.runBlockingTest { testViewModel.onNavigateToUserProfile(targetUserProfile) }

        // expect
        verify(exactly = 1) { navigationManager.onNavEvent(any()) }
    }

    @Test
    fun `profileFollowerViewModel should initialize with correct view state`() {
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowerViewModel.ViewState(
            isFollowerLoading = true,
            isServerError = false,
            isNetworkError = false,
            followerList = listOf()
        )
    }

    @Test
    fun `verify view state when getFollowerUserUseCase succeed`() {
        // given
        every { runBlocking { profileRepository.getFollowerById(any()) } } returns Either.Success(correctFollowerList)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowerViewModel.ViewState(
            isFollowerLoading = false,
            isNetworkError = false,
            isServerError = false,
            followerList = correctFollowerList
        )
    }

    @Test
    fun `verify view state when getFollowerUserUseCase fail on network connection`() {
        // given
        every { runBlocking { profileRepository.getFollowerById(any()) } } returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowerViewModel.ViewState(
            isFollowerLoading = false,
            isNetworkError = true,
            isServerError = false,
            followerList = listOf()
        )
    }

    @Test
    fun `verify view state when getFollowerUserUseCase fail on server error`() {
        // given
        every { runBlocking { profileRepository.getFollowerById(any()) } } returns Either.Failure(Failure.ServerError)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowerViewModel.ViewState(
            isFollowerLoading = false,
            isNetworkError = false,
            isServerError = true,
            followerList = listOf()
        )
    }

}