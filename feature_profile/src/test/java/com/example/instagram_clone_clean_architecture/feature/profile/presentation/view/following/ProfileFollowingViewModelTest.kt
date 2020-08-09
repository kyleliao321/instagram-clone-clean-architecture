package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.following

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetFollowingUserUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.NavigationUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.following.ProfileFollowingFragmentArgs
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
class ProfileFollowingViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = CoroutineTestRule()

    @MockK(relaxed = true)
    internal lateinit var profileFollowingFragmentArgs: ProfileFollowingFragmentArgs

    @MockK(relaxed = true)
    internal lateinit var profileRepository: ProfileRepository

    @MockK(relaxed = true)
    internal lateinit var navigationManager: NavigationManager

    @MockK(relaxed = true)
    internal lateinit var observer: Observer<ProfileFollowingViewModel.ViewState>

    private lateinit var getFollowingUserUserUseCase: GetFollowingUserUseCase

    private lateinit var navigationUserCase: NavigationUseCase

    private lateinit var testViewModel: ProfileFollowingViewModel

    /**
     * Mock data
     */
    private val targetUserProfile = UserDomainModel(
        id = 2, name = "Kyle", userName = "kyle", followerNum = 1, followingNum = 1, postNum = 1
    )

    private val correctFollowingList = listOf(
        UserDomainModel(id = 1, name = "anna", userName = "Anna", followingNum = 1, followerNum = 2, postNum = 1)
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        getFollowingUserUserUseCase = GetFollowingUserUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        navigationUserCase = NavigationUseCase(navigationManager, mainCoroutineRule.testDispatcher)

        testViewModel =
            ProfileFollowingViewModel(
                profileFollowingFragmentArgs,
                getFollowingUserUserUseCase,
                navigationUserCase,
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
    fun `profileFollowingViewModel should initialize with correct view state`() {
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowingViewModel.ViewState(
            isFollowingListLoading = true,
            isServerError = false,
            isNetworkError = false,
            followingList = listOf()
        )
    }

    @Test
    fun `verify view state when getFollowingUserUseCase succeed`() {
        // given
        every { runBlocking { profileRepository.getFollowingById(any()) } } returns Either.Success(correctFollowingList)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowingViewModel.ViewState(
            isFollowingListLoading = false,
            isNetworkError = false,
            isServerError = false,
            followingList = correctFollowingList
        )
    }

    @Test
    fun `verify view state when getFollowingUserUseCase fail on network connection`() {
        // given
        every { runBlocking { profileRepository.getFollowingById(any()) } } returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowingViewModel.ViewState(
            isFollowingListLoading = false,
            isNetworkError = true,
            isServerError = false,
            followingList = listOf()
        )
    }

    @Test
    fun `verify view state when getFollowingUserUseCase fail on server error`() {
        // given
        every { runBlocking { profileRepository.getFollowingById(any()) } } returns Either.Failure(Failure.ServerError)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowingViewModel.ViewState(
            isFollowingListLoading = false,
            isNetworkError = false,
            isServerError = true,
            followingList = listOf()
        )
    }

}