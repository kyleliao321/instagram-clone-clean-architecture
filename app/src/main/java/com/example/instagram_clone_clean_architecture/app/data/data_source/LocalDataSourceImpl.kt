package com.example.instagram_clone_clean_architecture.app.data.data_source

import android.content.Context
import com.example.instagram_clone_clean_architecture.app.domain.data_source.LocalDataSource
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import java.lang.ref.WeakReference

class LocalDataSourceImpl : LocalDataSource {

    private lateinit var applicationContext: WeakReference<Context>

    private val SHARE_PREFERENCE_KEY = "com.example.instagram_clone_clean_architecture.shared_preference"

    private val LOCAL_LOGIN_USER_KEY = "com.example.instagram_clone_clean_architecture.shared_preference.local_login_user_id"

    override fun init(context: Context) {
        applicationContext = WeakReference(context)
    }

    override suspend fun getLocalLoginUserId(): Either<Int, Failure> {
        val context = applicationContext.get()
            ?: throw IllegalStateException("$applicationContext cannot be resolved")

        val sharePref = context.getSharedPreferences(SHARE_PREFERENCE_KEY, Context.MODE_PRIVATE)
        val localLoginUserId = sharePref.getInt(LOCAL_LOGIN_USER_KEY, -1)

        return when (localLoginUserId) {
            -1 -> Either.Failure(Failure.LocalAccountNotFound)
            else -> Either.Success(localLoginUserId)
        }
    }

    override suspend fun updateLocalLoginUserId(userId: Int?): Either<Unit, Failure> {
        val context = applicationContext.get()
            ?: throw IllegalStateException("$applicationContext cannot be resolved")

        val sharePref = context.getSharedPreferences(SHARE_PREFERENCE_KEY, Context.MODE_PRIVATE)
        sharePref.edit().apply {
            putInt(LOCAL_LOGIN_USER_KEY, userId ?: -1)
            commit()
        }

        return Either.Success(Unit)
    }

}