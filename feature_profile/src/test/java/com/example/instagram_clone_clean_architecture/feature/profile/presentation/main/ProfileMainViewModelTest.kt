package com.example.instagram_clone_clean_architecture.feature.profile.presentation.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetUserPostUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetUserProfileUseCase
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.CoroutineTestRule
import com.example.library_base.domain.utility.Either
import com.example.library_base.domain.utility.runBlockingTest
import com.example.library_base.presentation.navigation.NavigationManager
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
import java.util.*

@RunWith(JUnit4::class)
class ProfileMainViewModelTest {


    @get:Rule
    var rule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = CoroutineTestRule()

    @MockK
    internal lateinit var profileMainFragmentArgs: ProfileMainFragmentArgs

    @MockK(relaxed = true)
    internal lateinit var profileRepository: ProfileRepository

    @MockK(relaxed = true)
    internal lateinit var navigationManager: NavigationManager

    @MockK(relaxed = true)
    internal lateinit var observer: Observer<ProfileMainViewModel.ViewState>

    private lateinit var getUserProfileUseCase: GetUserProfileUseCase

    private lateinit var getUserPostUseCase: GetUserPostUseCase

    private lateinit var testViewModel: ProfileMainViewModel

    /**
     * Mock data
     */
    private val correctUserId = 1

    private val correctUserProfile = UserDomainModel(
        id = correctUserId, name = "Kyle", userName = "kyle", postNum = 0, followerNum = 1, followingNum = 2
    )

    private val correctUserPost = listOf(
        PostDomainModel(
            id = correctUserId, imageSrc = "ss", date = Date(), belongUserId = 1
        )
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        getUserProfileUseCase = GetUserProfileUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        getUserPostUseCase = GetUserPostUseCase(profileRepository, mainCoroutineRule.testDispatcher)

        testViewModel = ProfileMainViewModel(
            profileMainFragmentArgs,
            getUserProfileUseCase,
            getUserPostUseCase,
            mainCoroutineRule.testDispatcher
        )

        testViewModel.stateLiveData.observeForever(observer)
    }

    @After
    fun teardown() {
        testViewModel.stateLiveData.removeObserver(observer)
    }

    @Test
    fun `profileMainViewModel should initialize with correct view state`() {
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileMainViewModel.ViewState(
            isProfileLoading = true,
            isPostLoading = true,
            isNetworkError = false,
            isServerError = false,
            userProfile = null,
            userPosts = listOf()
        )
    }

    @Test
    fun `verify view state when getUserProfileUseCase and getUserPostUseCase succeed`() {
        // given
        every { profileMainFragmentArgs.userId } returns correctUserId

        every { runBlocking { profileRepository.getPostByUserId(any()) } } returns Either.Success(correctUserPost)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(correctUserProfile)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileMainViewModel.ViewState(
            isServerError = false,
            isNetworkError = false,
            isPostLoading = false,
            isProfileLoading = false,
            userPosts = correctUserPost,
            userProfile = correctUserProfile
        )
    }


    @Test
    fun `verify view state when only getUserProfileUseCase failed`() {
        // given
        every { profileMainFragmentArgs.userId } returns correctUserId

        every { runBlocking { profileRepository.getPostByUserId(any()) } } returns Either.Success(correctUserPost)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(null)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileMainViewModel.ViewState(
            isServerError = true,
            isNetworkError = false,
            isPostLoading = false,
            isProfileLoading = false,
            userPosts = correctUserPost,
            userProfile = null
        )
    }

    @Test
    fun `verify view state when only getUserProfileUseCase failed on network connection`() {
        // given
        every { profileMainFragmentArgs.userId } returns correctUserId

        every { runBlocking { profileRepository.getPostByUserId(any()) } } returns Either.Success(correctUserPost)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileMainViewModel.ViewState(
            isServerError = false,
            isNetworkError = true,
            isPostLoading = false,
            isProfileLoading = false,
            userPosts = correctUserPost,
            userProfile = null
        )
    }

    @Test
    fun `verify view state when only getUserPostUseCase failed on network connection`() {
        // given
        every { profileMainFragmentArgs.userId } returns correctUserId

        every { runBlocking { profileRepository.getPostByUserId(any()) } } returns Either.Failure(Failure.NetworkConnection)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(correctUserProfile)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileMainViewModel.ViewState(
            isServerError = false,
            isNetworkError = true,
            isPostLoading = false,
            isProfileLoading = false,
            userPosts = listOf(),
            userProfile = correctUserProfile
        )
    }

    @Test
    fun `verify view state when getUserProfileUseCase and getUserPostUseCase failed on network connection`() {
        // given
        every { profileMainFragmentArgs.userId } returns correctUserId

        every { runBlocking { profileRepository.getPostByUserId(any()) } } returns Either.Failure(Failure.NetworkConnection)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileMainViewModel.ViewState(
            isServerError = false,
            isNetworkError = true,
            isPostLoading = false,
            isProfileLoading = false,
            userPosts = listOf(),
            userProfile = null
        )
    }

    @Test
    fun `verify view state when only getUserProfileUseCase failed on server error`() {
        // given
        every { profileMainFragmentArgs.userId } returns correctUserId

        every { runBlocking { profileRepository.getPostByUserId(any()) } } returns Either.Success(correctUserPost)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Failure(Failure.ServerError)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileMainViewModel.ViewState(
            isServerError = true,
            isNetworkError = false,
            isPostLoading = false,
            isProfileLoading = false,
            userPosts = correctUserPost,
            userProfile = null
        )
    }

    @Test
    fun `verify view state when only getUserPostUseCase failed on server error`() {
        // given
        every { profileMainFragmentArgs.userId } returns correctUserId

        every { runBlocking { profileRepository.getPostByUserId(any()) } } returns Either.Failure(Failure.ServerError)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(correctUserProfile)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileMainViewModel.ViewState(
            isServerError = true,
            isNetworkError = false,
            isPostLoading = false,
            isProfileLoading = false,
            userPosts = listOf(),
            userProfile = correctUserProfile
        )
    }

    @Test
    fun `verify view state when getUserProfileUseCase and getUserPostUseCase failed on server error`() {
        // given
        every { profileMainFragmentArgs.userId } returns correctUserId

        every { runBlocking { profileRepository.getPostByUserId(any()) } } returns Either.Failure(Failure.ServerError)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Failure(Failure.ServerError)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileMainViewModel.ViewState(
            isServerError = true,
            isNetworkError = false,
            isPostLoading = false,
            isProfileLoading = false,
            userPosts = listOf(),
            userProfile = null
        )
    }
}