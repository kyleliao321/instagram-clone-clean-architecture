package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.follower

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.*
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import com.example.library_base.presentation.navigation.NavigationManager
import com.example.library_test_utils.runBlockingTest
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

@RunWith(JUnit4::class)
class ProfileFollowerViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = com.example.library_test_utils.CoroutineTestRule()

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
    private val correctUserProfile = mockk<UserDomainModel>(relaxed = true)

    private val targetUserProfile = mockk<UserDomainModel>(relaxed = true)

    private val correctFollowerList = mockk<List<UserDomainModel>>(relaxed = true)

    private val correctFollowingList = mockk<List<UserDomainModel>>(relaxed = true)

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        getLoginUserUseCase = spyk(GetLoginUserUseCase(profileRepository, mainCoroutineRule.testDispatcher))
        getFollowerUserUseCase = spyk(GetFollowerUserUseCase(profileRepository, mainCoroutineRule.testDispatcher))
        getFollowingUserUseCase = spyk(GetFollowingUserUseCase(profileRepository, mainCoroutineRule.testDispatcher))
        addUserRelationUseCase = spyk(AddUserRelationUseCase(profileRepository, mainCoroutineRule.testDispatcher))
        removeUserRelationUseCase = spyk(RemoveUserRelationUseCase(profileRepository, mainCoroutineRule.testDispatcher))
        navigationUseCase = spyk(NavigationUseCase(navigationManager, mainCoroutineRule.testDispatcher))

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
        every { runBlocking { getLoginUserUseCase.run(any()) } } returns Either.Success(correctUserProfile)
        every { runBlocking { getFollowerUserUseCase.run(any()) } } returns Either.Success(correctFollowerList)
        every { runBlocking { getFollowingUserUseCase.run(any()) }} returns Either.Success(correctFollowingList)

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
        every { runBlocking { getLoginUserUseCase.run(any()) } } returns Either.Success(correctUserProfile)
        every { runBlocking { getFollowerUserUseCase.run(any()) } } returns Either.Failure(Failure.NetworkConnection)
        every { runBlocking { getFollowingUserUseCase.run(any()) }} returns Either.Success(correctFollowingList)

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
        every { runBlocking { getLoginUserUseCase.run(any()) } } returns Either.Failure(Failure.LocalAccountNotFound)
        every { runBlocking { getFollowerUserUseCase.run(any()) } } returns Either.Success(correctFollowerList)
        every { runBlocking { getFollowingUserUseCase.run(any()) }} returns Either.Success(correctFollowingList)

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
        every { runBlocking { getLoginUserUseCase.run(any()) } } returns Either.Success(correctUserProfile)
        every { runBlocking { getFollowerUserUseCase.run(any()) } } returns Either.Failure(Failure.ServerError)
        every { runBlocking { getFollowingUserUseCase.run(any()) }} returns Either.Success(correctFollowingList)

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
        every { runBlocking { addUserRelationUseCase.run(any()) } } returns Either.Success(Unit)
        every { runBlocking { getLoginUserUseCase.run(any()) } } returns Either.Success(correctUserProfile)
        every { runBlocking { getFollowerUserUseCase.run(any()) } } returns Either.Success(correctFollowerList)
        every { runBlocking { getFollowingUserUseCase.run(any()) }} returns Either.Success(correctFollowingList)

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
        every { runBlocking { addUserRelationUseCase.run(any()) } } returns Either.Failure(Failure.NetworkConnection)
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
        every { runBlocking { removeUserRelationUseCase.run(any()) } } returns Either.Success(Unit)
        every { runBlocking { getLoginUserUseCase.run(any()) } } returns Either.Success(correctUserProfile)
        every { runBlocking { getFollowerUserUseCase.run(any()) } } returns Either.Success(correctFollowerList)
        every { runBlocking { getFollowingUserUseCase.run(any()) }} returns Either.Success(correctFollowingList)

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
        every { runBlocking { removeUserRelationUseCase.run(any()) } } returns Either.Failure(Failure.NetworkConnection)
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
        every { runBlocking { getLoginUserUseCase.run(any()) } } returns Either.Failure(Failure.LocalAccountNotFound)
        every { runBlocking { getFollowerUserUseCase.run(any()) } } returns Either.Failure(Failure.NetworkConnection)
        every { runBlocking { getFollowingUserUseCase.run(any()) }} returns Either.Success(correctFollowingList)

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
        every { runBlocking { getLoginUserUseCase.run(any()) } } returns Either.Failure(Failure.LocalAccountNotFound)
        every { runBlocking { getFollowerUserUseCase.run(any()) } } returns Either.Failure(Failure.ServerError)
        every { runBlocking { getFollowingUserUseCase.run(any()) }} returns Either.Success(correctFollowingList)

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
        every { runBlocking { addUserRelationUseCase.run(any()) } } returns Either.Success(Unit)
        every { runBlocking { getLoginUserUseCase.run(any()) } } returns Either.Failure(Failure.LocalAccountNotFound)
        every { runBlocking { getFollowerUserUseCase.run(any()) } } returns Either.Failure(Failure.ServerError)
        every { runBlocking { getFollowingUserUseCase.run(any()) }} returns Either.Failure(Failure.NetworkConnection)

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
        every { runBlocking { removeUserRelationUseCase.run(any()) } } returns Either.Success(Unit)
        every { runBlocking { getLoginUserUseCase.run(any()) } } returns Either.Failure(Failure.LocalAccountNotFound)
        every { runBlocking { getFollowerUserUseCase.run(any()) } } returns Either.Failure(Failure.ServerError)
        every { runBlocking { getFollowingUserUseCase.run(any()) }} returns Either.Failure(Failure.NetworkConnection)

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