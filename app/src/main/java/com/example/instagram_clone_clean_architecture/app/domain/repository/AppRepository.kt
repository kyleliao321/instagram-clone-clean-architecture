package com.example.instagram_clone_clean_architecture.app.domain.repository

import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either

interface AppRepository {

    suspend fun getLocalLoginUserId() : Either<Int, Failure>

}