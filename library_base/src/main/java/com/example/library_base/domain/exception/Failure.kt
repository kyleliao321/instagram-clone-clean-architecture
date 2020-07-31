package com.example.library_base.domain.exception

sealed class Failure {
    object NetworkConnection: Failure()
    object NullValue: Failure()
    object ServerError: Failure()
}