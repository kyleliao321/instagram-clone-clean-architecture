package com.example.library_base.domain.exception

sealed class Failure {
    object NetworkConnection: Failure()
}