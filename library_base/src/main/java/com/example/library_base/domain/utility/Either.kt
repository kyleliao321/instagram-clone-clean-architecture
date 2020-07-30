package com.example.library_base.domain.utility

sealed class Either<out SuccessParam, out FailureParam> {

    data class Success<out SuccessParam>(val a: SuccessParam): Either<SuccessParam, Nothing>()

    data class Failure<out FailureParam>(val b: FailureParam): Either<Nothing, FailureParam>()

    fun fold(onSucceed: (SuccessParam) -> Any, onFail: (FailureParam) -> Any): Any =
        when (this) {
            is Success -> onSucceed(a)
            is Failure -> onFail(b)
        }
}