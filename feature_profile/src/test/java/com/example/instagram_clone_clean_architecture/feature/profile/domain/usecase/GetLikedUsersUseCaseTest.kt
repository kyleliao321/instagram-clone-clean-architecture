package com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase

import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
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
class GetLikedUsersUseCaseTest {

    @get:Rule
    val mainCoroutineRule = com.example.library_test_utils.CoroutineTestRule()

    @MockK(relaxed = true)
    internal lateinit var profileRepository: ProfileRepository

    private lateinit var testUseCase: GetLikedUsersUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        testUseCase = GetLikedUsersUseCase(profileRepository, mainCoroutineRule.testDispatcher)
    }

    @Test
    fun `should return correct type when profile repository return successfully`() {
        val mockResult = mockk<List<UserDomainModel>>()
        val mockParam = mockk<GetLikedUsersUseCase.Param>(relaxed = true)
        var result: Either<List<UserDomainModel>, Failure>? = null

        // given
        every { runBlocking { profileRepository.getLikedUsersByPostId(any()) } } returns Either.Success(mockResult)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(mockParam) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Success(mockResult)
    }

    @Test
    fun `should return correct type when profile repository return with failure`() {
        val mockParam = mockk<GetLikedUsersUseCase.Param>(relaxed = true)
        var result: Either<List<UserDomainModel>, Failure>? = null

        // given
        every { runBlocking { profileRepository.getLikedUsersByPostId(any()) } } returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(mockParam) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.NetworkConnection)
    }
}