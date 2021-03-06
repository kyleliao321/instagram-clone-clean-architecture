package com.example.instagram_clone_clean_architecture.feature.post.presentation.view.post

import android.graphics.Bitmap
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.PostUploadDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.service.IntentService
import com.example.instagram_clone_clean_architecture.feature.post.domain.repository.PostRepository
import com.example.instagram_clone_clean_architecture.feature.post.domain.usecase.GetBitmapUseCase
import com.example.instagram_clone_clean_architecture.feature.post.domain.usecase.GetLoginUserUseCase
import com.example.instagram_clone_clean_architecture.feature.post.domain.usecase.GetUserSelectedImageUseCase
import com.example.instagram_clone_clean_architecture.feature.post.domain.usecase.UploadPostUseCase
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
class PostViewModelTest {

    @get:Rule
    val mainCoroutineRule = com.example.library_test_utils.CoroutineTestRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @MockK(relaxed = true)
    internal lateinit var intentService: IntentService

    @MockK(relaxed = true)
    internal lateinit var observer: Observer<PostViewModel.ViewState>

    @MockK(relaxed = true)
    internal lateinit var postRepository: PostRepository

    @MockK(relaxed = true)
    internal lateinit var navManager: NavigationManager

    private lateinit var getLoginUserUseCase: GetLoginUserUseCase

    private lateinit var getUserSelectedImageUseCase: GetUserSelectedImageUseCase

    private lateinit var uploadPostUseCase: UploadPostUseCase

    private lateinit var getBitmapUseCase: GetBitmapUseCase

    private lateinit var mockPostUseCase: UploadPostUseCase

    private lateinit var viewModel: PostViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        getLoginUserUseCase =
            spyk(GetLoginUserUseCase(postRepository, mainCoroutineRule.testDispatcher))
        getUserSelectedImageUseCase =
            spyk(GetUserSelectedImageUseCase(postRepository, mainCoroutineRule.testDispatcher))
        uploadPostUseCase =
            spyk(UploadPostUseCase(postRepository, mainCoroutineRule.testDispatcher))
        getBitmapUseCase = spyk(GetBitmapUseCase(postRepository, mainCoroutineRule.testDispatcher))
        mockPostUseCase =
            spyk(spyk(UploadPostUseCase(postRepository, mainCoroutineRule.testDispatcher)))

