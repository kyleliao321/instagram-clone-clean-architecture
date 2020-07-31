package com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase

import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GetUserProfileUseCaseTest {

    @MockK
    internal lateinit var profileRepository: ProfileRepository

    private lateinit var testUseCase: GetUserProfileUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        testUseCase = GetUserProfileUseCase(profileRepository)
    }

    @Test
    fun `should return Success when repository return non-null value`() {
        val userProfile = UserDomainModel(id = 1, name = "Kyle", userName = "kyle", description = "any", postNum = 0, followerNum = 0, followingNum = 0)
        val param = GetUserProfileUseCase.Param(1)
        var result: Either<UserDomainModel, Failure>? = null

        coEvery { profileRepository.getUserProfileById(any()) } returns Either.Success(userProfile)

        runBlocking {
            testUseCase(param) {
                result = it
            }
        }

        result shouldBeEqualTo Either.Success(userProfile)
    }

    @Test
    fun `should return Failure when repository return null value`() {
        val param = GetUserProfileUseCase.Param(1)
        var result: Either<UserDomainModel, Failure>? = null

        coEvery { profileRepository.getUserProfileById(any()) } returns Either.Success(null)

        runBlocking {
            testUseCase(param) {
                result = it
            }
        }

        result shouldBeInstanceOf Either.Failure::class.java
        result shouldBeEqualTo Either.Failure(Failure.NullValue)
    }

}