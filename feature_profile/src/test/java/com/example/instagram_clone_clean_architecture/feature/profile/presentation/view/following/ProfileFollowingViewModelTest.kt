package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.following

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
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
class ProfileFollowingViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = com.example.library_test_utils.CoroutineTestRule()

    @MockK(relaxed = true)
    internal lateinit var profileFollowingFragmentArgs: ProfileFollowingFragmentArgs

    @MockK(relaxed = true)
    internal lateinit var profileRepository: ProfileRepository

    @MockK(relaxed = true)
    internal lateinit var navigationManager: NavigationManager

    @MockK(relaxed = true)
    internal lateinit var observer: Observer<ProfileFollowingViewModel.ViewState>

    private lateinit var getLoginUserUseCase: GetLoginUserUseCase

    private lateinit var getFollowingUserUserUseCase: GetFollowingUserUseCase

    private lateinit var addUserRelationUseCase: AddUserRelationUseCase

    private lateinit var removeUserRelationUseCase: RemoveUserRelationUseCase

    private lateinit var navigationUserCase: NavigationUseCase

    private lateinit var testViewModel: ProfileFollowingViewModel

    /**
     * Mock data
     */
    private val correctUserProfile = mockk<UserDomainModel>(relaxed = true)

    private val targetUserProfile = mockk<UserDomainModel>(relaxed = true)

    private val correctFollowingList = mockk<List<UserDomainModel>>(relaxed = true)

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        getLoginUserUseCase =
            GetLoginUserUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        getFollowingUserUserUseCase =
            GetFollowingUserUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        addUserRelationUseCase =
            AddUserRelationUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        removeUserRelationUseCase =
            RemoveUserRelationUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        navigationUserCase = NavigationUseCase(navigationManager, mainCoroutineRule.testDispatcher)

        testViewModel =
            ProfileFollowingViewModel(
                profileFollowingFragmentArgs,
                getLoginUserUseCase,
                getFollowingUserUserUseCase,
                addUserRelationUseCase,
                removeUserRelationUseCase,
                navigationUserCase,
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
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowingViewModel.ViewState(
            isLoginUserFollowingLoading = true,
            isLoginUserLoading = true,
            isFollowingListLoading = true,
            isServerError = false,
            isNetworkError = false,
            isLocalAccountError = false,
            loginUser = null,
            loginUserFollowingList = listOf(),
            followingList = listOf()
        )
    }

    @Test
    fun `verify view state when getFollowingUserUseCase and getLoginUserUseCase succeed`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(
            correctUserProfile
        )
        every { runBlocking { profileRepository.getFollowingById(any()) } } returns Either.Success(
            correctFollowingList
        )

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowingViewModel.ViewState(
            isLoginUserLoading = false,
            isLoginUserFollowingLoading = false,
            isFollowingListLoading = false,
            isNetworkError = false,
            isServerError = false,
            isLocalAccountError = false,
            loginUser = correctUserProfile,
            loginUserFollowingList = correctFollowingList,
            followingList = correctFollowingList
        )
    }

    @Test
    fun `verify view state when only getFollowingUserUseCase fail on network connection`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(
            correctUserProfile
        )
        every { runBlocking { profileRepository.getFollowingById(any()) } } returns Either.Failure(
            Failure.NetworkConnection
        )

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowingViewModel.ViewState(
            isLoginUserLoading = false,
            isLoginUserFollowingLoading = false,
            isFollowingListLoading = false,
            isNetworkError = true,
            isServerError = false,
            isLocalAccountError = false,
            loginUser = correctUserProfile,
            loginUserFollowingList = listOf(),
            followingList = listOf()
        )
    }

    @Test
    fun `verify view state when only getLoginUserUseCase fail on local account error`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Failure(
            Failure.LocalAccountNotFound
        )
        every { runBlocking { profileRepository.getFollowingById(any()) } } returns Either.Success(
            correctFollowingList
        )

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowingViewModel.ViewState(
            isLoginUserLoading = false,
            isLoginUserFollowingLoading = false,
            isFollowingListLoading = false,
            isNetworkError = false,
            isServerError = false,
            isLocalAccountError = true,
            loginUser = null,
            loginUserFollowingList = listOf(),
            followingList = correctFollowingList
        )
    }

    @Test
    fun `verify view state when only getFollowingUserUseCase fail on server error`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(
            correctUserProfile
        )
        every { runBlocking { profileRepository.getFollowingById(any()) } } returns Either.Failure(
            Failure.ServerError
        )

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowingViewModel.ViewState(
            isLoginUserLoading = false,
            isLoginUserFollowingLoading = false,
            isFollowingListLoading = false,
            isNetworkError = false,
            isServerError = true,
            isLocalAccountError = false,
            loginUser = correctUserProfile,
            loginUserFollowingList = listOf(),
            followingList = listOf()
        )
    }

    @Test
    fun `verify view state when addUserRelationUseCase invoke successfully`() {
        `verify view state when getFollowingUserUseCase and getLoginUserUseCase succeed`()

        // given
        every {
            runBlocking {
                profileRepository.addUserRelation(
                    any(),
                    any()
                )
            }
        } returns Either.Success(Unit)
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(
            correctUserProfile
        )
        every { runBlocking { profileRepository.getFollowingById(any()) } } returns Either.Success(
            correctFollowingList
        )

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.addUserRelation(correctUserProfile) }

        // expect
        verify(exactly = 8) { observer.onChanged(any()) } // Init, loginUser, loginFollowing, following, reload, loginUser, loginFollowing, following
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowingViewModel.ViewState(
            isLoginUserFollowingLoading = false,
            isFollowingListLoading = false,
            isLoginUserLoading = false,
            isLocalAccountError = false,
            isServerError = false,
            isNetworkError = false,
            loginUser = correctUserProfile,
            loginUserFollowingList = correctFollowingList,
            followingList = correctFollowingList
        )
    }

    @Test
    fun `verify view state when addUserRelationUseCase fail on network connection`() {
        `verify view state when getFollowingUserUseCase and getLoginUserUseCase succeed`()

        // given
        every {
            runBlocking {
                profileRepository.addUserRelation(
                    any(),
                    any()
                )
            }
        } returns Either.Failure(Failure.NetworkConnection)
        // when
        mainCoroutineRule.runBlockingTest { testViewModel.addUserRelation(correctUserProfile) }

        // expect
        verify(exactly = 5) { observer.onChanged(any()) } // Init, loginUser, loginFollowing, following, fail
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowingViewModel.ViewState(
            isLoginUserFollowingLoading = false,
            isFollowingListLoading = false,
            isLoginUserLoading = false,
            isLocalAccountError = false,
            isServerError = false,
            isNetworkError = true,
            loginUser = correctUserProfile,
            loginUserFollowingList = correctFollowingList,
            followingList = correctFollowingList
        )
    }

    @Test
    fun `verify view state when removeUserRelationUseCase invoke successfully`() {
        `verify view state when getFollowingUserUseCase and getLoginUserUseCase succeed`()

        // given
        every {
            runBlocking {
                profileRepository.removeUserRelation(
                    any(),
                    any()
                )
            }
        } returns Either.Success(Unit)
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(
            correctUserProfile
        )
        every { runBlocking { profileRepository.getFollowingById(any()) } } returns Either.Success(
            correctFollowingList
        )

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.removeUserRelation(correctUserProfile) }

        // expect
        verify(exactly = 8) { observer.onChanged(any()) } // Init, loginUser, loginFollowing, following, reload, loginUser, loginFollowing, following
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowingViewModel.ViewState(
            isLoginUserFollowingLoading = false,
            isFollowingListLoading = false,
            isLoginUserLoading = false,
            isLocalAccountError = false,
            isServerError = false,
            isNetworkError = false,
            loginUser = correctUserProfile,
            loginUserFollowingList = correctFollowingList,
            followingList = correctFollowingList
        )
    }

    @Test
    fun `verify view state when removeUserRelationUseCase fail on network connection`() {
        `verify view state when getFollowingUserUseCase and getLoginUserUseCase succeed`()

        // given
        every {
            runBlocking {
                profileRepository.removeUserRelation(
                    any(),
                    any()
                )
            }
        } returns Either.Failure(Failure.NetworkConnection)
        // when
        mainCoroutineRule.runBlockingTest { testViewModel.removeUserRelation(correctUserProfile) }

        // expect
        verify(exactly = 5) { observer.onChanged(any()) } // Init, loginUser, loginFollowing, following, fail
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowingViewModel.ViewState(
            isLoginUserFollowingLoading = false,
            isFollowingListLoading = false,
            isLoginUserLoading = false,
            isLocalAccountError = false,
            isServerError = false,
            isNetworkError = true,
            loginUser = correctUserProfile,
            loginUserFollowingList = correctFollowingList,
            followingList = correctFollowingList
        )
    }

    /**
     * ViewState edge-case test (i.e: useCases failed on different failure type)
     */
    @Test
    fun `verify view state when getLoginUserUseCase failed on local account error and getFollowingUseCase failed on network connection`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Failure(
            Failure.LocalAccountNotFound
        )
        every { runBlocking { profileRepository.getFollowingById(any()) } } returns Either.Failure(
            Failure.NetworkConnection
        )

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowingViewModel.ViewState(
            isLoginUserLoading = false,
            isLoginUserFollowingLoading = false,
            isFollowingListLoading = false,
            isNetworkError = true,
            isServerError = false,
            isLocalAccountError = true,
            loginUser = null,
            loginUserFollowingList = listOf(),
            followingList = listOf()
        )
    }

    @Test
    fun `verify view state when getLoginUserUseCase failed on local account error and getFollowingUseCase failed on server error`() {
        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Failure(
            Failure.LocalAccountNotFound
        )
        every { runBlocking { profileRepository.getFollowingById(any()) } } returns Either.Failure(
            Failure.ServerError
        )

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowingViewModel.ViewState(
            isLoginUserLoading = false,
            isLoginUserFollowingLoading = false,
            isFollowingListLoading = false,
            isNetworkError = false,
            isServerError = true,
            isLocalAccountError = true,
            loginUser = null,
            loginUserFollowingList = listOf(),
            followingList = listOf()
        )
    }

    @Test
    fun `verify view state when addUserRelationUseCase invoke successfully but reload data all fail`() {
        `verify view state when getFollowingUserUseCase and getLoginUserUseCase succeed`()

        // given
        every {
            runBlocking {
                profileRepository.addUserRelation(
                    any(),
                    any()
                )
            }
        } returns Either.Success(Unit)
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Failure(
            Failure.LocalAccountNotFound
        )
        every { runBlocking { profileRepository.getFollowingById(any()) } } returns Either.Failure(
            Failure.NetworkConnection
        )

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.addUserRelation(correctUserProfile) }

        // expect
        verify(exactly = 10) { observer.onChanged(any()) } // Init, loginUser, loginFollowing, following, reload, followingLoaded, fail, loginUserLoaded, fail*2
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowingViewModel.ViewState(
            isLoginUserFollowingLoading = false,
            isFollowingListLoading = false,
            isLoginUserLoading = false,
            isLocalAccountError = true,
            isServerError = false,
            isNetworkError = true,
            loginUser = null,
            loginUserFollowingList = listOf(),
            followingList = listOf()
        )
    }

    @Test
    fun `verify view state when removeUserRelationUseCase invoke successfully but reload data all fail`() {
        `verify view state when getFollowingUserUseCase and getLoginUserUseCase succeed`()

        // given
        every {
            runBlocking {
                profileRepository.removeUserRelation(
                    any(),
                    any()
                )
            }
        } returns Either.Success(Unit)
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Failure(
            Failure.LocalAccountNotFound
        )
        every { runBlocking { profileRepository.getFollowingById(any()) } } returns Either.Failure(
            Failure.NetworkConnection
        )

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.removeUserRelation(correctUserProfile) }

        // expect
        verify(exactly = 10) { observer.onChanged(any()) } // Init, loginUser, loginFollowing, following, reload, followingLoaded, fail, loginUserLoaded, fail*2
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileFollowingViewModel.ViewState(
            isLoginUserFollowingLoading = false,
            isFollowingListLoading = false,
            isLoginUserLoading = false,
            isLocalAccountError = true,
            isServerError = false,
            isNetworkError = true,
            loginUser = null,
            loginUserFollowingList = listOf(),
            followingList = listOf()
        )
    }

}