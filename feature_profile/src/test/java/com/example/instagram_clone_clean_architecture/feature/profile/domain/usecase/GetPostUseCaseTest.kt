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
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Before
import org.junit.Test
import org.junit.internal.runners.statements.Fail
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
    fun `should return Success when repository return non-null value`() {
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

    @Test
    fun `should return Failure when repository return null value`() {
        val param = GetPostUseCase.Param(1)
        var result: Either<PostDomainModel, Failure>? = null

        coEvery { profileRepository.getPostByPostId(any()) } returns Either.Success(null)

        runBlocking {
            testUseCase(param) {
                result = it
            }
        }

        result shouldBeInstanceOf Either.Failure::class.java
        result shouldBeEqualTo Either.Failure(Failure.NullValue)
    }
}