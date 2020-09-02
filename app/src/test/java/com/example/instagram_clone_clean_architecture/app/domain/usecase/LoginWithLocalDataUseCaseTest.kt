package com.example.instagram_clone_clean_architecture.app.domain.usecase

import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.repository.AppRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.CoroutineTestRule
import com.example.library_base.domain.utility.Either
import com.example.library_base.domain.utility.runBlockingTest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LoginWithLocalDataUseCaseTest {

    @get:Rule
    val mainCoroutineRule = CoroutineTestRule()

    @MockK
    internal lateinit var appRepository: AppRepository

    private lateinit var testUseCase: LoginWithLocalDataUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        testUseCase = LoginWithLocalDataUseCase(appRepository, mainCoroutineRule.testDispatcher)
    }

    @Test
    fun `should return success when getLocalLoginUseName and getLocalLoginUserPassword return non-null value and login succeed`() {
        val mockUserName = ""
        val mockUserPassword = ""
        val mockUserProfile = mockk<UserDomainModel>()

        var result: Either<UserDomainModel, Failure>? = null

        // given
        every { runBlocking { appRepository.getLocalLoginUserName() } } returns Either.Success(mockUserName)
        every { runBlocking { appRepository.getLocalLoginUserPassword() } } returns Either.Success(mockUserPassword)
        every { runBlocking { appRepository.login(any(), any()) } } returns Either.Success(mockUserProfile)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(Unit) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Success(mockUserProfile)
    }

    @Test
    fun `should return failure when getLocalLoginUseName and getLocalLoginUserPassword return non-null value but login fail`() {
        val mockUserName = ""
        val mockUserPassword = ""

        var result: Either<UserDomainModel, Failure>? = null

        // given
        every { runBlocking { appRepository.getLocalLoginUserName() } } returns Either.Success(mockUserName)
        every { runBlocking { appRepository.getLocalLoginUserPassword() } } returns Either.Success(mockUserPassword)
        every { runBlocking { appRepository.login(any(), any()) } } returns Either.Failure(Failure.LoginUserNameOrPasswordNotMatched)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(Unit) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.LoginUserNameOrPasswordNotMatched)
    }

    @Test
    fun `should return failure when getLocalLoginUseName return null value`() {
        val mockUserName = ""
        val mockUserPassword = ""

        var result: Either<UserDomainModel, Failure>? = null

        // given
        every { runBlocking { appRepository.getLocalLoginUserName() } } returns Either.Failure(Failure.LocalAccountNotFound)
        every { runBlocking { appRepository.getLocalLoginUserPassword() } } returns Either.Success(mockUserPassword)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(Unit) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.LocalAccountNotFound)
    }

    @Test
    fun `should return failure when getLocalLoginUsePassword return null value`() {
        val mockUserName = ""

        var result: Either<UserDomainModel, Failure>? = null

        // given
        every { runBlocking { appRepository.getLocalLoginUserName() } } returns Either.Success(mockUserName)
        every { runBlocking { appRepository.getLocalLoginUserPassword() } } returns Either.Failure(Failure.LocalAccountNotFound)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(Unit) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.LocalAccountNotFound)
    }

}