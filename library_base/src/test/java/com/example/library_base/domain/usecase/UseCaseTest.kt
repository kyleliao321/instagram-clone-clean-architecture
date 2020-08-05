package com.example.library_base.domain.usecase

import android.util.Log
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.usercase.UseCase
import com.example.library_base.domain.utility.CoroutineTestRule
import com.example.library_base.domain.utility.Either
import com.example.library_base.domain.utility.runBlockingTest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeIn
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UseCaseTest {

    @get:Rule
    var mainCoroutineRule = CoroutineTestRule()

    private val testUseCase = TestUseCase(mainCoroutineRule.testDispatcher)

    @Test
    fun `running use case should return Either class`() {
        val param = Param(1)
        var result: Either<ReturnType, Failure>? = null

        mainCoroutineRule.runBlockingTest { result = testUseCase.run(param) }

        result shouldBeInstanceOf Either::class.java
        result shouldBeEqualTo Either.Success(ReturnType(returnResult))
    }

    @Test
    fun `should return correct data when executing use case`() {
        var result: Either<ReturnType, Failure>? = null
        val param = Param(1)

        mainCoroutineRule.runBlockingTest {
            testUseCase(param) {
                result = it
            }
        }

        result shouldBeEqualTo Either.Success(ReturnType(returnResult))
    }


    data class Param(val id: Int)
    data class ReturnType(val value: String)
    val returnResult = "TestUseCaseString"

    private inner class TestUseCase(
        defaultDispatcher: CoroutineDispatcher
    ) : UseCase<ReturnType, Param>(defaultDispatcher) {
        override suspend fun run(params: Param): Either<ReturnType, Failure> = Either.Success(ReturnType(returnResult))
    }

}