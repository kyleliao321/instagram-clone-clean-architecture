package com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase

import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.library_base.domain.utility.CoroutineTestRule
import com.example.library_base.domain.utility.Either
import com.example.library_base.domain.utility.runBlockingTest
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class GetUserPostUseCaseTest {

    @get:Rule
    val mainCoroutineRule = CoroutineTestRule()

    @MockK
    internal lateinit var profileRepository: ProfileRepository

    private lateinit var testUseCase: GetUserPostUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        testUseCase = GetUserPostUseCase(profileRepository, mainCoroutineRule.testDispatcher)
    }

    @Test
    fun `should return correct type when invoke`() {
        val postList = listOf<PostDomainModel>(
            PostDomainModel(id = 1, imageSrc = "ss", description = null, location = null, date = Date(), belongUserId = 1)
        )

        val param = GetUserPostUseCase.Param(1)
        var result: Either<List<PostDomainModel>, com.example.library_base.domain.exception.Failure>? = null

        coEvery { profileRepository.getPostByUserId(any()) } returns Either.Success(postList)

        mainCoroutineRule.runBlockingTest {
            testUseCase(param) {
                result = it
            }
        }

        result shouldBeEqualTo Either.Success(postList)
    }

}