package com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase

import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
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
import java.util.*

@RunWith(JUnit4::class)
class GetPostUseCaseTest {

    @MockK
    internal lateinit var profileRepository: ProfileRepository

    private lateinit var testUseCase: GetPostUseCase


    @Before
    fun setup() {
        MockKAnnotations.init(this)

        testUseCase = GetPostUseCase(profileRepository)
    }

    @Test
    fun `should return correct type when invoke`() {
        val post = PostDomainModel(
            id = 1, imageSrc = "ss", description = null, location = null, date = Date(), belongUserId = 1
        )
        val param = GetPostUseCase.Param(1)
        var result: Either<PostDomainModel, Failure>? = null

        coEvery { profileRepository.getPostByPostId(any()) } returns Either.Success(post)

        runBlocking {
            testUseCase(param) {
                result = it
            }
        }

        result shouldBeEqualTo Either.Success(post)
    }
}