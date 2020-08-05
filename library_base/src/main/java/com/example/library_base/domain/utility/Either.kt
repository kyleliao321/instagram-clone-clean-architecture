package com.example.library_base.domain.utility

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