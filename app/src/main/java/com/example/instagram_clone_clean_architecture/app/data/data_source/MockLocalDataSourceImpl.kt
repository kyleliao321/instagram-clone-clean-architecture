package com.example.instagram_clone_clean_architecture.app.data.data_source

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.example.instagram_clone_clean_architecture.app.domain.data_source.LocalDataSource
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import java.io.File

class MockLocalDataSourceImpl: LocalDataSource {

    private var localLoginUserId: Int? = null

    override fun init(context: Context) {}

    override suspend fun getLocalLoginUserId(): Either<Int, Failure> {
        return when (localLoginUserId) {
            null -> Either.Failure(Failure.LocalAccountNotFound)
            else -> Either.Success(localLoginUserId!!)
        }
    }

    override suspend fun updateLocalLoginUserId(userId: Int?): Either<Unit, Failure> {
        localLoginUserId = userId
        return Either.Success(Unit)
    }

    override suspend fun cacheImage(image: Uri?): Either<Unit, Failure> {
        TODO("Not yet implemented")
    }

    override suspend fun consumeLoadedImage(): Either<Uri, Failure> {
        TODO("Not yet implemented")
    }

}