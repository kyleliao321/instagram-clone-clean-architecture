package com.example.instagram_clone_clean_architecture.feature.post.domain.usecase

import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.PostUploadDomainModel
import com.example.instagram_clone_clean_architecture.feature.post.domain.repository.PostRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_test_utils.CoroutineTestRule
import com.example.library_base.domain.utility.Either
import com.example.library_test_utils.runBlockingTest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

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
    fun `should return correct type when post complete and upload success`() {
        var result: Either<PostDomainModel, Failure>? = null
        val mockPost = mockk<PostUploadDomainModel>(relaxed = true)
        val mockReturnPost = mockk<PostDomainModel>()

        // given
        every { mockPost.isPostReady } returns true
        every { runBlocking {  postRepository.uploadPostUseCase(any()) } } returns Either.Success(mockReturnPost)

        // when
        mainCoroutineRule.runBlockingTest {
            val param = UploadPostUseCase.Param(mockPost)
            testUseCase(param) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Success(mockReturnPost)
    }

    @Test
    fun `should return failure type when post is not complete`() {
        var result: Either<PostDomainModel, Failure>? = null
        val mockPost = mockk<PostUploadDomainModel>()

        // given
        every { mockPost.isPostReady } returns false

        // when
        mainCoroutineRule.runBlockingTest {
            val param = UploadPostUseCase.Param(mockPost)
            testUseCase(param) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.PostNotComplete)
    }

    @Test
    fun `should return failure type when post complete but upload fail`() {
        var result: Either<PostDomainModel, Failure>? = null
        val mockPost = mockk<PostUploadDomainModel>(relaxed = true)

        // given
        every { mockPost.isPostReady } returns true
        every { runBlocking {  postRepository.uploadPostUseCase(any()) } } returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest {
            val param = UploadPostUseCase.Param(mockPost)
            testUseCase(param) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.NetworkConnection)
    }

}