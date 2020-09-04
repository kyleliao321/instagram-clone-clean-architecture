package com.example.instagram_clone_clean_architecture.feature.login.domain.usercase

import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.login.domain.repository.LoginRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_test_utils.CoroutineTestRule
import com.example.library_base.domain.utility.Either
import com.example.library_test_utils.runBlockingTest
import io.mockk.MockKAnnotations
import io.mockk.every
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
class UserRegisterUseCaseTest {

    @get:Rule
    val mainCoroutineRule = com.example.library_test_utils.CoroutineTestRule()

    @MockK
    internal lateinit var loginRepository: LoginRepository

    private lateinit var testUseCase: UserRegisterUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        testUseCase = UserRegisterUseCase(loginRepository, mainCoroutineRule.testDispatcher)
    }

    @Test
    fun `should return correct type when login succeed`() {
        val mockReturnData = mockk<UserDomainModel>()
        val mockParam = mockk<UserRegisterUseCase.Param>(relaxed = true)

        var result: Either<UserDomainModel, Failure>? = null

        // given
        every { runBlocking { loginRepository.userRegister(any(), any()) } } returns Either.Success(mockReturnData)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(mockParam) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Success(mockReturnData)
    }

    @Test
    fun `should return correct type when login fail`() {
        val mockParam = mockk<UserRegisterUseCase.Param>(relaxed = true)

        var result: Either<UserDomainModel, Failure>? = null

        // given
        every { runBlocking { loginRepository.userRegister(any(), any()) } } returns Either.Failure(
            Failure.DuplicatedUserName)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(mockParam) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.DuplicatedUserName)
    }

}