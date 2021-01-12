package com.example.instagram_clone_clean_architecture.feature.post.domain.usecase

import android.net.Uri
import com.example.instagram_clone_clean_architecture.feature.post.domain.repository.PostRepository
import com.example.library_base.domain.exception.Failure
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
class GetUserSelectedImageUseCaseTest {

    @get:Rule
    val mainCoroutineRule = com.example.library_test_utils.CoroutineTestRule()

    @MockK(relaxed = true)
    internal lateinit var postRepository: PostRepository

    private lateinit var testUseCase: GetUserSelectedImageUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        testUseCase = GetUserSelectedImageUseCase(postRepository, mainCoroutineRule.testDispatcher)
    }

    @Test
    fun `should return correct type when getUserSelectedImage of postRepository invoke successfully`() {
        var result: Either<Uri?, Failure>? = null
        val mockFile = mockk<Uri>()

        // given
        every { runBlocking { postRepository.getUserSelectedImage() } } returns Either.Success(
            mockFile
        )

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(Unit) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Success(mockFile)
    }

}