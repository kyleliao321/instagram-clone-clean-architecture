package com.example.instagram_clone_clean_architecture.app.domain.data_source

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import java.io.File

interface LocalDataSource {

    fun init(context: Context)

    suspend fun getLocalLoginUserName(): Either<String, Failure>

    suspend fun getLocalLoginUserPassword(): Either<String, Failure>

    suspend fun updateLocalLoginUserName(userName: String?): Either<Unit, Failure>

    suspend fun updateLocalLoginUserPassword(password: String?): Either<Unit, Failure>

    suspend fun getBitmap(uri: Uri) : Either<Bitmap, Failure>
}