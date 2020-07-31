package com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase

import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.withFirstArg
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UpdateUserProfileUseCaseTest {

    @MockK
    internal lateinit var profileRepository: ProfileRepository

    private lateinit var testUseCase: UpdateUserProfileUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        testUseCase = UpdateUserProfileUseCase(profileRepository)
    }

    @Test
    fun `should invoke without failure`() {
        var userProfile = UserDomainModel(id = 1, name = "Kyle", userName = "kyle", postNum = 0, followingNum = 0, followerNum = 0)
        val updatedUserProfile = userProfile.copy(userName = "kyle0321")
        var result: Either<UserDomainModel, Failure>? = null

        val param = UpdateUserProfileUseCase.Param(updatedUserProfile)

        coEvery { profileRepository.updateUserProfile(any()) } answers {
            userProfile = firstArg<UserDomainModel>()
            Either.Success(userProfile)
        }

        runBlocking {
            testUseCase(param) {
                result = it
            }
        }

        result shouldBeEqualTo Either.Success(updatedUserProfile)
    }
}