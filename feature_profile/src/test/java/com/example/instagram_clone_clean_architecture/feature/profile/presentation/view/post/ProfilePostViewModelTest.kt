package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.post

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetPostUseCase
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

    private lateinit var testViewModel: ProfilePostViewModel

    /**
     * Mock data
     */
    private val correctPost = PostDomainModel(
        id = 1, imageSrc = "ss", date = Date(), belongUserId = 1
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        getPostUseCase = GetPostUseCase(profileRepository, mainCoroutineRule.testDispatcher)

        testViewModel =
            ProfilePostViewModel(
                profilePostFragmentArgs,
                getPostUseCase,
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
            isPostLoading = true,
            isServerError = false,
            isNetworkError = false,
            post = null
        )
    }

    @Test
    fun `verify view state when getPostUseCase succeed`() {
        // given
        every { runBlocking { profileRepository.getPostByPostId(any()) } } returns Either.Success(correctPost)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfilePostViewModel.ViewState(
            isPostLoading = false,
            isNetworkError = false,
            isServerError = false,
            post = correctPost
        )
    }

    @Test
    fun `verify view state when getPostUseCase fail on network connection`() {
        // given
        every { runBlocking { profileRepository.getPostByPostId(any()) } } returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfilePostViewModel.ViewState(
            isPostLoading = false,
            isNetworkError = true,
            isServerError = false,
            post = null
        )
    }

    @Test
    fun `verify view state when getPostUseCase fail on server error`() {
        // given
        every { runBlocking { profileRepository.getPostByPostId(any()) } } returns Either.Failure(Failure.ServerError)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadData() }

        // expect
        testViewModel.stateLiveData.value shouldBeEqualTo ProfilePostViewModel.ViewState(
            isPostLoading = false,
            isNetworkError = false,
            isServerError = true,
            post = null
        )
    }

}