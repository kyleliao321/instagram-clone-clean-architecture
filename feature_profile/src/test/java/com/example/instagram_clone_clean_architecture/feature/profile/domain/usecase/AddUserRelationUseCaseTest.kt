package com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase

import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_test_utils.CoroutineTestRule
import com.example.library_base.domain.utility.Either
import com.example.library_test_utils.runBlockingTest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AddUserRelationUseCaseTest {

    @get:Rule
    val mainCoroutineRule = com.example.library_test_utils.CoroutineTestRule()

    @MockK(relaxed = true)
    internal lateinit var profileRepository: ProfileRepository

    @MockK(relaxed = true)
    internal lateinit var mockParams: AddUserRelationUseCase.Param

    private lateinit var testUseCase: AddUserRelationUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        testUseCase = AddUserRelationUseCase(profileRepository, mainCoroutineRule.testDispatcher)
    }

    @Test
    fun `should return correct type when addUserRelation in profileRepository invoke successfully`() {
        var result: Either<Unit, Failure>? = null

        // given
        every { runBlocking { profileRepository.addUserRelation(any(), any()) } } returns Either.Success(Unit)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(mockParams) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Success(Unit)
    }

    @Test
    fun `should return correct type when addUserRelation in profileRepository invoke with failure`() {
        var result: Either<Unit, Failure>? = null

        // given
        every { runBlocking { profileRepository.addUserRelation(any(), any()) } } returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(mockParams) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.NetworkConnection)
    }

}