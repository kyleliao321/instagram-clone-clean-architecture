package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.edit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.service.IntentService
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetLoginUserUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetUserProfileUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.NavigationUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.UpdateUserProfileUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.edit.ProfileEditFragmentArgs
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.edit.ProfileEditFragmentDirections
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
class ProfileEditViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = CoroutineTestRule()

    @MockK(relaxed = true)
    internal lateinit var profileEditFragmentArgs: ProfileEditFragmentArgs

    @MockK(relaxed = true)
    internal lateinit var profileRepository: ProfileRepository

    @MockK(relaxed = true)
    internal lateinit var navigationManager: NavigationManager

    @MockK(relaxed = true)
    internal lateinit var intentService: IntentService

    @MockK(relaxed = true)
    internal lateinit var observer: Observer<ProfileEditViewModel.ViewState>
    
    private lateinit var updateUserProfileUseCase: UpdateUserProfileUseCase

    private lateinit var getUserProfileUseCase: GetUserProfileUseCase

    private lateinit var navigationUseCase: NavigationUseCase

    private lateinit var testViewModel: ProfileEditViewModel

    /**
     * Mock data
     */
    private val correctUserId = 1

    private val correctUserProfile = UserDomainModel(
        id = correctUserId, name = "Kyle", userName = "kyle", postNum = 1, followingNum = 1, followerNum = 1
    )

    private val editedUserProfile = correctUserProfile.copy(userName = "kyle0321")

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        updateUserProfileUseCase = UpdateUserProfileUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        getUserProfileUseCase = GetUserProfileUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        navigationUseCase = NavigationUseCase(navigationManager, mainCoroutineRule.testDispatcher)

        testViewModel =
            ProfileEditViewModel(
                profileEditFragmentArgs,
                intentService,
                getUserProfileUseCase,
                updateUserProfileUseCase,
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
        // given
        val navDir =
            ProfileEditFragmentDirections.actionProfileEditFragmentToProfileMainFragment(
                1
            )
        val params = NavigationUseCase.Param(navDir)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.onNavigateToMainProfile() }

        // expect
        verify(exactly = 1) { navigationManager.onNavEvent(any()) }
    }

    @Test
    fun `profileEditViewModel should initialize with correct view state`() {
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileEditViewModel.ViewState(
            isUserProfileLoading = true,
            isUserProfileUpdating = false,
            isNetworkError = false,
            isServerError = false,
            originalUserProfile = null,
            bindingUserProfile = null
        )
    }

    @Test
    fun `verify view state when getUserProfile succeed`() {
        // given
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(correctUserProfile)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileEditViewModel.ViewState(
            isUserProfileLoading = false,
            isUserProfileUpdating = false,
            isServerError = false,
            isNetworkError = false,
            originalUserProfile = correctUserProfile,
            bindingUserProfile = correctUserProfile
        )
    }

    @Test
    fun `verify view state when getUserProfileUseCase fail on network connection`() {
        // given
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileEditViewModel.ViewState(
            isUserProfileLoading = false,
            isUserProfileUpdating = false,
            isServerError = false,
            isNetworkError = true,
            originalUserProfile = null,
            bindingUserProfile = null
        )
    }

    @Test
    fun `verify view state when getUserProfileUseCase fail on server error`() {
        // given
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(null) // this should be interpreted as server error

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileEditViewModel.ViewState(
            isUserProfileLoading = false,
            isUserProfileUpdating = false,
            isServerError = true,
            isNetworkError = false,
            originalUserProfile = null,
            bindingUserProfile = null
        )
    }

    @Test
    fun `verify view state when updateUserProfileUseCase succeed`() {
        // load data
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.updateUserProfile(any()) } } returns Either.Success(editedUserProfile)
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // edit profile
        testViewModel.stateLiveData.value!!.bindingUserProfile!!.userName = editedUserProfile.userName

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.onUpdateUserProfile() }

        // expect liveData updated four times: init, load, updating, updated
        verify(exactly = 4) { observer.onChanged(any()) }
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileEditViewModel.ViewState(
            isUserProfileLoading = false,
            isUserProfileUpdating = false,
            isNetworkError = false,
            isServerError = false,
            originalUserProfile = editedUserProfile,
            bindingUserProfile = editedUserProfile
        )
    }

    @Test
    fun `verify view state when updateUserProfileUseCase fail on network connection`() {
        // load data
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.updateUserProfile(any()) } } returns Either.Failure(Failure.NetworkConnection)
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // edit profile
        testViewModel.stateLiveData.value!!.bindingUserProfile!!.userName = editedUserProfile.userName

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.onUpdateUserProfile() }

        // expect liveData updated four times: init, load, updating, updated, failOnNetwork
        verify(exactly = 5) { observer.onChanged(any()) }
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileEditViewModel.ViewState(
            isUserProfileLoading = false,
            isUserProfileUpdating = false,
            isNetworkError = true,
            isServerError = false,
            originalUserProfile = correctUserProfile,
            bindingUserProfile = correctUserProfile
        )
    }

    @Test
    fun `verify view state when updateUserProfileUseCase fail on server error`() {
        // load data
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.updateUserProfile(any()) } } returns Either.Failure(Failure.ServerError)
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // edit profile
        testViewModel.stateLiveData.value!!.bindingUserProfile!!.userName = editedUserProfile.userName

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.onUpdateUserProfile() }

        // expect liveData updated four times: init, load, updating, updated, failOnServerError
        verify(exactly = 5) { observer.onChanged(any()) }
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileEditViewModel.ViewState(
            isUserProfileLoading = false,
            isUserProfileUpdating = false,
            isNetworkError = false,
            isServerError = true,
            originalUserProfile = correctUserProfile,
            bindingUserProfile = correctUserProfile
        )
    }
}