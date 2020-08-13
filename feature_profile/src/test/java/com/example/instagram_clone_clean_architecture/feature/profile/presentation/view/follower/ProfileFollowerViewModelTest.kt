package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.follower

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.*
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.follower.ProfileFollowerFragmentArgs
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.following.ProfileFollowingViewModel
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

    private lateinit var getLoginUserUseCase: GetLoginUserUseCase

    private lateinit var getFollowerUserUseCase: GetFollowerUserUseCase

    private lateinit var getFollowingUserUseCase: GetFollowingUserUseCase

    private lateinit var addUserRelationUseCase: AddUserRelationUseCase

    private lateinit var removeUserRelationUseCase: RemoveUserRelationUseCase

    private lateinit var navigationUseCase: NavigationUseCase

    private lateinit var testViewModel: ProfileFollowerViewModel

    /**
     * Mock data
     */
    private val correctUserProfile = UserDomainModel(
        id = 1, name = "Kyle", userName = "kyle", postNum = 0, followerNum = 1, followingNum = 2
    )

    private val targetUserProfile = UserDomainModel(
        id = 2, name = "Kyle", userName = "kyle", followerNum = 1, followingNum = 1, postNum = 1
    )

    private val correctFollowerList = listOf(
        UserDomainModel(id = 1, name = "anna", userName = "Anna", followingNum = 1, followerNum = 2, postNum = 1)
    )

    private val correctFollowingList = listOf(
        UserDomainModel(id = 5, name = "anna", userName = "Anna", followingNum = 1, followerNum = 2, postNum = 1)
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        getLoginUserUseCase = GetLoginUserUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        getFollowerUserUseCase = GetFollowerUserUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        getFollowingUserUseCase = GetFollowingUserUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        addUserRelationUseCase = AddUserRelationUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        removeUserRelationUseCase = RemoveUserRelationUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        navigationUseCase = NavigationUseCase(navigationManager, mainCoroutineRule.testDispatcher)

        testViewModel =
            ProfileFollowerViewModel(
                profileFollowerFragmentArgs,
                getLoginUserUseCase,
                getFollowerUserUseCase,
                getFollowingUserUseCase,
                addUserRelationUseCase,
                removeUserRelationUseCase,
                navigationUseCase,
                mainCoroutineRule.testDispatcher
            )

        testViewModel.stateLiveData.observeForever(observer)
    }

    @After
    fun teardown() {
        testViewModel.stateLiveData.removeObserver(observer)
    }

    /**
     *  Navigation test
     */
    @Test
    fun `should invoke onNavEvent inside navigationManager when navigate to main profile fragment`() {
        // when
        mainCoroutineRule.runBlockingTest { testViewModel.onNavigateToUserProfile(targetUserProfile) }

        // expect
        verify(exactly = 1) { navigationManager.onNavEvent(any()) }
    }

    /**
     * ViewState typical test
     */
    @Test
    fun `profileFollowingViewModel should initialize with correct view state`() {
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowerViewModel.ViewState(
            isLoginUserLoading = true,
            isLoginUserFollowingLoading = true,
            isFollowerListLoading = true,
            isServerError = false,
            isNetworkError = false,
            isLocalAccountError = false,
            loginUser = null,
            loginUserFollowingList = listOf(),
            followerList = listOf()
        )
    }

    @Test
    fun `verify view state when getFollowingUserUseCase, getFollowerUserUseCase and getLoginUserUseCase succeed`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.getFollowerById(any()) } } returns Either.Success(correctFollowerList)
        every { runBlocking { profileRepository.getFollowingById(any()) }} returns Either.Success(correctFollowingList)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowerViewModel.ViewState(
            isLoginUserLoading = false,
            isLoginUserFollowingLoading = false,
            isFollowerListLoading = false,
            isNetworkError = false,
            isServerError = false,
            isLocalAccountError = false,
            loginUser = correctUserProfile,
            loginUserFollowingList = correctFollowingList,
            followerList = correctFollowerList
        )
    }

    @Test
    fun `verify view state when only getFollowerUserUseCase fail on network connection`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.getFollowerById(any()) } } returns Either.Failure(Failure.NetworkConnection)
        every { runBlocking { profileRepository.getFollowingById(any()) }} returns Either.Success(correctFollowingList)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowerViewModel.ViewState(
            isLoginUserLoading = false,
            isLoginUserFollowingLoading = false,
            isFollowerListLoading = false,
            isNetworkError = true,
            isServerError = false,
            isLocalAccountError = false,
            loginUser = correctUserProfile,
            loginUserFollowingList = correctFollowingList,
            followerList = listOf()
        )
    }

    @Test
    fun `verify view state when only getLoginUserUseCase fail on local account error`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Failure(Failure.LocalAccountNotFound)
        every { runBlocking { profileRepository.getFollowerById(any()) } } returns Either.Success(correctFollowerList)
        every { runBlocking { profileRepository.getFollowingById(any()) }} returns Either.Success(correctFollowingList)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowerViewModel.ViewState(
            isLoginUserLoading = false,
            isLoginUserFollowingLoading = false,
            isFollowerListLoading = false,
            isNetworkError = false,
            isServerError = false,
            isLocalAccountError = true,
            loginUser = null,
            loginUserFollowingList = listOf(),
            followerList = correctFollowerList
        )
    }

    @Test
    fun `verify view state when only getFollowerUserUseCase fail on server error`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.getFollowerById(any()) } } returns Either.Failure(Failure.ServerError)
        every { runBlocking { profileRepository.getFollowingById(any()) }} returns Either.Success(correctFollowingList)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowerViewModel.ViewState(
            isLoginUserLoading = false,
            isLoginUserFollowingLoading = false,
            isFollowerListLoading = false,
            isNetworkError = false,
            isServerError = true,
            isLocalAccountError = false,
            loginUser = correctUserProfile,
            loginUserFollowingList = correctFollowingList,
            followerList = listOf()
        )
    }

    @Test
    fun `verify view state when addUserRelationUseCase invoke successfully`() {
        `verify view state when getFollowingUserUseCase, getFollowerUserUseCase and getLoginUserUseCase succeed`()

        // given
        every { runBlocking { profileRepository.addUserRelation(any(), any()) } } returns Either.Success(Unit)
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.getFollowerById(any()) } } returns Either.Success(correctFollowerList)
        every { runBlocking { profileRepository.getFollowingById(any()) }} returns Either.Success(correctFollowingList)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.addUserRelation(correctUserProfile) }

        // expect
        verify(exactly = 8) { observer.onChanged(any()) } // Init, loginUser, loginFollowing, following, reload, loginUser, loginFollowing, following
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowerViewModel.ViewState(
            isLoginUserFollowingLoading = false,
            isFollowerListLoading = false,
            isLoginUserLoading = false,
            isLocalAccountError = false,
            isServerError = false,
            isNetworkError = false,
            loginUser = correctUserProfile,
            loginUserFollowingList = correctFollowingList,
            followerList = correctFollowerList
        )
    }

    @Test
    fun `verify view state when addUserRelationUseCase fail on network connection`() {
        `verify view state when getFollowingUserUseCase, getFollowerUserUseCase and getLoginUserUseCase succeed`()

        // given
        every { runBlocking { profileRepository.addUserRelation(any(), any()) } } returns Either.Failure(Failure.NetworkConnection)
        // when
        mainCoroutineRule.runBlockingTest { testViewModel.addUserRelation(correctUserProfile) }

        // expect
        verify(exactly = 5) { observer.onChanged(any()) } // Init, loginUser, loginFollowing, following, fail
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowerViewModel.ViewState(
            isLoginUserFollowingLoading = false,
            isFollowerListLoading = false,
            isLoginUserLoading = false,
            isLocalAccountError = false,
            isServerError = false,
            isNetworkError = true,
            loginUser = correctUserProfile,
            loginUserFollowingList = correctFollowingList,
            followerList = correctFollowerList
        )
    }

    @Test
    fun `verify view state when removeUserRelationUseCase invoke successfully`() {
        `verify view state when getFollowingUserUseCase, getFollowerUserUseCase and getLoginUserUseCase succeed`()

        // given
        every { runBlocking { profileRepository.removeUserRelation(any(), any()) } } returns Either.Success(Unit)
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.getFollowerById(any()) } } returns Either.Success(correctFollowerList)
        every { runBlocking { profileRepository.getFollowingById(any()) }} returns Either.Success(correctFollowingList)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.removeUserRelation(correctUserProfile) }

        // expect
        verify(exactly = 8) { observer.onChanged(any()) } // Init, loginUser, loginFollowing, following, reload, loginUser, loginFollowing, following
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowerViewModel.ViewState(
            isLoginUserFollowingLoading = false,
            isFollowerListLoading = false,
            isLoginUserLoading = false,
            isLocalAccountError = false,
            isServerError = false,
            isNetworkError = false,
            loginUser = correctUserProfile,
            loginUserFollowingList = correctFollowingList,
            followerList = correctFollowerList
        )
    }

    @Test
    fun `verify view state when removeUserRelationUseCase fail on network connection`() {
        `verify view state when getFollowingUserUseCase, getFollowerUserUseCase and getLoginUserUseCase succeed`()

        // given
        every { runBlocking { profileRepository.removeUserRelation(any(), any()) } } returns Either.Failure(Failure.NetworkConnection)
        // when
        mainCoroutineRule.runBlockingTest { testViewModel.removeUserRelation(correctUserProfile) }

        // expect
        verify(exactly = 5) { observer.onChanged(any()) } // Init, loginUser, loginFollowing, following, fail
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowerViewModel.ViewState(
            isLoginUserFollowingLoading = false,
            isFollowerListLoading = false,
            isLoginUserLoading = false,
            isLocalAccountError = false,
            isServerError = false,
            isNetworkError = true,
            loginUser = correctUserProfile,
            loginUserFollowingList = correctFollowingList,
            followerList = correctFollowerList
        )
    }

    /**
     * ViewState edge-case test (i.e: useCases failed on different failure type)
     */
    @Test
    fun `verify view state when getLoginUserUseCase failed on local account error and getFollowerUserUseCase failed on network connection`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Failure(Failure.LocalAccountNotFound)
        every { runBlocking { profileRepository.getFollowerById(any()) } } returns Either.Failure(Failure.NetworkConnection)
        every { runBlocking { profileRepository.getFollowingById(any()) }} returns Either.Success(correctFollowingList)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowerViewModel.ViewState(
            isLoginUserLoading = false,
            isLoginUserFollowingLoading = false,
            isFollowerListLoading = false,
            isNetworkError = true,
            isServerError = false,
            isLocalAccountError = true,
            loginUser = null,
            loginUserFollowingList = listOf(),
            followerList = listOf()
        )
    }

    @Test
    fun `verify view state when getLoginUserUseCase failed on local account error and getFollowerUserUseCase failed on server error`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Failure(Failure.LocalAccountNotFound)
        every { runBlocking { profileRepository.getFollowerById(any()) } } returns Either.Failure(Failure.ServerError)
        every { runBlocking { profileRepository.getFollowingById(any()) }} returns Either.Success(correctFollowingList)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowerViewModel.ViewState(
            isLoginUserLoading = false,
            isLoginUserFollowingLoading = false,
            isFollowerListLoading = false,
            isNetworkError = false,
            isServerError = true,
            isLocalAccountError = true,
            loginUser = null,
            loginUserFollowingList = listOf(),
            followerList = listOf()
        )
    }

    @Test
    fun `verify view state when addUserRelationUseCase invoke successfully but reload data all fail`() {
        `verify view state when getFollowingUserUseCase, getFollowerUserUseCase and getLoginUserUseCase succeed`()

        // given
        every { runBlocking { profileRepository.addUserRelation(any(), any()) } } returns Either.Success(Unit)
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Failure(Failure.LocalAccountNotFound)
        every { runBlocking { profileRepository.getFollowerById(any()) } } returns Either.Failure(Failure.ServerError)
        every { runBlocking { profileRepository.getFollowingById(any()) }} returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.addUserRelation(correctUserProfile) }

        // expect
        verify(exactly = 10) { observer.onChanged(any()) } // Init, loginUser, loginFollowing, following, reload, followingLoaded, fail, loginUserLoaded, fail*2
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowerViewModel.ViewState(
            isLoginUserFollowingLoading = false,
            isFollowerListLoading = false,
            isLoginUserLoading = false,
            isLocalAccountError = true,
            isServerError = true,
            isNetworkError = false,
            loginUser = null,
            loginUserFollowingList = listOf(),
            followerList = listOf()
        )
    }

    @Test
    fun `verify view state when removeUserRelationUseCase invoke successfully but reload data all fail`() {
        `verify view state when getFollowingUserUseCase, getFollowerUserUseCase and getLoginUserUseCase succeed`()

        // given
        every { runBlocking { profileRepository.removeUserRelation(any(), any()) } } returns Either.Success(Unit)
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Failure(Failure.LocalAccountNotFound)
        every { runBlocking { profileRepository.getFollowerById(any()) } } returns Either.Failure(Failure.ServerError)
        every { runBlocking { profileRepository.getFollowingById(any()) }} returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.removeUserRelation(correctUserProfile) }

        // expect
        verify(exactly = 10) { observer.onChanged(any()) } // Init, loginUser, loginFollowing, following, reload, followingLoaded, fail, loginUserLoaded, fail*2
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowerViewModel.ViewState(
            isLoginUserFollowingLoading = false,
            isFollowerListLoading = false,
            isLoginUserLoading = false,
            isLocalAccountError = true,
            isServerError = true,
            isNetworkError = false,
            loginUser = null,
            loginUserFollowingList = listOf(),
            followerList = listOf()
        )
    }


}