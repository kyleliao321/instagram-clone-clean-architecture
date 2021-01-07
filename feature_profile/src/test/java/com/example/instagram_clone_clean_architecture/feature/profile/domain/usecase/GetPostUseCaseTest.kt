package com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase

import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import com.example.library_test_utils.runBlockingTest
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GetPostUseCaseTest {

    @get:Rule
    val mainCoroutineRule = com.example.library_test_utils.CoroutineTestRule()

    @MockK
    internal lateinit var profileRepository: ProfileRepository

    private lateinit var testUseCase: GetPostUseCase


    @Before
    fun setup() {
        MockKAnnotations.init(this)

        testUseCase = GetPostUseCase(profileRepository, mainCoroutineRule.testDispatcher)
    }

    @Test
    fun `should return Success when repository return non-null value`() {
        val post = mockk<PostDomainModel>(relaxed = true)
        val param = GetPostUseCase.Param("mockId")
        var result: Either<PostDomainModel, Failure>? = null

        coEvery { profileRepository.getPostByPostId(any()) } returns Either.Success(post)

        mainCoroutineRule.runBlockingTest {
            testUseCase(param) {
                result = it
            }
        }

        result shouldBeEqualTo Either.Success(post)
    }

    @Test
    fun `should return Failure when repository return null value`() {
        val param = GetPostUseCase.Param("mockId")
        var result: Either<PostDomainModel, Failure>? = null

        coEvery { profileRepository.getPostByPostId(any()) } returns Either.Success(null)

        mainCoroutineRule.runBlockingTest {
            testUseCase(param) {
                result = it
            }
        }

        result shouldBeInstanceOf Either.Failure::class.java
        result shouldBeEqualTo Either.Failure(Failure.ServerError)
    }
    @Test

    fun `should return Failure when repository return failure`() {
        val param = GetPostUseCase.Param("mockId")
        var result: Either<PostDomainModel, Failure>? = null

        coEvery { profileRepository.getPostByPostId(any()) } returns Either.Failure(Failure.NetworkConnection)

        mainCoroutineRule.runBlockingTest {
            testUseCase(param) {
                result = it
            }
        }

        result shouldBeInstanceOf Either.Failure::class.java
        result shouldBeEqualTo Either.Failure(Failure.NetworkConnection)
    }
}