package com.example.instagram_clone_clean_architecture.feature.login.data.repository

import com.example.instagram_clone_clean_architecture.app.domain.data_source.CacheDataSource
import com.example.instagram_clone_clean_architecture.app.domain.data_source.LocalDataSource
import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.login.domain.repository.LoginRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import com.example.library_test_utils.CoroutineTestRule
import com.example.library_test_utils.runBlockingTest
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LoginRepositoryImplTest {

    @get:Rule
    val mainCoroutineRule = CoroutineTestRule()

    @MockK(relaxed = true)
    internal lateinit var remoteDataSource: RemoteDataSource

    @MockK(relaxed = true)
    internal lateinit var localDataSource: LocalDataSource

    @MockK(relaxed = true)
    internal lateinit var cacheDataSource: CacheDataSource

    private lateinit var loginRepository: LoginRepository

    private val userName = "mockUserName"
    private val password = "mockPassword"

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        loginRepository = LoginRepositoryImpl(
            localDataSource,
            cacheDataSource,
            remoteDataSource
        )
    }

    @Test
    fun `userLogin should return whatever remoteDataSource userLogin return when it return failure`() {
        // given
        var result: Either<UserDomainModel, Failure>? = null

        coEvery {
            remoteDataSource.userLogin(
                any(),
                any()
            )
        } returns Either.Failure(Failure.LoginUserNameOrPasswordNotMatched)

        // when
        mainCoroutineRule.runBlockingTest {
            result = loginRepository.userLogin(userName, password)
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.LoginUserNameOrPasswordNotMatched)
    }
}