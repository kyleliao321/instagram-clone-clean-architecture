package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.*
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import com.example.library_base.presentation.navigation.NavigationManager
import com.example.library_test_utils.runBlockingTest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
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
class ProfileMainViewModelTest {


    @get:Rule
    var rule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = com.example.library_test_utils.CoroutineTestRule()

    @MockK(relaxed = true)
    internal lateinit var profileMainFragmentArgs: ProfileMainFragmentArgs

    @MockK(relaxed = true)
    internal lateinit var profileRepository: ProfileRepository

    @MockK(relaxed = true)
    internal lateinit var navigationManager: NavigationManager

    @MockK(relaxed = true)
    internal lateinit var observer: Observer<ProfileMainViewModel.ViewState>

    private lateinit var getLoginUserUserCase: GetLoginUserUseCase

    private lateinit var getFollowingUserUseCase: GetFollowingUserUseCase

    private lateinit var getUserProfileUseCase: GetUserProfileUseCase

    private lateinit var getUserPostUseCase: GetUserPostUseCase

    private lateinit var addUserRelationUseCase: AddUserRelationUseCase

    private lateinit var removeUserRelationUseCase: RemoveUserRelationUseCase

    private lateinit var navigationUseCase: NavigationUseCase

    private lateinit var logoutUseCase: LogoutUseCase

    private lateinit var testViewModel: ProfileMainViewModel

    /**
     * Mock data
     */
    private val correctUserId = 1

    private val correctUserProfile = mockk<UserDomainModel>(relaxed = true)

    private val correctFollowingList = mockk<List<UserDomainModel>>(relaxed = true)

    private val correctUserPost = mockk<List<PostDomainModel>>(relaxed = true)

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        getLoginUserUserCase =
            GetLoginUserUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        getUserProfileUseCase =
            GetUserProfileUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        getUserPostUseCase = GetUserPostUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        getFollowingUserUseCase =
            GetFollowingUserUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        addUserRelationUseCase =
            AddUserRelationUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        removeUserRelationUseCase =
            RemoveUserRelationUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        navigationUseCase = NavigationUseCase(navigationManager, mainCoroutineRule.testDispatcher)
        logoutUseCase = LogoutUseCase(profileRepository, mainCoroutineRule.testDispatcher)

        testViewModel =
            ProfileMainViewModel(
                profileMainFragmentArgs,
                getLoginUserUserCase,
                getUserProfileUseCase,
                getUserPostUseCase,
                getFollowingUserUseCase,
                addUserRelationUseCase,
                removeUserRelationUseCase,
                navigationUseCase,
                logoutUseCase,
                mainCoroutineRule.testDispatcher
            )

