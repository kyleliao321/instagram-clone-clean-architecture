package com.example.instagram_clone_clean_architecture.app.data.data_source

import android.net.Uri
import com.example.instagram_clone_clean_architecture.app.domain.data_source.CacheDataSource
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either

class CacheDataSourceImpl : CacheDataSource {

    private var userSelectedImageUri: Uri? = null

    override suspend fun cacheUserSelectedImageUri(uri: Uri): Either<Unit, Failure> {
        userSelectedImageUri =  uri
        return Either.Success(Unit)
    }

    override suspend fun consumeCachedSelectedImageUri(): Either<Uri, Failure> {
        return if (userSelectedImageUri == null) {
            Either.Failure(Failure.CacheNotFound)
        } else {
            val tmp = userSelectedImageUri
            userSelectedImageUri = null
            Either.Success(tmp!!)
        }

    }
}