package com.example.instagram_clone_clean_architecture.feature.post.domain.usecase

import android.graphics.Bitmap
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.PostUploadDomainModel
import com.example.instagram_clone_clean_architecture.feature.post.domain.repository.PostRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.extension.getJpegByteArray
import com.example.library_base.domain.extension.resizeAndCrop
import com.example.library_base.domain.utility.Either
import com.example.library_test_utils.runBlockingTest
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.internal.runners.statements.Fail
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class UploadPostUseCaseTest {

    @get:Rule
    val mainCoroutineRule = com.example.library_test_utils.CoroutineTestRule()

    @MockK(relaxed = true)
    internal lateinit var postRepository: PostRepository

    private lateinit var testUseCase: UploadPostUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        mockkStatic("com.example.library_base.domain.extension.BitmapExtensionKt")

        testUseCase = UploadPostUseCase(postRepository, mainCoroutineRule.testDispatcher)
    }

    @Test
    fun `should return post not complete failure when post is not ready`() {
        var result: Either<PostDomainModel, Failure>? = null

        // given
        val post = mockk<PostUploadDomainModel>(relaxed = true)
        val param = mockk<UploadPostUseCase.Param>(relaxed = true)

        every { param.post } returns post
        every { post.isPostReady } returns false

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(param) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.PostNotComplete)
    }

    @Test
    fun `should return whatever postRepository getBitmap return when it return failure`() {
        var result: Either<PostDomainModel, Failure>? = null

        // given
        val post = mockk<PostUploadDomainModel>(relaxed = true)
        val param = mockk<UploadPostUseCase.Param>(relaxed = true)

        every { param.post } returns post
        every { post.isPostReady } returns true
        every { post.imageUri } returns mockk()
        coEvery { postRepository.getBitmap(any()) } returns Either.Failure(Failure.ExternalImageDecodeFail)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(param) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.ExternalImageDecodeFail)
    }

    @Test
    fun `should return whatever postRepository cacheCompressedImageFile when it return failure`() {
        var result: Either<PostDomainModel, Failure>? = null

        // given
        val mockByteArray = ByteArray(10)
        val mockBitmap = mockk<Bitmap>()
        val post = mockk<PostUploadDomainModel>(relaxed = true)
        val param = mockk<UploadPostUseCase.Param>(relaxed = true)

        every { any<Bitmap>().resizeAndCrop(any(), any()) } returns mockk()
        every { any<Bitmap>().getJpegByteArray(any()) } returns mockByteArray

        every { param.post } returns post
        every { post.isPostReady } returns true
        every { post.imageUri } returns mockk()
        coEvery { postRepository.getBitmap(any()) } returns Either.Success(mockBitmap)
        coEvery { postRepository.cacheCompressedImageFile(any(), any()) } returns Either.Failure(Failure.ExternalImageDecodeFail)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(param) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.ExternalImageDecodeFail)
    }

    @Test
    fun `should return whatever postRepository uploadPost return`() {
        var result: Either<PostDomainModel, Failure>? = null

        // given
        val mockByteArray = ByteArray(10)
        val mockBitmap = mockk<Bitmap>(relaxed = true)
        val cachedFile = mockk<File>(relaxed = true)
        val post = mockk<PostUploadDomainModel>(relaxed = true)
        val param = mockk<UploadPostUseCase.Param>(relaxed = true)

        every { any<Bitmap>().resizeAndCrop(any(), any()) } returns mockk()
        every { any<Bitmap>().getJpegByteArray(any()) } returns mockByteArray

        every { param.post } returns post
        every { post.isPostReady } returns true
        every { post.imageUri } returns mockk()
        coEvery { postRepository.getBitmap(any()) } returns Either.Success(mockBitmap)
        coEvery { postRepository.cacheCompressedImageFile(any(), any()) } returns Either.Success(cachedFile)
        coEvery { postRepository.uploadPost(any()) } returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(param) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.NetworkConnection)
    }

    @Test
    fun `cachedFile should call delete no matter what postRepository uploadPost return`() {
        var result: Either<PostDomainModel, Failure>? = null

        // given
        val mockByteArray = ByteArray(10)
        val mockBitmap = mockk<Bitmap>(relaxed = true)
        val cachedFile = mockk<File>(relaxed = true)
        val post = mockk<PostUploadDomainModel>(relaxed = true)
        val param = mockk<UploadPostUseCase.Param>(relaxed = true)

        every { any<Bitmap>().resizeAndCrop(any(), any()) } returns mockk()
        every { any<Bitmap>().getJpegByteArray(any()) } returns mockByteArray

        every { param.post } returns post
        every { post.isPostReady } returns true
        every { post.imageUri } returns mockk()
        coEvery { postRepository.getBitmap(any()) } returns Either.Success(mockBitmap)
        coEvery { postRepository.cacheCompressedImageFile(any(), any()) } returns Either.Success(cachedFile)
        coEvery { postRepository.uploadPost(any()) } returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(param) {
                result = it
            }
        }

        // expect
        verify(exactly = 1) { cachedFile.delete() }
    }

}