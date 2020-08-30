package com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase

import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.CoroutineTestRule
import com.example.library_base.domain.utility.Either
import com.example.library_base.domain.utility.runBlockingTest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock

@RunWith(JUnit4::class)
class GetLoginUserUseCaseTest {

    @get:Rule
    val mainCoroutineRule = CoroutineTestRule()

    @MockK(relaxed = true)
    internal lateinit var profileRepository: ProfileRepository

    private lateinit var testUseCase: GetLoginUserUseCase

    /**
     * Mock data
     */
    private val correctUserProfile = mockk<UserDomainModel>(relaxed = true)

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        testUseCase = GetLoginUserUseCase(profileRepository, mainCoroutineRule.testDispatcher)
    }

    @Test
    fun `should return success when getLoginUserProfile in profileRepository return non-null value`() {
        var result: Either<UserDomainModel, Failure>? = null

        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(correctUserProfile)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(Unit) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Success(correctUserProfile)
    }

    @Test
    fun `should return failure when getLoginUserProfile in profileRepository return null value`() {
        var result: Either<UserDomainModel, Failure>? = null

        // given
        every { runBlocking { profileRepository.getLoginUserProfile() } } returns Either.Success(null)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(Unit) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.LocalAccountNotFound)
    }

}