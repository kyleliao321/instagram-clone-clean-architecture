package com.example.instagram_clone_clean_architecture.app.domain.data_source

import android.content.ContentResolver
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either

interface LocalDataSource {

    fun init(contentResolver: ContentResolver)

    fun init(encryptedSharedPrefs: SharedPreferences)

    suspend fun getLocalLoginUserName(): Either<String, Failure>

    suspend fun getLocalLoginUserPassword(): Either<String, Failure>

    suspend fun getAuthorizedToken(): Either<String, Failure>

    suspend fun updateLocalLoginUserName(userName: String?): Either<Unit, Failure>

    suspend fun updateLocalLoginUserPassword(password: String?): Either<Unit, Failure>

    suspend fun updateAuthorizedToken(token: String?): Either<Unit, Failure>

    suspend fun getBitmap(uri: Uri): Either<Bitmap, Failure>
}