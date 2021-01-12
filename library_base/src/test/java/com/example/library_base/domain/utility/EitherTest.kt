package com.example.library_base.domain.utility

import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBeInstanceOf
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class EitherTest {

    @Test
    fun `Either Success should return correct type`() {
        val param = "Test String"
        val result = Either.Success(param)

        result shouldBeInstanceOf Either::class.java
        result shouldNotBeInstanceOf Either.Failure::class.java
        result shouldBeInstanceOf Either.Success::class.java

        result.fold(
            // Handle Success
            { value ->
                value shouldBeInstanceOf String::class.java
                value shouldBeEqualTo param
            },
            // Handle Failure
            {}
        )
    }

    @Test
    fun `Either Failure should return correct type`() {
        val param = "Test String"
        val result = Either.Failure(param)

        result shouldBeInstanceOf Either::class.java
        result shouldBeInstanceOf Either.Failure::class.java
        result shouldNotBeInstanceOf Either.Success::class.java

        result.fold(
            // Handle Success
            {},
            // Handle Failure
            { value ->
                value shouldBeInstanceOf String::class.java
                value shouldBeEqualTo param
            }
        )
    }
}