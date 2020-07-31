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
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class GetFollowingUserUseCaseTest {

    @MockK
    internal lateinit var profileRepository: ProfileRepository

    private lateinit var testUseCase: GetFollowingUserUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        testUseCase = GetFollowingUserUseCase(profileRepository)
    }

    @Test
    fun `should return correct type when invoke`() {
        val userProfiles = listOf(
            UserDomainModel(id = 1, name = "a", userName = "a", postNum = 1, followingNum = 1, followerNum = 1),
            UserDomainModel(id = 2, name = "B", userName = "b",  postNum = 1, followingNum = 1, followerNum = 1)
        )
        val param = GetFollowingUserUseCase.Param(1)
        var result: Either<List<UserDomainModel>, Failure>? = null

        coEvery { profileRepository.getFollowingById(any()) } returns Either.Success(userProfiles)

        runBlocking {
            testUseCase(param) {
                result = it
            }
        }

        result shouldBeEqualTo Either.Success(userProfiles)
    }

}