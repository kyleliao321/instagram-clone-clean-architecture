package com.example.instagram_clone_clean_architecture.feature.profile.presentation.main

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetPostUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetUserPostUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetUserProfileUseCase
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.CoroutineTestRule
import com.example.library_base.domain.utility.Either
import com.example.library_base.domain.utility.runBlockingTest
import com.example.library_base.presentation.navigation.NavigationManager
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.amshove.kluent.shouldBeEqualTo
import org.junit.After
import org.junit.Assert.*
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
    private val correctUserProfile = UserDomainModel(
        id = 1, name = "Kyle", userName = "kyle", postNum = 0, followerNum = 1, followingNum = 2
    )

    private val correctUserPost = listOf(
        PostDomainModel(
            id = 1, imageSrc = "ss", date = Date(), belongUserId = 1
        )
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        getUserProfileUseCase = GetUserProfileUseCase(profileRepository, mainCoroutineRule.testDispatcher)
        getUserPostUseCase = GetUserPostUseCase(profileRepository, mainCoroutineRule.testDispatcher)

        testViewModel = ProfileMainViewModel(
            navigationManager,
            getUserProfileUseCase,
            getUserPostUseCase,
            mainCoroutineRule.testDispatcher
        )
    }

    @Test
    fun `profileMainViewModel should initialize with correct view state`() {
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileMainViewModel.ViewState(
            isProfileLoading = true,
            isPostLoading = true,
            isNetworkError = false,
            isUnknownError = false,
            isUserProfileError = false,
            userProfile = null,
            userPosts = listOf()
        )
    }

    @Test
    fun `verify view state when getUserProfileUseCase and getUserPostUseCase succeed`() {
        // given
        every { runBlocking { profileRepository.getPostByUserId(any()) } } returns Either.Success(correctUserPost)
        every { runBlocking { profileRepository.getUserProfileById(any()) } } returns Either.Success(correctUserProfile)

        testViewModel.stateLiveData.observeForever(observer)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        verify(exactly = 3) { observer.onChanged(any()) }
        testViewModel.stateLiveData.value shouldBeEqualTo ProfileMainViewModel.ViewState(
            isUserProfileError = false,
            isUnknownError = false,
            isNetworkError = false,
            isPostLoading = false,
            isProfileLoading = false,
            userPosts = correctUserPost,
            userProfile = correctUserProfile
        )

        // cleanup
        testViewModel.stateLiveData.removeObserver(observer)
    }
}