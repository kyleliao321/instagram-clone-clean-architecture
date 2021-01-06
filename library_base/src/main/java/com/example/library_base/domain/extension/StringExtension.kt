package com.example.library_base.domain.extension

fun String.toBearAuthToken(): String {
    return "Bearer $this"
}