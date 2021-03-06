package com.example.instagram_clone_clean_architecture.feature.search.domain.usecase

import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.search.domain.repository.SearchRepository
import com.example.library_base.domain.exception.Failure
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
class GetUserProfileListUseCaseTest {

    @get:Rule
    val mainCoroutineRule = com.example.library_test_utils.CoroutineTestRule()

    @MockK(relaxed = true)
    internal lateinit var searchRepository: SearchRepository

    private lateinit var testUseCase: GetUserProfileListUseCase

    /**
     * Mock data
     */
    @MockK(relaxed = true)
    internal lateinit var mockParam: GetUserProfileListUseCase.Param

    @MockK(relaxed = true)
    internal lateinit var correctUserProfileList: List<UserDomainModel>

    private val validKeyword = "validKeyword"
    private val nullKeyword = null
    private val blankKeyword = ""

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        testUseCase = GetUserProfileListUseCase(searchRepository, mainCoroutineRule.testDispatcher)
    }

    @Test
    fun `should return correct type when keyword is valid and getUserProfileListByKeyword in searchRepository invoke successfully`() {
        var result: Either<List<UserDomainModel>, Failure>? = null

        // given
        every { mockParam.keyword } returns validKeyword
        every { runBlocking { searchRepository.getUserProfileListByKeyword(any()) } } returns Either.Success(
            correctUserProfileList
        )

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(mockParam) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Success(correctUserProfileList)
    }

    @Test
    fun `should return correct type when keyword is valid but getUserProfileListByKeyword in searchRepository invoke with failure`() {
        var result: Either<List<UserDomainModel>, Failure>? = null

        // given
        every { mockParam.keyword } returns validKeyword
        every { runBlocking { searchRepository.getUserProfileListByKeyword(any()) } } returns Either.Failure(
            Failure.NetworkConnection
        )

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(mockParam) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.NetworkConnection)
    }

    @Test
    fun `should return form data not complete failure when keyword is null`() {
        var result: Either<List<UserDomainModel>, Failure>? = null

        // given
        every { mockParam.keyword } returns nullKeyword

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
    fun `should return form data not complete failure when keyword is blank`() {
        var result: Either<List<UserDomainModel>, Failure>? = null

        // given
        every { mockParam.keyword } returns blankKeyword

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