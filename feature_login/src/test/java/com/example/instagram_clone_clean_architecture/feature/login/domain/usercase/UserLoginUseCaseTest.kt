package com.example.instagram_clone_clean_architecture.feature.login.domain.usercase

import com.example.instagram_clone_clean_architecture.app.domain.model.LoginCredentialDomainModel
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
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UserLoginUseCaseTest {

    @get:Rule
    val mainCoroutineRule = com.example.library_test_utils.CoroutineTestRule()

    @MockK(relaxed = true)
    internal lateinit var loginRepository: LoginRepository

    private lateinit var testUseCase: UserLoginUseCase

    private val mockUserProfile = mockk<UserDomainModel>()

    private val mockLoginCredential = LoginCredentialDomainModel(
        jwt = "mockJwt",
        userProfile = mockUserProfile
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        testUseCase = UserLoginUseCase(loginRepository, mainCoroutineRule.testDispatcher)
    }

    @Test
    fun `should return success and trigger caching, updating when login succeed`() {
        val mockParam = mockk<UserLoginUseCase.Param>(relaxed = true)
        var result: Either<UserDomainModel, Failure>? = null

        // given
        every { runBlocking { loginRepository.userLogin(any(), any()) } } returns Either.Success(mockLoginCredential)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(mockParam) {
                result = it
            }
        }

        // expect
        verify(exactly = 1) { runBlocking { loginRepository.updateLocalLoginUserName(any()) } }
        verify(exactly = 1) { runBlocking { loginRepository.updateLocalLoginUserPassword(any()) } }
        verify(exactly = 1) { runBlocking { loginRepository.cacheLoginUserProfile(any()) } }
        verify(exactly = 1) { runBlocking { loginRepository.updateLocalAuthToken(any()) } }
        verify(exactly = 1) { runBlocking { loginRepository.userLogin(any(), any()) } }
        result shouldBeEqualTo Either.Success(mockUserProfile)
    }

    @Test
    fun `should return failure when login failed`() {
        val mockParam = mockk<UserLoginUseCase.Param>(relaxed = true)
        var result: Either<UserDomainModel, Failure>? = null

        // given
        every { runBlocking { loginRepository.userLogin(any(), any()) } } returns Either.Failure(Failure.LoginUserNameOrPasswordNotMatched)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(mockParam) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.LoginUserNameOrPasswordNotMatched)
    }

    @Test
    fun `should return failure when login failed network connection`() {
        val mockParam = mockk<UserLoginUseCase.Param>(relaxed = true)
        var result: Either<UserDomainModel, Failure>? = null

        // given
        every { runBlocking { loginRepository.userLogin(any(), any()) } } returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(mockParam) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.NetworkConnection)
    }


}