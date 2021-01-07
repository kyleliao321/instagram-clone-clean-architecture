package com.example.instagram_clone_clean_architecture.feature.login.domain.usercase

import com.example.instagram_clone_clean_architecture.app.domain.model.LoginCredentialDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.login.domain.repository.LoginRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import com.example.library_test_utils.runBlockingTest
import io.mockk.*
import io.mockk.impl.annotations.MockK
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

    private val validUserName = "validUserName"
    private val nullUserName = null
    private val blankUserName = ""

    private val validPassword = "validPassword"

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        testUseCase = UserLoginUseCase(loginRepository, mainCoroutineRule.testDispatcher)
    }

    @Test
    fun `should return whatever loginRepository userLogin return when userName and password are valid`() {
        val mockParam = mockk<UserLoginUseCase.Param>(relaxed = true)
        val userProfile = mockk<UserDomainModel>(relaxed = true)
        var result: Either<UserDomainModel, Failure>? = null

        // given
        every { mockParam.userName } returns validUserName
        every { mockParam.password } returns validPassword

        coEvery { loginRepository.userLogin(any(), any()) } returns Either.Success(userProfile)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(mockParam) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Success(userProfile)
    }

    @Test
    fun `should return form data not complete failure when username or password is null`() {
        val mockParam = mockk<UserLoginUseCase.Param>(relaxed = true)
        var result: Either<UserDomainModel, Failure>? = null

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
        val mockParam = mockk<UserLoginUseCase.Param>(relaxed = true)
        var result: Either<UserDomainModel, Failure>? = null

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