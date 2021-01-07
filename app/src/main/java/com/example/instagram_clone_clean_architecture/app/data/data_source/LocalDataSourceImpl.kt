package com.example.instagram_clone_clean_architecture.app.data.data_source

import android.content.ContentResolver
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.instagram_clone_clean_architecture.app.domain.data_source.LocalDataSource
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import java.lang.ref.SoftReference

class LocalDataSourceImpl: LocalDataSource {

    private lateinit var sharedPrefsRefer: SoftReference<SharedPreferences>

    private lateinit var contentResolverRefer: SoftReference<ContentResolver>

    private val LOCAL_LOGIN_USER_NAME_KEY = "com.example.instagram_clone_clean_architecture.shared_preference.local_login_user_name"

    private val LOCAL_LOGIN_USER_PASSWORD_KEY = "com.example.instagram_clone_clean_architecture.shared_preference.local_login_user_password"

    private val LOCAL_TOKEN_KEY = "com.example.instagram_clone_clean_architecture.shared_preference.local_token"

    private val ERROR_STRING = ""

    override fun init(contentResolver: ContentResolver) {
        contentResolverRefer = SoftReference(contentResolver)
    }

    override fun init(encryptedSharedPrefs: SharedPreferences) {
        sharedPrefsRefer = SoftReference(encryptedSharedPrefs)
    }

    override suspend fun getLocalLoginUserName(): Either<String, Failure> {
        val sharedPrefs = sharedPrefsRefer.get()
            ?: throw IllegalStateException("Cannot get EncryptedSharedPreferences object")

        val localLoginUserName = sharedPrefs.getString(LOCAL_LOGIN_USER_NAME_KEY, ERROR_STRING)

        return if (localLoginUserName == ERROR_STRING) {
            Either.Failure(Failure.LocalAccountNotFound)
        } else {
            Either.Success(localLoginUserName!!)
        }
    }

    override suspend fun getLocalLoginUserPassword(): Either<String, Failure> {
        val sharedPrefs = sharedPrefsRefer.get()
            ?: throw IllegalStateException("Cannot get EncryptedSharedPreferences object")

        val localLoginUserPassword = sharedPrefs.getString(LOCAL_LOGIN_USER_PASSWORD_KEY, ERROR_STRING)

        return if (localLoginUserPassword == ERROR_STRING) {
            Either.Failure(Failure.LocalAccountNotFound)
        } else {
            Either.Success(localLoginUserPassword!!)
        }
    }

    override suspend fun getAuthorizedToken(): Either<String, Failure> {
        val sharedPrefs = sharedPrefsRefer.get()
            ?: throw IllegalStateException("Cannot get EncryptedSharedPreferences object")

        val localToken = sharedPrefs.getString(LOCAL_TOKEN_KEY, ERROR_STRING)

        return if (localToken == ERROR_STRING) {
            Either.Failure(Failure.LocalAccountNotFound)
        } else {
            Either.Success(localToken!!)
        }
    }

    override suspend fun updateLocalLoginUserName(userName: String?): Either<Unit, Failure> {
        val sharedPrefs = sharedPrefsRefer.get()
            ?: throw IllegalStateException("Cannot get EncryptedSharedPreferences object")

        sharedPrefs.edit().apply {
            putString(LOCAL_LOGIN_USER_NAME_KEY, userName ?: ERROR_STRING)
            commit()
        }

        return Either.Success(Unit)
    }

    override suspend fun updateLocalLoginUserPassword(password: String?): Either<Unit, Failure> {
        val sharedPrefs = sharedPrefsRefer.get()
            ?: throw IllegalStateException("Cannot get EncryptedSharedPreferences object")

        sharedPrefs.edit().apply {
            putString(LOCAL_LOGIN_USER_PASSWORD_KEY, password ?: ERROR_STRING)
            commit()
        }

        return Either.Success(Unit)
    }

    override suspend fun updateAuthorizedToken(token: String?): Either<Unit, Failure> {
        val sharedPrefs = sharedPrefsRefer.get()
            ?: throw IllegalStateException("Cannot get EncryptedSharedPreferences object")

        sharedPrefs.edit().apply {
            putString(LOCAL_TOKEN_KEY, token ?: ERROR_STRING)
            commit()
        }

        return Either.Success(Unit)
    }

    override suspend fun getBitmap(uri: Uri): Either<Bitmap, Failure> {
        val contentResolver = contentResolverRefer.get()
            ?: throw IllegalStateException("Cannot get ContentProvider object")

        val bitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        } else {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }

        return if (bitmap == null) {
            Either.Failure(Failure.ExternalImageDecodeFail)
        } else {
            Either.Success(bitmap)
        }
    }

}