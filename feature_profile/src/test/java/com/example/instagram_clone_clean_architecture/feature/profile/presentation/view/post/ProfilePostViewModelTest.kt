package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.post

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.*
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

    private lateinit var getLoginUserUseCase: GetLoginUserUseCase

    private lateinit var getPostUseCase: GetPostUseCase

    private lateinit var getUserProfileUseCase: GetUserProfileUseCase

    private lateinit var getLikedUsersUseCase: GetLikedUsersUseCase

    private lateinit var userLikedUsersUseCase: UserLikePostUseCase

    private lateinit var userUnlikePostUseCase: UserUnlikePostUseCase

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

    private val correctLikedUsers = listOf(correctUserProfile)

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        getLoginUserUseCase = GetLoginUserUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        getPostUseCase = GetPostUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        getUserProfileUseCase = GetUserProfileUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        getLikedUsersUseCase = GetLikedUsersUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        userLikedUsersUseCase = UserLikePostUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        userUnlikePostUseCase = UserUnlikePostUseCase(profileRepository, mainCoroutineRule.testDispatcher)

        testViewModel =
            ProfilePostViewModel(
                profilePostFragmentArgs,
                getLoginUserUseCase,
                getPostUseCase,
                getUserProfileUseCase,
                getLikedUsersUseCase,
                userLikedUsersUseCase,
                userUnlikePostUseCase,
                mainCoroutineRule.testDispatcher
            )

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
    fun `profilePostViewModel should initialize with correct view state`() {
        testViewModel.stateLiveData.value shouldBeEqualTo ProfilePostViewModel.ViewState(
            isLoginUserProfileLoading = true,
            isProfileLoading = true,
            isPostLoading = true,
            isLikedUsersLoading = true,
            isServerError = false,
            isNetworkError = false,
            isLocalAccountError = false,
            loginUserProfile = null,
            userProfile = null,
            post = null,
            likedUsers = listOf()
        )
    }

    @Test
    fun `verify view state when all usecases invoke successfully`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.getPostByPostId(any()) } } returns Either.Success(correctPost)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.getLikedUsersByPostId(any()) } } returns Either.Success(correctLikedUsers)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfilePostViewModel.ViewState(
            isLoginUserProfileLoading = false,
            isProfileLoading = false,
            isPostLoading = false,
            isLikedUsersLoading = false,
            isNetworkError = false,
            isServerError = false,
            isLocalAccountError = false,
            loginUserProfile = correctUserProfile,
            userProfile = correctUserProfile,
            post = correctPost,
            likedUsers = correctLikedUsers
        )
    }

    @Test
    fun `verify view state when only getPostUseCase fail on network connection`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.getPostByPostId(any()) } } returns Either.Failure(Failure.NetworkConnection)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.getLikedUsersByPostId(any()) } } returns Either.Success(correctLikedUsers)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfilePostViewModel.ViewState(
            isLoginUserProfileLoading = false,
            isProfileLoading = false,
            isPostLoading = false,
            isLikedUsersLoading = false,
            isNetworkError = true,
            isServerError = false,
            isLocalAccountError = false,
            loginUserProfile = correctUserProfile,
            userProfile = correctUserProfile,
            post = null,
            likedUsers = correctLikedUsers
        )
    }

    @Test
    fun `verify view state when only getPostUseCase fail on server error`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.getPostByPostId(any()) } } returns Either.Failure(Failure.ServerError)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.getLikedUsersByPostId(any()) } } returns Either.Success(correctLikedUsers)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfilePostViewModel.ViewState(
            isLoginUserProfileLoading = false,
            isProfileLoading = false,
            isPostLoading = false,
            isLikedUsersLoading = false,
            isNetworkError = false,
            isServerError = true,
            isLocalAccountError = false,
            loginUserProfile = correctUserProfile,
            userProfile = correctUserProfile,
            post = null,
            likedUsers = correctLikedUsers
        )
    }

    @Test
    fun `verify view state when only getLoginUserUseCase fail on local account error`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(null)
        every { runBlocking { profileRepository.getPostByPostId(any()) } } returns Either.Success(correctPost)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.getLikedUsersByPostId(any()) } } returns Either.Success(correctLikedUsers)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfilePostViewModel.ViewState(
            isLoginUserProfileLoading = false,
            isProfileLoading = false,
            isPostLoading = false,
            isLikedUsersLoading = false,
            isNetworkError = false,
            isServerError = false,
            isLocalAccountError = true,
            loginUserProfile = null,
            userProfile = correctUserProfile,
            post = correctPost,
            likedUsers = correctLikedUsers
        )
    }

    @Test
    fun `verify view state when only getUserProfileUseCase fail on network connection`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.getPostByPostId(any()) } } returns Either.Success(correctPost)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Failure(Failure.NetworkConnection)
        every { runBlocking { profileRepository.getLikedUsersByPostId(any()) } } returns Either.Success(correctLikedUsers)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfilePostViewModel.ViewState(
            isLoginUserProfileLoading = false,
            isProfileLoading = false,
            isPostLoading = false,
            isLikedUsersLoading = false,
            isNetworkError = true,
            isServerError = false,
            isLocalAccountError = false,
            loginUserProfile = correctUserProfile,
            userProfile = null,
            post = correctPost,
            likedUsers = correctLikedUsers
        )
    }

    @Test
    fun `verify view state when only getLikedUsersUseCase fail on network connection`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.getPostByPostId(any()) } } returns Either.Success(correctPost)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.getLikedUsersByPostId(any()) } } returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfilePostViewModel.ViewState(
            isLoginUserProfileLoading = false,
            isProfileLoading = false,
            isPostLoading = false,
            isLikedUsersLoading = false,
            isNetworkError = true,
            isServerError = false,
            isLocalAccountError = false,
            loginUserProfile = correctUserProfile,
            userProfile = correctUserProfile,
            post = correctPost,
            likedUsers = listOf()
        )
    }

    @Test
    fun `verify view state when only getUserProfileUseCase fail on server error`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.getPostByPostId(any()) } } returns Either.Success(correctPost)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Failure(Failure.ServerError)
        every { runBlocking { profileRepository.getLikedUsersByPostId(any()) } } returns Either.Success(correctLikedUsers)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfilePostViewModel.ViewState(
            isLoginUserProfileLoading = false,
            isProfileLoading = false,
            isPostLoading = false,
            isLikedUsersLoading = false,
            isNetworkError = false,
            isServerError = true,
            isLocalAccountError = false,
            loginUserProfile = correctUserProfile,
            userProfile = null,
            post = correctPost,
            likedUsers = correctLikedUsers
        )
    }

    @Test
    fun `verify view state when both getUserProfileUseCase and getPostUseCase fail on server error`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.getPostByPostId(any()) } } returns Either.Failure(Failure.ServerError)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Failure(Failure.ServerError)
        every { runBlocking { profileRepository.getLikedUsersByPostId(any()) } } returns Either.Success(correctLikedUsers)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfilePostViewModel.ViewState(
            isLoginUserProfileLoading = false,
            isProfileLoading = false,
            isPostLoading = false,
            isLikedUsersLoading = false,
            isNetworkError = false,
            isServerError = true,
            isLocalAccountError = false,
            loginUserProfile = correctUserProfile,
            userProfile = null,
            post = null,
            likedUsers = correctLikedUsers
        )
    }

    @Test
    fun `verify view state when both getUserProfileUseCase and getPostUseCase fail on network connection`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.getPostByPostId(any()) } } returns Either.Failure(Failure.NetworkConnection)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Failure(Failure.NetworkConnection)
        every { runBlocking { profileRepository.getLikedUsersByPostId(any()) } } returns Either.Success(correctLikedUsers)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfilePostViewModel.ViewState(
            isLoginUserProfileLoading = false,
            isProfileLoading = false,
            isPostLoading = false,
            isLikedUsersLoading = false,
            isNetworkError = true,
            isServerError = false,
            isLocalAccountError = false,
            loginUserProfile = correctUserProfile,
            userProfile = null,
            post = null,
            likedUsers = correctLikedUsers
        )
    }

    /**
     * ViewState edge-case test(i.e: useCases failed on different failure type)
     */
    @Test
    fun `verify view state when getUserProfileUseCase failed on network connection but getPostUseCase failed on server error`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.getPostByPostId(any()) } } returns Either.Failure(Failure.ServerError)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Failure(Failure.NetworkConnection)
        every { runBlocking { profileRepository.getLikedUsersByPostId(any()) } } returns Either.Success(correctLikedUsers)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfilePostViewModel.ViewState(
            isLoginUserProfileLoading = false,
            isProfileLoading = false,
            isPostLoading = false,
            isLikedUsersLoading = false,
            isNetworkError = true,
            isServerError = true,
            isLocalAccountError = false,
            loginUserProfile = correctUserProfile,
            userProfile = null,
            post = null,
            likedUsers = correctLikedUsers
        )
    }

    @Test
    fun `verify view state when getUserProfileUseCase failed on server error but getPostUseCase failed on network connection`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.getPostByPostId(any()) } } returns Either.Failure(Failure.ServerError)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Failure(Failure.NetworkConnection)
        every { runBlocking { profileRepository.getLikedUsersByPostId(any()) } } returns Either.Success(correctLikedUsers)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfilePostViewModel.ViewState(
            isLoginUserProfileLoading = false,
            isProfileLoading = false,
            isPostLoading = false,
            isLikedUsersLoading = false,
            isNetworkError = true,
            isServerError = true,
            isLocalAccountError = false,
            loginUserProfile = correctUserProfile,
            userProfile = null,
            post = null,
            likedUsers = correctLikedUsers
        )
    }

    @Test
    fun `verify view state when three use cases all failed on different failure type`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(null)
        every { runBlocking { profileRepository.getPostByPostId(any()) } } returns Either.Failure(Failure.ServerError)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Failure(Failure.NetworkConnection)
        every { runBlocking { profileRepository.getLikedUsersByPostId(any()) } } returns Either.Success(correctLikedUsers)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfilePostViewModel.ViewState(
            isLoginUserProfileLoading = false,
            isProfileLoading = false,
            isPostLoading = false,
            isLikedUsersLoading = false,
            isNetworkError = true,
            isServerError = true,
            isLocalAccountError = true,
            loginUserProfile = null,
            userProfile = null,
            post = null,
            likedUsers = correctLikedUsers
        )
    }

}