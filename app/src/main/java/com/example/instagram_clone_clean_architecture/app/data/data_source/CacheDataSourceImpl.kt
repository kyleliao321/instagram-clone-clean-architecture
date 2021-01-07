package com.example.instagram_clone_clean_architecture.app.data.data_source

import android.net.Uri
import com.example.instagram_clone_clean_architecture.app.domain.data_source.CacheDataSource
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import java.io.File
import java.io.FileOutputStream

class CacheDataSourceImpl : CacheDataSource {

    private lateinit var cacheDir: File

    private var userSelectedImageUri: Uri? = null

    private var loginUser: UserDomainModel? = null

    private var authToken: String? = null

    override fun init(cacheDir: File) {
        this.cacheDir = cacheDir
    }

    override fun getAuthToken(): String? = authToken

    override fun cacheAuthToken(token: String) {
        authToken = token
    }

    override suspend fun cacheCompressedUploadImage(
        fileName: String,
        byteArray: ByteArray
    ): Either<File, Failure> {
        val cacheFile = File(cacheDir, fileName).apply { createNewFile() }

        var fos: FileOutputStream? = null

        fos = FileOutputStream(cacheFile)

        fos!!.write(byteArray)
        fos!!.flush()
        fos!!.close()

        return Either.Success(cacheFile)
    }

    override suspend fun cacheLoginUserProfile(userProfile: UserDomainModel?): Either<Unit, Failure> {
        loginUser = userProfile
        return Either.Success(Unit)
    }

    override suspend fun getLoginUser(): Either<UserDomainModel, Failure> {
        return if (loginUser == null) {
            Either.Failure(Failure.CacheNotFound)
        } else {
            Either.Success(loginUser!!)
        }
    }

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