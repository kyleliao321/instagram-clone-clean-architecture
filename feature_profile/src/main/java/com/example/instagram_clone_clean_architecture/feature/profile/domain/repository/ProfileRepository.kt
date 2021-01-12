package com.example.instagram_clone_clean_architecture.feature.profile.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserProfileUploadDomainModel
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import java.io.File

interface ProfileRepository {

    /**
     * operation for local account
     */
    suspend fun getLoginUserProfile(): Either<UserDomainModel?, Failure>

    suspend fun cleanUserLocalData(): Either<Unit, Failure>

    /**
     * getter for view information
     */
    suspend fun getUserProfileById(id: String): Either<UserDomainModel?, Failure>

    suspend fun getFollowerById(id: String): Either<List<UserDomainModel>, Failure>

    suspend fun getFollowingById(id: String): Either<List<UserDomainModel>, Failure>

    suspend fun getPostByUserId(id: String): Either<List<PostDomainModel>, Failure>

    suspend fun getPostByPostId(id: String): Either<PostDomainModel?, Failure>

    suspend fun getLikedUsersByPostId(id: String): Either<List<UserDomainModel>, Failure>

    suspend fun consumeUserSelectedImageUri(): Either<Uri, Failure>

    suspend fun getBitmap(uri: Uri): Either<Bitmap, Failure>

    /**
     * Data update operations
     */
    suspend fun updateUserProfile(userProfile: UserProfileUploadDomainModel): Either<UserDomainModel, Failure>

    suspend fun addUserRelation(follower: String, following: String): Either<Unit, Failure>

    suspend fun removeUserRelation(follower: String, following: String): Either<Unit, Failure>

    suspend fun addLikedPost(userId: String, postId: String): Either<Unit, Failure>

    suspend fun removeLikedPost(userId: String, postId: String): Either<Unit, Failure>

    suspend fun cacheCompressedUploadImage(
        fileName: String,
        imageByteArray: ByteArray
    ): Either<File, Failure>
}