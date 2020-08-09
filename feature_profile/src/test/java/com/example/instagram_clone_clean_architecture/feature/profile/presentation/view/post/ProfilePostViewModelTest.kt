package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.post

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetPostUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetUserProfileUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.post.ProfilePostFragmentArgs
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.CoroutineTestRule
import com.example.library_base.domain.utility.Either
import com.example.library_base.domain.utility.runBlockingTest
import io.mockk.MockKAnnotations
import io.mockk.every
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
class ProfilePostViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = CoroutineTestRule()

    @MockK(relaxed = true)
    internal lateinit var profilePostFragmentArgs: ProfilePostFragmentArgs

    @MockK(relaxed = true)
    internal lateinit var profileRepository: ProfileRepository

    @MockK(relaxed = true)
    internal lateinit var observer: Observer<ProfilePostViewModel.ViewState>

    private lateinit var getPostUseCase: GetPostUseCase

    private lateinit var getUserProfileUseCase: GetUserProfileUseCase

    private lateinit var testViewModel: ProfilePostViewModel

    /**
     * Mock data
     */
    private val correctUserProfile = UserDomainModel(
        id = 1, name = "Kyle", userName = "kyle", postNum = 0, followerNum = 1, followingNum = 2
    )

    private val correctPost = PostDomainModel(
        id = 1, imageSrc = "ss", date = Date(), belongUserId = 1
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        getPostUseCase = GetPostUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        getUserProfileUseCase = GetUserProfileUseCase(profileRepository, mainCoroutineRule.testDispatcher)

        testViewModel =
            ProfilePostViewModel(
                profilePostFragmentArgs,
                getPostUseCase,
                getUserProfileUseCase,
                mainCoroutineRule.testDispatcher
            )

        testViewModel.stateLiveData.observeForever(observer)
    }

    @After
    fun teardown() {
        testViewModel.stateLiveData.removeObserver(observer)
    }

    @Test
    fun `profilePostViewModel should initialize with correct view state`() {
        testViewModel.stateLiveData.value shouldBeEqualTo ProfilePostViewModel.ViewState(
            isProfileLoading = true,
            isPostLoading = true,
            isServerError = false,
            isNetworkError = false,
            userProfile = null,
            post = null
        )
    }

    @Test
    fun `verify view state when getPostUseCase and getUserProfileUseCase succeed`() {
        // given
        every { runBlocking { profileRepository.getPostByPostId(any()) } } returns Either.Success(correctPost)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(correctUserProfile)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfilePostViewModel.ViewState(
            isProfileLoading = false,
            isPostLoading = false,
            isNetworkError = false,
            isServerError = false,
            userProfile = correctUserProfile,
            post = correctPost
        )
    }

    @Test
    fun `verify view state when only getPostUseCase fail on network connection`() {
        // given
        every { runBlocking { profileRepository.getPostByPostId(any()) } } returns Either.Failure(Failure.NetworkConnection)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(correctUserProfile)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfilePostViewModel.ViewState(
            isProfileLoading = false,
            isPostLoading = false,
            isNetworkError = true,
            isServerError = false,
            userProfile = correctUserProfile,
            post = null
        )
    }

    @Test
    fun `verify view state when only getPostUseCase fail on server error`() {
        // given
        every { runBlocking { profileRepository.getPostByPostId(any()) } } returns Either.Failure(Failure.ServerError)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(correctUserProfile)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfilePostViewModel.ViewState(
            isProfileLoading = false,
            isPostLoading = false,
            isNetworkError = false,
            isServerError = true,
            userProfile = correctUserProfile,
            post = null
        )
    }

    @Test
    fun `verify view state when only getUserProfileUseCase fail on network connection`() {
        // given
        every { runBlocking { profileRepository.getPostByPostId(any()) } } returns Either.Success(correctPost)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfilePostViewModel.ViewState(
            isProfileLoading = false,
            isPostLoading = false,
            isNetworkError = true,
            isServerError = false,
            userProfile = null,
            post = correctPost
        )
    }

    @Test
    fun `verify view state when only getUserProfileUseCase fail on server error`() {
        // given
        every { runBlocking { profileRepository.getPostByPostId(any()) } } returns Either.Success(correctPost)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Failure(Failure.ServerError)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfilePostViewModel.ViewState(
            isProfileLoading = false,
            isPostLoading = false,
            isNetworkError = false,
            isServerError = true,
            userProfile = null,
            post = correctPost
        )
    }

    @Test
    fun `verify view state when both getUserProfileUseCase and getPostUseCase fail on server error`() {
        // given
        every { runBlocking { profileRepository.getPostByPostId(any()) } } returns Either.Failure(Failure.ServerError)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Failure(Failure.ServerError)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfilePostViewModel.ViewState(
            isProfileLoading = false,
            isPostLoading = false,
            isNetworkError = false,
            isServerError = true,
            userProfile = null,
            post = null
        )
    }

    @Test
    fun `verify view state when both getUserProfileUseCase and getPostUseCase fail on network conntection`() {
        // given
        every { runBlocking { profileRepository.getPostByPostId(any()) } } returns Either.Failure(Failure.NetworkConnection)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfilePostViewModel.ViewState(
            isProfileLoading = false,
            isPostLoading = false,
            isNetworkError = true,
            isServerError = false,
            userProfile = null,
            post = null
        )
    }

}