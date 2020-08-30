package com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase

import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.CoroutineTestRule
import com.example.library_base.domain.utility.Either
import com.example.library_base.domain.utility.runBlockingTest
import io.mockk.MockKAnnotations
import io.mockk.coEvery
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
class GetFollowingUserUseCaseTest {

    @get:Rule
    val mainCoroutineRule = CoroutineTestRule()

    @MockK
    internal lateinit var profileRepository: ProfileRepository

    private lateinit var testUseCase: GetFollowingUserUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        testUseCase = GetFollowingUserUseCase(profileRepository, mainCoroutineRule.testDispatcher)
    }

    @Test
    fun `should return correct type when invoke`() {
        val userProfiles = mockk<List<UserDomainModel>>()
        val param = GetFollowingUserUseCase.Param(1)
        var result: Either<List<UserDomainModel>, Failure>? = null

        coEvery { profileRepository.getFollowingById(any()) } returns Either.Success(userProfiles)

        mainCoroutineRule.runBlockingTest {
            testUseCase(param) {
                result = it
            }
        }

        result shouldBeEqualTo Either.Success(userProfiles)
    }

}