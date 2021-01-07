package com.example.instagram_clone_clean_architecture.feature.login.domain.usercase

import com.example.instagram_clone_clean_architecture.feature.login.domain.repository.LoginRepository
import com.example.library_base.domain.exception.Failure
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

    private val validUserName = "validUserName"
    private val nullUserName = null
    private val blankUserName = ""

    private val validPassword = "validPassword"

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        testUseCase = UserRegisterUseCase(loginRepository, mainCoroutineRule.testDispatcher)
    }

    @Test
    fun `should return correct type when username, password are valid and register succeed`() {
        val mockReturnData = mockk<Unit>()
        val mockParam = mockk<UserRegisterUseCase.Param>(relaxed = true)

        var result: Either<Unit, Failure>? = null

        // given
        every { mockParam.userName } returns validUserName
        every { mockParam.password } returns validPassword
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
    fun `should return correct type when username, password are valid but login fail`() {
        val mockParam = mockk<UserRegisterUseCase.Param>(relaxed = true)

        var result: Either<Unit, Failure>? = null

        // given
        every { mockParam.userName } returns validUserName
        every { mockParam.password } returns validPassword
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

    @Test
    fun `should return form data not complete failure when username or password is null`() {
        val mockParam = mockk<UserRegisterUseCase.Param>(relaxed = true)

        var result: Either<Unit, Failure>? = null

        // given
        every { mockParam.userName } returns nullUserName
        every { mockParam.password } returns validPassword

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(mockParam) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.FormDataNotComplete)
    }

    @Test
    fun `should return form data not complete failure when username or password is blank`() {
        val mockParam = mockk<UserRegisterUseCase.Param>(relaxed = true)

        var result: Either<Unit, Failure>? = null

        // given
        every { mockParam.userName } returns blankUserName
        every { mockParam.password } returns validPassword

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(mockParam) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.FormDataNotComplete)
    }

}