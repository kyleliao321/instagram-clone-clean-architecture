package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.edit

import android.graphics.Bitmap
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.service.IntentService
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.*
import com.example.library_base.domain.exception.Failure
import com.example.library_test_utils.CoroutineTestRule
import com.example.library_base.domain.utility.Either
import com.example.library_test_utils.runBlockingTest
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

@RunWith(JUnit4::class)
class ProfileEditViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = com.example.library_test_utils.CoroutineTestRule()

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

    private lateinit var consumeUserSelectedImageUseCase: ConsumeUserSelectedImageUseCase

    private lateinit var getBitmapUseCase: GetBitmapUseCase

    private lateinit var navigationUseCase: NavigationUseCase

    private lateinit var testViewModel: ProfileEditViewModel

    /**
     * Mock data
     */
    private val correctUserId = "mockId"

    private val correctUserProfile = UserDomainModel(
        id = correctUserId, name = "Kyle", userName = "kyle", postNum = 1, followingNum = 1, followerNum = 1
    )

    private val editedUserProfile = correctUserProfile.copy(userName = "kyle0321")

    @MockK(relaxed = true)
    internal lateinit var mockUri: Uri

    @MockK(relaxed = true)
    internal lateinit var mockBitmap: Bitmap

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        updateUserProfileUseCase = spyk(UpdateUserProfileUseCase(profileRepository, mainCoroutineRule.testDispatcher))
        getUserProfileUseCase = GetUserProfileUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        navigationUseCase = NavigationUseCase(navigationManager, mainCoroutineRule.testDispatcher)
        consumeUserSelectedImageUseCase = ConsumeUserSelectedImageUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        getBitmapUseCase = GetBitmapUseCase(profileRepository, mainCoroutineRule.testDispatcher)

        testViewModel =
            ProfileEditViewModel(
                profileEditFragmentArgs,
                intentService,
                getUserProfileUseCase,
                updateUserProfileUseCase,
                consumeUserSelectedImageUseCase,
                getBitmapUseCase,
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
                "mockId"
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
            isCachedImageLoading = true,
            isNetworkError = false,
            isServerError = false,
            originalUserProfile = null,
            bindingUserProfile = null,
            cacheImage = null,
            cacheImageUri = null
        )
    }

    @Test
    fun `verify view state when all UseCase succeed`() {
        // given
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.consumeUserSelectedImageUri() } } returns Either.Success(mockUri)
        every { runBlocking { profileRepository.getBitmap(any()) } } returns Either.Success(mockBitmap)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileEditViewModel.ViewState(
            isUserProfileLoading = false,
            isUserProfileUpdating = false,
            isCachedImageLoading = false,
            isServerError = false,
            isNetworkError = false,
            originalUserProfile = correctUserProfile,
            bindingUserProfile = correctUserProfile,
            cacheImage = mockBitmap,
            cacheImageUri = mockUri
        )
    }

    @Test
    fun `verify view state when only getUserProfileUseCase fail on network connection`() {
        // given
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Failure(Failure.NetworkConnection)
        every { runBlocking { profileRepository.consumeUserSelectedImageUri() } } returns Either.Success(mockUri)
        every { runBlocking { profileRepository.getBitmap(any()) } } returns Either.Success(mockBitmap)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileEditViewModel.ViewState(
            isUserProfileLoading = false,
            isUserProfileUpdating = false,
            isCachedImageLoading = false,
            isServerError = false,
            isNetworkError = true,
            originalUserProfile = null,
            bindingUserProfile = null,
            cacheImage = mockBitmap,
            cacheImageUri = mockUri
        )
    }

    @Test
    fun `verify view state when only getUserProfileUseCase fail on server error`() {
        // given
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(null) // this should be interpreted as server error
        every { runBlocking { profileRepository.consumeUserSelectedImageUri() } } returns Either.Success(mockUri)
        every { runBlocking { profileRepository.getBitmap(any()) } } returns Either.Success(mockBitmap)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileEditViewModel.ViewState(
            isUserProfileLoading = false,
            isUserProfileUpdating = false,
            isCachedImageLoading = false,
            isServerError = true,
            isNetworkError = false,
            originalUserProfile = null,
            bindingUserProfile = null,
            cacheImage = mockBitmap,
            cacheImageUri = mockUri
        )
    }

    @Test
    fun `verify view state when only consumeUserSelectedImageUseCase fail`() {
        // given
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.consumeUserSelectedImageUri() } } returns Either.Failure(Failure.CacheNotFound)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileEditViewModel.ViewState(
            isUserProfileLoading = false,
            isUserProfileUpdating = false,
            isCachedImageLoading = false,
            isServerError = false,
            isNetworkError = false,
            originalUserProfile = correctUserProfile,
            bindingUserProfile = correctUserProfile,
            cacheImage = null,
            cacheImageUri = null
        )
    }

    @Test
    fun `verify view state when updateUserProfileUseCase succeed`() {
        // load data
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.consumeUserSelectedImageUri() } } returns Either.Success(mockUri)
        every { runBlocking { profileRepository.getBitmap(any()) } } returns Either.Success(mockBitmap)
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        coEvery { updateUserProfileUseCase.run(any()) } returns Either.Success(editedUserProfile)

        // edit profile
        testViewModel.stateLiveData.value!!.bindingUserProfile!!.userName = editedUserProfile.userName

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.onUpdateUserProfile() }

        // expect liveData updated four times: init, loadProfile, loadImage, updating, updated
        verify(exactly = 5) { observer.onChanged(any()) }
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileEditViewModel.ViewState(
            isUserProfileLoading = false,
            isUserProfileUpdating = false,
            isCachedImageLoading = false,
            isNetworkError = false,
            isServerError = false,
            originalUserProfile = editedUserProfile,
            bindingUserProfile = editedUserProfile,
            cacheImage = mockBitmap,
            cacheImageUri = mockUri
        )
    }

    @Test
    fun `verify view state when updateUserProfileUseCase fail on network connection`() {
        // load data
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.consumeUserSelectedImageUri() } } returns Either.Success(mockUri)
        every { runBlocking { profileRepository.getBitmap(any()) } } returns Either.Success(mockBitmap)
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        coEvery { updateUserProfileUseCase.run(any()) } returns Either.Failure(Failure.NetworkConnection)
        // edit profile
        testViewModel.stateLiveData.value!!.bindingUserProfile!!.userName = editedUserProfile.userName

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.onUpdateUserProfile() }

        // expect liveData updated four times: init, loadProfile,  , updating, updated, failOnNetwork
        verify(exactly = 6) { observer.onChanged(any()) }
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileEditViewModel.ViewState(
            isUserProfileLoading = false,
            isUserProfileUpdating = false,
            isCachedImageLoading = false,
            isNetworkError = true,
            isServerError = false,
            cacheImage = mockBitmap,
            originalUserProfile = correctUserProfile,
            bindingUserProfile = correctUserProfile,
            cacheImageUri = mockUri
        )
    }

    @Test
    fun `verify view state when updateUserProfileUseCase fail on server error`() {
        // load data
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(correctUserProfile)
        every { runBlocking { profileRepository.consumeUserSelectedImageUri() } } returns Either.Success(mockUri)
        every { runBlocking { profileRepository.getBitmap(any()) } } returns Either.Success(mockBitmap)
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        coEvery { updateUserProfileUseCase.run(any()) } returns Either.Failure(Failure.ServerError)
        // edit profile
        testViewModel.stateLiveData.value!!.bindingUserProfile!!.userName = editedUserProfile.userName

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.onUpdateUserProfile() }

        // expect liveData updated four times: init, loadProfile, loadImage, updating, updated, failOnServerError
        verify(exactly = 6) { observer.onChanged(any()) }
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileEditViewModel.ViewState(
            isUserProfileLoading = false,
            isUserProfileUpdating = false,
            isCachedImageLoading = false,
            isNetworkError = false,
            isServerError = true,
            cacheImage = mockBitmap,
            originalUserProfile = correctUserProfile,
            bindingUserProfile = correctUserProfile,
            cacheImageUri = mockUri
        )
    }
}