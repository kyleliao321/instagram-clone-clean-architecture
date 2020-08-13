package com.example.library_base.domain.utility

/**
 * Helper class to wrap asynchronize operation's result.
 *
 * @param SuccessParam Return type when it's use to indicate operation return successfully.
 * @param FailureParam Return type when it's use to indicate operation return with failure.
 */
sealed class Either<out SuccessParam, out FailureParam> {

    data class Success<out SuccessParam>(val a: SuccessParam): Either<SuccessParam, Nothing>()

    data class Failure<out FailureParam>(val b: FailureParam): Either<Nothing, FailureParam>()

    val isSuccess: Boolean get() = this is Success

    val isFailure: Boolean get() = this is Failure

    fun fold(onSucceed: (SuccessParam) -> Unit, onFail: (FailureParam) -> Unit): Any =
        when (this) {
            is Success -> onSucceed(a)
            is Failure -> onFail(b)
        }
}