        testViewModel.stateLiveData.observeForever(observer)
    }

    @After
    fun teardown() {
        testViewModel.stateLiveData.removeObserver(observer)
    }

    /**
     * Navigation test
     */
    @Test
    fun `should invoke onNavEvent inside navigationManager when navigate to edit profile fragment`() {
        // given
        mainCoroutineRule.runBlockingTest { testViewModel.onNavigateToEditProfile() }

        // expect
        verify(exactly = 1) { navigationManager.onNavEvent(any()) }
    }

    @Test
    fun `should invoke onNavEvent inside navigationManager when navigate to follower profile fragment`() {
        // given
        mainCoroutineRule.runBlockingTest { testViewModel.onNavigateToFollowerProfile() }

        // expect
        verify(exactly = 1) { navigationManager.onNavEvent(any()) }
    }

    @Test
    fun `should invoke onNavEvent inside navigationManager when navigate to following profile fragment`() {
        // given
        mainCoroutineRule.runBlockingTest { testViewModel.onNavigateToFollowingProfile() }

        // expect
        verify(exactly = 1) { navigationManager.onNavEvent(any()) }
    }

    @Test
    fun `should invoke onNavEvent inside navigationManager when navigate to post fragment`() {
        // given
        val post = mockk<PostDomainModel>(relaxed = true)
        mainCoroutineRule.runBlockingTest { testViewModel.onNavigateToPostDetail(post) }

        // expect
        verify(exactly = 1) { navigationManager.onNavEvent(any()) }
    }

    /**
     * ViewState typical test
     */
    @Test
    fun `profileMainViewModel should initialize with correct view state`() {
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileMainViewModel.ViewState(
            isLoginUserLoading = true,
            isLoginFollowingListLoading = true,
            isProfileLoading = true,
            isPostLoading = true,
            isNetworkError = false,
            isServerError = false,
            isFollowingProcessing = false,
            isLocalAccountError = false,
            loginUserProfile = null,
            loginUserFollowingList = listOf(),
            userProfile = null,
            userPosts = listOf()
        )
    }

    @Test
    fun `verify view state when getLoginUserUseCase, getUserProfileUseCase, getUserPostUseCase and getFollowingUserUseCase succeed`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(
            correctUserProfile
        )
        every { runBlocking { profileRepository.getPostByUserId(any()) } } returns Either.Success(
            correctUserPost
        )
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(
            correctUserProfile
        )
        every { runBlocking { profileRepository.getFollowingById(any()) } } returns Either.Success(
            correctFollowingList
        )

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileMainViewModel.ViewState(
            isLocalAccountError = false,
            isServerError = false,
            isNetworkError = false,
            isPostLoading = false,
            isProfileLoading = false,
            isLoginUserLoading = false,
            isFollowingProcessing = false,
            isLoginFollowingListLoading = false,
            loginUserProfile = correctUserProfile,
            loginUserFollowingList = correctFollowingList,
            userPosts = correctUserPost,
            userProfile = correctUserProfile
        )
    }


    @Test
    fun `verify view state when only getUserProfileUseCase failed on network connection`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(
            correctUserProfile
        )
        every { runBlocking { profileRepository.getPostByUserId(any()) } } returns Either.Success(
            correctUserPost
        )
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Failure(
            Failure.NetworkConnection
        )
        every { runBlocking { profileRepository.getFollowingById(any()) } } returns Either.Success(
            correctFollowingList
        )

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileMainViewModel.ViewState(
            isLocalAccountError = false,
            isServerError = false,
            isNetworkError = true,
            isPostLoading = false,
            isProfileLoading = false,
            isFollowingProcessing = false,
            isLoginUserLoading = false,
            isLoginFollowingListLoading = false,
            loginUserProfile = correctUserProfile,
            loginUserFollowingList = correctFollowingList,
            userPosts = correctUserPost,
            userProfile = null
        )
    }

    @Test
    fun `verify view state when only getUserPostUseCase failed on network connection`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(
            correctUserProfile
        )
        every { runBlocking { profileRepository.getPostByUserId(any()) } } returns Either.Failure(
            Failure.NetworkConnection
        )
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(
            correctUserProfile
        )
        every { runBlocking { profileRepository.getFollowingById(any()) } } returns Either.Success(
            correctFollowingList
        )

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileMainViewModel.ViewState(
            isLocalAccountError = false,
            isServerError = false,
            isNetworkError = true,
            isPostLoading = false,
            isFollowingProcessing = false,
            isProfileLoading = false,
            isLoginUserLoading = false,
            isLoginFollowingListLoading = false,
            loginUserProfile = correctUserProfile,
            loginUserFollowingList = correctFollowingList,
            userPosts = listOf(),
            userProfile = correctUserProfile
        )
    }

    @Test
    fun `verify view state when only getLoginUserUseCase failed on local account error`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Failure(
            Failure.LocalAccountNotFound
        )
        every { runBlocking { profileRepository.getPostByUserId(any()) } } returns Either.Success(
            correctUserPost
        )
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(
            correctUserProfile
        )
        every { runBlocking { profileRepository.getFollowingById(any()) } } returns Either.Success(
            correctFollowingList
        )

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileMainViewModel.ViewState(
            isLocalAccountError = true,
            isServerError = false,
            isNetworkError = false,
            isPostLoading = false,
            isProfileLoading = false,
            isLoginUserLoading = false,
            isLoginFollowingListLoading = false,
            isFollowingProcessing = false,
            loginUserProfile = null,
            loginUserFollowingList = listOf(),
            userPosts = correctUserPost,
            userProfile = correctUserProfile
        )
    }

    @Test
    fun `verify view state when getUserProfileUseCase and getUserPostUseCase failed on network connection`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(
            correctUserProfile
        )
        every { runBlocking { profileRepository.getPostByUserId(any()) } } returns Either.Failure(
            Failure.NetworkConnection
        )
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Failure(
            Failure.NetworkConnection
        )
        every { runBlocking { profileRepository.getFollowingById(any()) } } returns Either.Success(
            correctFollowingList
        )

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileMainViewModel.ViewState(
            isLocalAccountError = false,
            isServerError = false,
            isNetworkError = true,
            isPostLoading = false,
            isProfileLoading = false,
            isLoginUserLoading = false,
            isLoginFollowingListLoading = false,
            isFollowingProcessing = false,
            loginUserProfile = correctUserProfile,
            loginUserFollowingList = correctFollowingList,
            userPosts = listOf(),
            userProfile = null
        )
    }

    @Test
    fun `verify view state when only getUserProfileUseCase failed on server error`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(
            correctUserProfile
        )
        every { runBlocking { profileRepository.getPostByUserId(any()) } } returns Either.Success(
            correctUserPost
        )
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Failure(
            Failure.ServerError
        )
        every { runBlocking { profileRepository.getFollowingById(any()) } } returns Either.Success(
            correctFollowingList
        )

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileMainViewModel.ViewState(
            isLocalAccountError = false,
            isServerError = true,
            isNetworkError = false,
            isPostLoading = false,
            isProfileLoading = false,
            isLoginUserLoading = false,
            isLoginFollowingListLoading = false,
            isFollowingProcessing = false,
            loginUserProfile = correctUserProfile,
            loginUserFollowingList = correctFollowingList,
            userPosts = correctUserPost,
            userProfile = null
        )
    }

    @Test
    fun `verify view state when only getUserPostUseCase failed on server error`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(
            correctUserProfile
        )
        every { runBlocking { profileRepository.getPostByUserId(any()) } } returns Either.Failure(
            Failure.ServerError
        )
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(
            correctUserProfile
        )
        every { runBlocking { profileRepository.getFollowingById(any()) } } returns Either.Success(
            correctFollowingList
        )

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileMainViewModel.ViewState(
            isLocalAccountError = false,
            isServerError = true,
            isNetworkError = false,
            isPostLoading = false,
            isProfileLoading = false,
            isLoginUserLoading = false,
            isLoginFollowingListLoading = false,
            isFollowingProcessing = false,
            loginUserProfile = correctUserProfile,
            loginUserFollowingList = correctFollowingList,
            userPosts = listOf(),
            userProfile = correctUserProfile
        )
    }

    @Test
    fun `verify view state when getUserProfileUseCase and getUserPostUseCase failed on server error`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(
            correctUserProfile
        )
        every { runBlocking { profileRepository.getPostByUserId(any()) } } returns Either.Failure(
            Failure.ServerError
        )
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Failure(
            Failure.ServerError
        )
        every { runBlocking { profileRepository.getFollowingById(any()) } } returns Either.Success(
            correctFollowingList
        )

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileMainViewModel.ViewState(
            isLocalAccountError = false,
            isServerError = true,
            isNetworkError = false,
            isPostLoading = false,
            isProfileLoading = false,
            isLoginUserLoading = false,
            isLoginFollowingListLoading = false,
            isFollowingProcessing = false,
            loginUserProfile = correctUserProfile,
            loginUserFollowingList = correctFollowingList,
            userPosts = listOf(),
            userProfile = null
        )
    }

    @Test
    fun `verify view state when addUserRelationUseCase invoke successfully`() {
        `verify view state when getLoginUserUseCase, getUserProfileUseCase, getUserPostUseCase and getFollowingUserUseCase succeed`()

        // given
        every {
            runBlocking {
                profileRepository.addUserRelation(
                    any(),
                    any()
                )
            }
        } returns Either.Success(Unit)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.addUserRelation() }

        // expect
        verify(exactly = 12) { observer.onChanged(any()) }
        // init, loadUser, loadLoginUser, loadLoginFollowing, loadPost, startFollow, finishFollow, reload,
        // loadUser, loadLoginUser, loadLoginFollowing, loadPost
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileMainViewModel.ViewState(
            isLoginUserLoading = false,
            isLoginFollowingListLoading = false,
            isProfileLoading = false,
            isPostLoading = false,
            isNetworkError = false,
            isServerError = false,
            isLocalAccountError = false,
            isFollowingProcessing = false,
            loginUserProfile = correctUserProfile,
            loginUserFollowingList = correctFollowingList,
            userProfile = correctUserProfile,
            userPosts = correctUserPost
        )
    }

    @Test
    fun `verify view state when removeUserRelationUseCase invoke successfully`() {
        `verify view state when getLoginUserUseCase, getUserProfileUseCase, getUserPostUseCase and getFollowingUserUseCase succeed`()

        // given
        every {
            runBlocking {
                profileRepository.removeUserRelation(
                    any(),
                    any()
                )
            }
        } returns Either.Success(Unit)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.removeUserRelation() }

        // expect
        verify(exactly = 12) { observer.onChanged(any()) }
        // init, loadUser, loadLoginUser, loadLoginFollowing,
        // loadPost, startFollow, finishFollow, reload, loadUser, loadLoginUser, loadLoginFollowing, loadPost
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileMainViewModel.ViewState(
            isLoginUserLoading = false,
            isLoginFollowingListLoading = false,
            isProfileLoading = false,
            isPostLoading = false,
            isNetworkError = false,
            isServerError = false,
            isLocalAccountError = false,
            isFollowingProcessing = false,
            loginUserProfile = correctUserProfile,
            loginUserFollowingList = correctFollowingList,
            userProfile = correctUserProfile,
            userPosts = correctUserPost
        )
    }

    /**
     * ViewState edge-case test (i.e: useCases failed on different failure type)
     */
    @Test
    fun `verify view state when getUserProfileUseCase failed on network connection but getUserPostUseCase failed on server error`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(
            correctUserProfile
        )
        every { runBlocking { profileRepository.getPostByUserId(any()) } } returns Either.Failure(
            Failure.ServerError
        )
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Failure(
            Failure.NetworkConnection
        )
        every { runBlocking { profileRepository.getFollowingById(any()) } } returns Either.Success(
            correctFollowingList
        )

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileMainViewModel.ViewState(
            isLocalAccountError = false,
            isServerError = true,
            isNetworkError = true,
            isPostLoading = false,
            isProfileLoading = false,
            isLoginUserLoading = false,
            isLoginFollowingListLoading = false,
            isFollowingProcessing = false,
            loginUserProfile = correctUserProfile,
            loginUserFollowingList = correctFollowingList,
            userPosts = listOf(),
            userProfile = null
        )
    }

    @Test
    fun `verify view state when getUserProfileUseCase failed on server error but getUserPostUseCase failed on network connection`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(
            correctUserProfile
        )
        every { runBlocking { profileRepository.getPostByUserId(any()) } } returns Either.Failure(
            Failure.NetworkConnection
        )
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Failure(
            Failure.ServerError
        )
        every { runBlocking { profileRepository.getFollowingById(any()) } } returns Either.Success(
            correctFollowingList
        )

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileMainViewModel.ViewState(
            isLocalAccountError = false,
            isServerError = true,
            isNetworkError = true,
            isPostLoading = false,
            isProfileLoading = false,
            isLoginUserLoading = false,
            isLoginFollowingListLoading = false,
            isFollowingProcessing = false,
            loginUserProfile = correctUserProfile,
            loginUserFollowingList = correctFollowingList,
            userPosts = listOf(),
            userProfile = null
        )
    }

    @Test
    fun `verify view state when three useCase all fail on different type of failure`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Failure(
            Failure.LocalAccountNotFound
        )
        every { runBlocking { profileRepository.getPostByUserId(any()) } } returns Either.Failure(
            Failure.NetworkConnection
        )
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Failure(
            Failure.ServerError
        )
        every { runBlocking { profileRepository.getFollowingById(any()) } } returns Either.Success(
            correctFollowingList
        )

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileMainViewModel.ViewState(
            isLocalAccountError = true,
            isServerError = true,
            isNetworkError = true,
            isPostLoading = false,
            isProfileLoading = false,
            isLoginUserLoading = false,
            isLoginFollowingListLoading = false,
            isFollowingProcessing = false,
            loginUserProfile = null,
            loginUserFollowingList = listOf(),
            userPosts = listOf(),
            userProfile = null
        )
    }
}