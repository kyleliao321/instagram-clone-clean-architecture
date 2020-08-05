package com.example.instagram_clone_clean_architecture.feature.profile.presentation.main

import android.util.Log
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
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
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
    val mainCoroutineRule = CoroutineTestRule()

    @MockK(relaxed = true)
    internal lateinit var navigationManager: NavigationManager

    @MockK(relaxed = true)
    internal lateinit var getUserProfileUseCase: GetUserProfileUseCase

    @MockK(relaxed = true)
    internal lateinit var getUserPostUseCase: GetUserPostUseCase

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
    fun `loadData should invoke getUserProfileUseCase and getUserPostUseCase`() {
        // given
        coEvery { getUserPostUseCase.run(any()) } returns Either.Success(correctUserPost)
        coEvery { getUserProfileUseCase.run(any()) } returns Either.Success(correctUserProfile)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        coVerify { getUserPostUseCase(any(), any()) }
        coVerify { getUserProfileUseCase(any(), any()) }
    }

    @Test
    fun `verify view state when getUserProfileUseCase and getUserPostUseCase succeed`() {
        /**
         * TODO: Currently, Mockk will not mock the behavior of the abstract class that all UseCase extended.
         *       As a result, the callback inside viewModel is not trigger, viewState will not be updated.
         */
    }
}