        viewModel = PostViewModel(
            intentService,
            navManager,
            getLoginUserUseCase,
            getUserSelectedImageUseCase,
            mockPostUseCase,
            getBitmapUseCase,
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
            isBitmapDecoding = false,
            isUploading = false,
            isServerError = false,
            isNetworkError = false,
            isPhotoGalleryError = false,
            isCameraError = false,
            isPostNotComplete = false,
            loginUser = null,
            post = PostUploadDomainModel(),
            bitmap = null
        )
    }

    @Test
    fun `verify view state when all UseCases succeed after loadData`() {
        val mockLoginUser = mockk<UserDomainModel>()
        val mockImage = mockk<Uri>()
        val mockBitmap = mockk<Bitmap>()

        // given
        every { runBlocking { getLoginUserUseCase.run(any()) } } returns Either.Success(
            mockLoginUser
        )
        every { runBlocking { getUserSelectedImageUseCase.run(any()) } } returns Either.Success(
            mockImage
        )
        every { runBlocking { getBitmapUseCase.run(any()) } } returns Either.Success(mockBitmap)
        every { mockLoginUser.id } returns "mockId"

        // when
        mainCoroutineRule.runBlockingTest { viewModel.loadData() }

        // expect
        val expectPost =
            PostUploadDomainModel(
                imageUri = mockImage,
                belongUserId = mockLoginUser.id
            )
        viewModel.stateLiveData.value shouldBeEqualTo PostViewModel.ViewState(
            isUserSelectedImageLoading = false,
            isLoginUserLoading = false,
            isBitmapDecoding = false,
            isUploading = false,
            isCameraError = false,
            isPhotoGalleryError = false,
            isNetworkError = false,
            isServerError = false,
            isLocalAccountError = false,
            isPostNotComplete = false,
            loginUser = mockLoginUser,
            post = expectPost,
            bitmap = mockBitmap
        )
    }

    @Test
    fun `verify view state when only getLoginUserUseCase fail during loadData`() {
        val mockImage = mockk<Uri>()
        val mockBitmap = mockk<Bitmap>()

        // given
        every { runBlocking { getLoginUserUseCase.run(any()) } } returns Either.Failure(Failure.LocalAccountNotFound)
        every { runBlocking { getUserSelectedImageUseCase.run(any()) } } returns Either.Success(
            mockImage
        )
        every { runBlocking { getBitmapUseCase.run(any()) } } returns Either.Success(mockBitmap)

        // when
        mainCoroutineRule.runBlockingTest { viewModel.loadData() }

        // expect
        val expectPost =
            PostUploadDomainModel(
                imageUri = mockImage
            )
        viewModel.stateLiveData.value shouldBeEqualTo PostViewModel.ViewState(
            isUserSelectedImageLoading = false,
            isLoginUserLoading = false,
            isBitmapDecoding = false,
            isUploading = false,
            isCameraError = false,
            isPhotoGalleryError = false,
            isNetworkError = false,
            isServerError = false,
            isLocalAccountError = true,
            isPostNotComplete = false,
            loginUser = null,
            post = expectPost,
            bitmap = mockBitmap
        )
    }

    @Test
    fun `verify view state when getUserSelectedImageUseCase return null during loadData`() {
        val mockLoginUser = mockk<UserDomainModel>()

        // given
        every { runBlocking { getLoginUserUseCase.run(any()) } } returns Either.Success(
            mockLoginUser
        )
        every { runBlocking { getUserSelectedImageUseCase.run(any()) } } returns Either.Success(null)
        every { mockLoginUser.id } returns "mockId"

        // when
        mainCoroutineRule.runBlockingTest { viewModel.loadData() }

        // expect
        val expectPost =
            PostUploadDomainModel(
                imageUri = null,
                belongUserId = mockLoginUser.id
            )
        viewModel.stateLiveData.value shouldBeEqualTo PostViewModel.ViewState(
            isUserSelectedImageLoading = false,
            isLoginUserLoading = false,
            isBitmapDecoding = false,
            isUploading = false,
            isCameraError = false,
            isPhotoGalleryError = false,
            isNetworkError = false,
            isServerError = false,
            isLocalAccountError = false,
            isPostNotComplete = false,
            loginUser = mockLoginUser,
            post = expectPost,
            bitmap = null
        )
    }

    @Test
    fun `verify view state when getUserSelectedImageUseCase return null and getLoginUseUseCase fail during loadData`() {
        // given
        every { runBlocking { getLoginUserUseCase.run(any()) } } returns Either.Failure(Failure.LocalAccountNotFound)
        every { runBlocking { getUserSelectedImageUseCase.run(any()) } } returns Either.Success(null)

        // when
        mainCoroutineRule.runBlockingTest { viewModel.loadData() }

        // expect
        val expectPost =
            PostUploadDomainModel(
                imageUri = null
            )
        viewModel.stateLiveData.value shouldBeEqualTo PostViewModel.ViewState(
            isUserSelectedImageLoading = false,
            isLoginUserLoading = false,
            isBitmapDecoding = false,
            isUploading = false,
            isCameraError = false,
            isPhotoGalleryError = false,
            isNetworkError = false,
            isServerError = false,
            isLocalAccountError = true,
            isPostNotComplete = false,
            loginUser = null,
            post = expectPost,
            bitmap = null
        )
    }

    @Test
    fun `verify view state when uploadPost succeed`() {
        // given
        val mockPost = mockk<PostUploadDomainModel>(relaxed = true)
        val mockReturnPost = mockk<PostDomainModel>()
        val mockUser = mockk<UserDomainModel>(relaxed = true)

        every { runBlocking { mockPostUseCase.run(any()) } } returns Either.Success(mockReturnPost)

        // when
        mainCoroutineRule.runBlockingTest {
            viewModel.uploadPost(mockPost, mockUser)
        }

        // expect
        verify(exactly = 3) { observer.onChanged(any()) } // init, startUploading, finishUploading
        verify(exactly = 1) { navManager.onNavEvent(any()) }
    }

    @Test
    fun `verify view state when uploadPost fail on network connection`() {
        // given
        val mockPost = mockk<PostUploadDomainModel>(relaxed = true)
        val mockUser = mockk<UserDomainModel>(relaxed = true)

        every { runBlocking { mockPostUseCase.run(any()) } } returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest {
            viewModel.uploadPost(mockPost, mockUser)
        }

        // expect
        verify(exactly = 4) { observer.onChanged(any()) } // init, startUploading, finishUploading, network fail
        viewModel.stateLiveData.value shouldBeEqualTo PostViewModel.ViewState(
            isNetworkError = true
        )
    }

    @Test
    fun `verify view state when uploadPost fail on server error`() {
        val mockPost = mockk<PostUploadDomainModel>(relaxed = true)
        val mockUser = mockk<UserDomainModel>(relaxed = true)

        // given
        every { runBlocking { mockPostUseCase.run(any()) } } returns Either.Failure(Failure.ServerError)

        // when
        mainCoroutineRule.runBlockingTest {
            viewModel.uploadPost(mockPost, mockUser)
        }

        // expect
        verify(exactly = 4) { observer.onChanged(any()) } // init, startUploading, finishUploading, server error
        viewModel.stateLiveData.value shouldBeEqualTo PostViewModel.ViewState(
            isServerError = true
        )
    }

    @Test
    fun `verify view state when uploadPost fail on post incomplete`() {
        val mockPost = mockk<PostUploadDomainModel>(relaxed = true)
        val mockUser = mockk<UserDomainModel>(relaxed = true)

        // given
        every { runBlocking { mockPostUseCase.run(any()) } } returns Either.Failure(Failure.PostNotComplete)

        // when
        mainCoroutineRule.runBlockingTest {
            viewModel.uploadPost(mockPost, mockUser)
        }

        // expect
        verify(exactly = 4) { observer.onChanged(any()) } // init, startUploading, finishUploading, post fail
        viewModel.stateLiveData.value shouldBeEqualTo PostViewModel.ViewState(
            isPostNotComplete = true
        )
    }

}