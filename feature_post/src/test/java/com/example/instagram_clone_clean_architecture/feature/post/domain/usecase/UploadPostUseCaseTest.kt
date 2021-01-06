package com.example.instagram_clone_clean_architecture.feature.post.domain.usecase

import android.graphics.Bitmap
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.PostUploadDomainModel
import com.example.instagram_clone_clean_architecture.feature.post.domain.repository.PostRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import com.example.library_test_utils.runBlockingTest
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
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
        val mockBitmap = mockk<Bitmap>(relaxed = true)
        val post = mockk<PostUploadDomainModel>(relaxed = true)
        val param = mockk<UploadPostUseCase.Param>(relaxed = true)

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
        val mockBitmap = mockk<Bitmap>(relaxed = true)
        val cachedFile = mockk<File>(relaxed = true)
        val post = mockk<PostUploadDomainModel>(relaxed = true)
        val param = mockk<UploadPostUseCase.Param>(relaxed = true)

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

}