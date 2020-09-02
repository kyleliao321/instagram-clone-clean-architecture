package com.example.instagram_clone_clean_architecture.app.domain.repository

import android.net.Uri
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either

interface AppRepository {

    suspend fun cacheUserSelectedImage(imageUri: Uri) : Either<Unit, Failure>

    suspend fun cacheLoginUser(userProfile: UserDomainModel) : Either<Unit, Failure>

    suspend fun getLoginUser() : Either<UserDomainModel, Failure>

}