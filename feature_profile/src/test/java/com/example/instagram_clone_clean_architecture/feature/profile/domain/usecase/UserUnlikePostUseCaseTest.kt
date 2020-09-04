package com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase

import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
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
class UserUnlikePostUseCaseTest {

    @get:Rule
    val mainCoroutineRule = com.example.library_test_utils.CoroutineTestRule()

    @MockK(relaxed = true)
    internal lateinit var profileRepository: ProfileRepository

    private lateinit var testUseCase: UserUnlikePostUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        testUseCase = UserUnlikePostUseCase(profileRepository, mainCoroutineRule.testDispatcher)
    }

    @Test
    fun `should return correct type when addLikedPost in profileRepository returns successfully`() {
        var result: Either<Unit, Failure>? = null
        val mockParam = mockk<UserUnlikePostUseCase.Param>(relaxed = true)

        // given
        every { runBlocking { profileRepository.removeLikedPost(any(), any()) } } returns Either.Success(Unit)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(mockParam) { result = it}
        }

        // expect
        result shouldBeEqualTo Either.Success(Unit)
    }

    @Test
    fun `should return correct type when addLikedPost in profileRepository returns with failure`() {
        var result: Either<Unit, Failure>? = null
        val mockParam = mockk<UserUnlikePostUseCase.Param>(relaxed = true)

        // given
        every { runBlocking { profileRepository.removeLikedPost(any(), any()) } } returns Either.Failure(
            Failure.ServerError)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(mockParam) { result = it}
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.ServerError)
    }

}