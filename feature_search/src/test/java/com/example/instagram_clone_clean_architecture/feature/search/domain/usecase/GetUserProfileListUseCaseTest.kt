package com.example.instagram_clone_clean_architecture.feature.search.domain.usecase

import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.search.domain.repository.SearchRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.CoroutineTestRule
import com.example.library_base.domain.utility.Either
import com.example.library_base.domain.utility.runBlockingTest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GetUserProfileListUseCaseTest {

    @get:Rule
    val mainCoroutineRule = CoroutineTestRule()

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

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        testUseCase = GetUserProfileListUseCase(searchRepository, mainCoroutineRule.testDispatcher)
    }

    @Test
    fun `should return correct type when getUserProfileListByKeyword in searchRepository invoke successfully`() {
        var result: Either<List<UserDomainModel>, Failure>? = null

        // given
        every { runBlocking { searchRepository.getUserProfileListByKeyword(any()) } } returns Either.Success(correctUserProfileList)

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
    fun `should return correct type when getUserProfileListByKeyword in searchRepository invoke with failure`() {
        var result: Either<List<UserDomainModel>, Failure>? = null

        // given
        every { runBlocking { searchRepository.getUserProfileListByKeyword(any()) } } returns Either.Failure(Failure.NetworkConnection)

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