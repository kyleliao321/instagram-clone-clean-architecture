package com.example.library_base.domain.usecase

import android.util.Log
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.usercase.UseCase
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeIn
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UseCaseTest {

    private val testUseCase = TestUseCase()

    @Test
    fun `running use case should return Either class`() {
        val param = Param(1)
        val result = runBlocking { testUseCase.run(param) }

        result shouldBeInstanceOf Either::class.java
        result shouldBeEqualTo Either.Success(ReturnType(returnResult))
    }

    @Test
    fun `should return correct data when executing use case`() {
        var result: Either<ReturnType, Failure>? = null
        val param = Param(1)

        runBlocking {
            testUseCase(param) {
                result = it
            }
        }

        result shouldBeEqualTo Either.Success(ReturnType(returnResult))
    }


    data class Param(val id: Int)
    data class ReturnType(val value: String)
    val returnResult = "TestUseCaseString"

    private inner class TestUseCase : UseCase<ReturnType, Param>() {
        override suspend fun run(params: Param): Either<ReturnType, Failure> = Either.Success(ReturnType(returnResult))
    }

}