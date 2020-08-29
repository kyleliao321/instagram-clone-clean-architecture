package com.example.instagram_clone_clean_architecture.app.domain.data_source

import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either

interface LocalDataSource {

    suspend fun getLocalLoginUserId(): Either<Int, Failure>

    suspend fun updateLocalLoginUserId(userId: Int?): Either<Unit, Failure>

}