package com.example.instagram_clone_clean_architecture.app.data.data_source

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.example.instagram_clone_clean_architecture.app.domain.data_source.LocalDataSource
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import java.io.File
import java.lang.ref.WeakReference

class LocalDataSourceImpl : LocalDataSource {

    private lateinit var applicationContext: WeakReference<Context>

    private var cacheImageUri: Uri? = null

    private val SHARE_PREFERENCE_KEY = "com.example.instagram_clone_clean_architecture.shared_preference"

    private val LOCAL_LOGIN_USER_KEY = "com.example.instagram_clone_clean_architecture.shared_preference.local_login_user_id"

    private val ERROR_KEY = -1

    override fun init(context: Context) {
        applicationContext = WeakReference(context)
    }

    override suspend fun getLocalLoginUserId(): Either<Int, Failure> {
        val context = applicationContext.get()
            ?: throw IllegalStateException("$applicationContext cannot be resolved")

        val sharePref = context.getSharedPreferences(SHARE_PREFERENCE_KEY, Context.MODE_PRIVATE)
        val localLoginUserId = sharePref.getInt(LOCAL_LOGIN_USER_KEY, ERROR_KEY)

        return when (localLoginUserId) {
            ERROR_KEY -> Either.Failure(Failure.LocalAccountNotFound)
            else -> Either.Success(localLoginUserId)
        }
    }

    override suspend fun updateLocalLoginUserId(userId: Int?): Either<Unit, Failure> {
        val context = applicationContext.get()
            ?: throw IllegalStateException("$applicationContext cannot be resolved")

        val sharePref = context.getSharedPreferences(SHARE_PREFERENCE_KEY, Context.MODE_PRIVATE)
        sharePref.edit().apply {
            putInt(LOCAL_LOGIN_USER_KEY, userId ?: ERROR_KEY)
            commit()
        }

        return Either.Success(Unit)
    }

    override suspend fun getBitmap(uri: Uri): Either<Bitmap, Failure> {
        val context = applicationContext.get()
            ?: throw IllegalStateException("$applicationContext cannot be resolved")

        val bitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }

        return if (bitmap == null) {
            Either.Failure(Failure.PhotoGalleryServiceFail)
        } else {
            Either.Success(bitmap)
        }
    }

    override suspend fun cacheImage(imageUri: Uri?): Either<Unit, Failure> {
        cacheImageUri = imageUri
        return Either.Success(Unit)
    }

    override suspend fun consumeLoadedImage(): Either<Uri?, Failure> {
        val tmp = cacheImageUri
        cacheImageUri = null
        return Either.Success(tmp)
    }

}