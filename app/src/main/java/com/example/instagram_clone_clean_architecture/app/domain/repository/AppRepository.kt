package com.example.instagram_clone_clean_architecture.app.domain.repository

import android.net.Uri
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either

interface AppRepository {

    suspend fun cacheUserSelectedImage(imageUri: Uri) : Either<Unit, Failure>

    suspend fun getLocalLoginUserId() : Either<Int, Failure>

    suspend fun getLocalLoginUserName() : Either<String, Failure>

    suspend fun getLocalLoginUserPassword() : Either<String, Failure>

}