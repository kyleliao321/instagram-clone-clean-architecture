package com.example.instagram_clone_clean_architecture.feature.post.presentation.view.post

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.service.IntentService
import com.example.instagram_clone_clean_architecture.feature.post.domain.model.PostUploadDomainModel
import com.example.instagram_clone_clean_architecture.feature.post.domain.repository.PostRepository
import com.example.instagram_clone_clean_architecture.feature.post.domain.usecase.GetLoginUserUseCase
import com.example.instagram_clone_clean_architecture.feature.post.domain.usecase.GetUserSelectedImageUseCase
import com.example.instagram_clone_clean_architecture.feature.post.domain.usecase.UploadPostUseCase
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.CoroutineTestRule
import com.example.library_base.domain.utility.Either
import com.example.library_base.domain.utility.runBlockingTest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class PostViewModelTest {

    @get:Rule
    val mainCoroutineRule = CoroutineTestRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @MockK(relaxed = true)
    internal lateinit var intentService: IntentService

    @MockK(relaxed = true)
    internal lateinit var observer: Observer<PostViewModel.ViewState>

    @MockK(relaxed = true)
    internal lateinit var postRepository: PostRepository

    private lateinit var getLoginUserUseCase: GetLoginUserUseCase

    private lateinit var getUserSelectedImageUseCase: GetUserSelectedImageUseCase

    private lateinit var uploadPostUseCase: UploadPostUseCase

    private lateinit var viewModel: PostViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        getLoginUserUseCase = GetLoginUserUseCase(postRepository, mainCoroutineRule.testDispatcher)
        getUserSelectedImageUseCase = GetUserSelectedImageUseCase(postRepository, mainCoroutineRule.testDispatcher)
        uploadPostUseCase = UploadPostUseCase(postRepository, mainCoroutineRule.testDispatcher)

        viewModel = PostViewModel(
            intentService,
            getLoginUserUseCase,
            getUserSelectedImageUseCase,
            uploadPostUseCase,
            mainCoroutineRule.testDispatcher
        )

        viewModel.stateLiveData.observeForever(observer)
    }

    @After
    fun teardown() {
        viewModel.stateLiveData.removeObserver(observer)
    }

    @Test
    fun `openCamera of intentService should be invoked when promptToTakePhotoByCamera`() {
        // given
        mainCoroutineRule.runBlockingTest { viewModel.promptToTakePhotoByCamera() }

        // expect
        verify(exactly = 1) { intentService.openCamera() }
    }

    @Test
    fun `openPhotoGallery of intentService should be invoked when promptToGetPhotoFromGallery`() {
        // given
        mainCoroutineRule.runBlockingTest { viewModel.promptToGetPhotoFromGallery() }

        // expect
        verify(exactly = 1) { intentService.openPhotoGallery() }
    }

    @Test
    fun `viewModel should initialize with correct view state`() {
        viewModel.stateLiveData.value shouldBeEqualTo PostViewModel.ViewState(
            isLoginUserLoading = true,
            isUserSelectedImageLoading = true,
            isUploading = false,
            isServerError = false,
            isNetworkError = false,
            isPhotoGalleryError = false,
            isCameraError = false,
            isPostNotComplete = false,
            loginUser = null,
            post = PostUploadDomainModel()
        )
    }

    @Test
    fun `verify view state when all UseCases succeed after loadData`() {
        val mockLoginUser = mockk<UserDomainModel>()
        val mockImage = mockk<File>()

        // given
        every { runBlocking { postRepository.getLoginUserProfile() } } returns Either.Success(mockLoginUser)
        every { runBlocking { postRepository.getUserSelectedImage() } } returns Either.Success(mockImage)
        every { mockLoginUser.id } returns 1

        // when
        mainCoroutineRule.runBlockingTest { viewModel.loadData() }

        // expect
        val expectPost = PostUploadDomainModel(imageFile = mockImage, belongUserId = mockLoginUser.id)
        viewModel.stateLiveData.value shouldBeEqualTo PostViewModel.ViewState(
            isUserSelectedImageLoading = false,
            isLoginUserLoading = false,
            isUploading = false,
            isCameraError = false,
            isPhotoGalleryError = false,
            isNetworkError = false,
            isServerError = false,
            isLocalAccountError = false,
            isPostNotComplete = false,
            loginUser = mockLoginUser,
            post = expectPost
        )
    }

    @Test
    fun `verify view state when only getLoginUserUseCase fail during loadData`() {
        val mockImage = mockk<File>()

        // given
        every { runBlocking { postRepository.getLoginUserProfile() } } returns Either.Failure(Failure.LocalAccountNotFound)
        every { runBlocking { postRepository.getUserSelectedImage() } } returns Either.Success(mockImage)

        // when
        mainCoroutineRule.runBlockingTest { viewModel.loadData() }

        // expect
        val expectPost = PostUploadDomainModel(imageFile = mockImage)
        viewModel.stateLiveData.value shouldBeEqualTo PostViewModel.ViewState(
            isUserSelectedImageLoading = false,
            isLoginUserLoading = false,
            isUploading = false,
            isCameraError = false,
            isPhotoGalleryError = false,
            isNetworkError = false,
            isServerError = false,
            isLocalAccountError = true,
            isPostNotComplete = false,
            loginUser = null,
            post = expectPost
        )
    }

    @Test
    fun `verify view state when getUserSelectedImageUseCase return null during loadData`() {
        val mockLoginUser = mockk<UserDomainModel>()

        // given
        every { runBlocking { postRepository.getLoginUserProfile() } } returns Either.Success(mockLoginUser)
        every { runBlocking { postRepository.getUserSelectedImage() } } returns Either.Success(null)
        every { mockLoginUser.id } returns 1

        // when
        mainCoroutineRule.runBlockingTest { viewModel.loadData() }

        // expect
        val expectPost = PostUploadDomainModel(imageFile = null, belongUserId = mockLoginUser.id)
        viewModel.stateLiveData.value shouldBeEqualTo PostViewModel.ViewState(
            isUserSelectedImageLoading = false,
            isLoginUserLoading = false,
            isUploading = false,
            isCameraError = false,
            isPhotoGalleryError = false,
            isNetworkError = false,
            isServerError = false,
            isLocalAccountError = false,
            isPostNotComplete = false,
            loginUser = mockLoginUser,
            post = expectPost
        )
    }

    @Test
    fun `verify view state when getUserSelectedImageUseCase return null and getLoginUseUseCase fail during loadData`() {
        // given
        every { runBlocking { postRepository.getLoginUserProfile() } } returns Either.Failure(Failure.LocalAccountNotFound)
        every { runBlocking { postRepository.getUserSelectedImage() } } returns Either.Success(null)

        // when
        mainCoroutineRule.runBlockingTest { viewModel.loadData() }

        // expect
        val expectPost = PostUploadDomainModel(imageFile = null)
        viewModel.stateLiveData.value shouldBeEqualTo PostViewModel.ViewState(
            isUserSelectedImageLoading = false,
            isLoginUserLoading = false,
            isUploading = false,
            isCameraError = false,
            isPhotoGalleryError = false,
            isNetworkError = false,
            isServerError = false,
            isLocalAccountError = true,
            isPostNotComplete = false,
            loginUser = null,
            post = expectPost
        )
    }

    @Test
    fun `verify view state when uploadPost succeed`() {
        val mockPost = mockk<PostUploadDomainModel>()

        // given
        every { runBlocking { mockPost.isPostReady } } returns true
        every { runBlocking { postRepository.uploadPostUseCase(any()) } } returns Either.Success(Unit)

        // when
        mainCoroutineRule.runBlockingTest {
            viewModel.uploadPost(mockPost)
        }

        // expect
        verify(exactly = 3) { observer.onChanged(any()) } // init, startUploading, finishUploading
    }

    @Test
    fun `verify view state when uploadPost fail on network connection`() {
        val mockPost = mockk<PostUploadDomainModel>()

        // given
        every { runBlocking { mockPost.isPostReady } } returns true
        every { runBlocking { postRepository.uploadPostUseCase(any()) } } returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest {
            viewModel.uploadPost(mockPost)
        }

        // expect
        verify(exactly = 4) { observer.onChanged(any()) } // init, startUploading, finishUploading, network fail
        viewModel.stateLiveData.value shouldBeEqualTo PostViewModel.ViewState(
            isNetworkError = true
        )
    }

    @Test
    fun `verify view state when uploadPost fail on server error`() {
        val mockPost = mockk<PostUploadDomainModel>()

        // given
        every { runBlocking { mockPost.isPostReady } } returns true
        every { runBlocking { postRepository.uploadPostUseCase(any()) } } returns Either.Failure(Failure.ServerError)

        // when
        mainCoroutineRule.runBlockingTest {
            viewModel.uploadPost(mockPost)
        }

        // expect
        verify(exactly = 4) { observer.onChanged(any()) } // init, startUploading, finishUploading, server error
        viewModel.stateLiveData.value shouldBeEqualTo PostViewModel.ViewState(
            isServerError = true
        )
    }

    @Test
    fun `verify view state when uploadPost fail on post incomplete`() {
        val mockPost = mockk<PostUploadDomainModel>()

        // given
        every { runBlocking { mockPost.isPostReady } } returns false

        // when
        mainCoroutineRule.runBlockingTest {
            viewModel.uploadPost(mockPost)
        }

        // expect
        verify(exactly = 4) { observer.onChanged(any()) } // init, startUploading, finishUploading, post fail
        viewModel.stateLiveData.value shouldBeEqualTo PostViewModel.ViewState(
            isPostNotComplete = true
        )
    }

}