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
import org.amshove.kluent.withFirstArg
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UpdateUserProfileUseCaseTest {

    @get:Rule
    val mainCoroutineRule = CoroutineTestRule()

    @MockK
    internal lateinit var profileRepository: ProfileRepository

    private lateinit var testUseCase: UpdateUserProfileUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        testUseCase = UpdateUserProfileUseCase(profileRepository, mainCoroutineRule.testDispatcher)
    }

    @Test
    fun `should invoke without failure`() {
        var userProfile = mockk<UserDomainModel>(relaxed = true)
        val updatedUserProfile = userProfile.copy(userName = "kyle0321")
        var result: Either<UserDomainModel, Failure>? = null

        val param = UpdateUserProfileUseCase.Param(updatedUserProfile)

        coEvery { profileRepository.updateUserProfile(any()) } answers {
            userProfile = firstArg<UserDomainModel>()
            Either.Success(userProfile)
        }

        mainCoroutineRule.runBlockingTest {
            testUseCase(param) {
                result = it
            }
        }

        result shouldBeEqualTo Either.Success(updatedUserProfile)
    }
}