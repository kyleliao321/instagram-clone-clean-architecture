package com.example.instagram_clone_clean_architecture.app.data.data_source

import com.example.instagram_clone_clean_architecture.app.data.retrofit.services.AccountServices
import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import com.example.library_test_utils.CoroutineTestRule
import com.example.library_test_utils.runBlockingTest
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBe
import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response
import java.net.HttpURLConnection

@RunWith(JUnit4::class)
class RemoteDataSourceImplTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @MockK(relaxed = true)
    internal lateinit var accountServices: AccountServices

    private lateinit var remoteDataSourceImpl: RemoteDataSource

    private val mockUserName = "mockUserName"
    private val mockPassword = "mockPassword"

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        remoteDataSourceImpl = RemoteDataSourceImpl(accountServices)
    }

    @Test
    fun `should trigger registerNewAccountAsync when invoke`() {
        // given
        val mockRes = mockk<Response<Unit>>(relaxed = true)

        coEvery { accountServices.registerNewAccountAsync(any()) } returns mockRes

        // when
        coroutineRule.runBlockingTest {
            remoteDataSourceImpl.userRegister(mockUserName, mockPassword)
        }

        // expect
        coVerify(exactly = 1) { accountServices.registerNewAccountAsync(any()) }
    }

    @Test
    fun `should return success when server responded with HTTP_CREATED`() {
        var result: Either<Unit, Failure>? = null

        // given
        val mockRes = mockk<Response<Unit>>(relaxed = true)

        every { mockRes.code() } returns HttpURLConnection.HTTP_CREATED
        coEvery { accountServices.registerNewAccountAsync(any()) } returns mockRes

        // when
        coroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.userRegister(mockUserName, mockPassword)
        }

        // expect
        result shouldNotBeEqualTo null
        result shouldBeEqualTo Either.Success(Unit)
    }

    @Test
    fun `should return duplicated username failure when server responded with HTTP_CONFLICT`() {
        var result: Either<Unit, Failure>? = null

        // given
        val mockRes = mockk<Response<Unit>>(relaxed = true)

        every { mockRes.code() } returns HttpURLConnection.HTTP_CONFLICT
        coEvery { accountServices.registerNewAccountAsync(any()) } returns mockRes

        // when
        coroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.userRegister(mockUserName, mockPassword)
        }

        // expect
        result shouldNotBeEqualTo null
        result shouldBeEqualTo Either.Failure(Failure.DuplicatedUserName)
    }

    @Test
    fun `should return server error failure when server responded with other status`() {
        var result: Either<Unit, Failure>? = null

        // given
        val mockRes = mockk<Response<Unit>>(relaxed = true)

        every { mockRes.code() } returns HttpURLConnection.HTTP_BAD_REQUEST // anything but not created or conflict
        coEvery { accountServices.registerNewAccountAsync(any()) } returns mockRes

        // when
        coroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.userRegister(mockUserName, mockPassword)
        }

        // expect
        result shouldNotBeEqualTo null
        result shouldBeEqualTo Either.Failure(Failure.ServerError)
    }

}