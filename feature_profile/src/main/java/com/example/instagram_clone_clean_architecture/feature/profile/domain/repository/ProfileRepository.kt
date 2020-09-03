package com.example.instagram_clone_clean_architecture.feature.profile.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserProfileUploadDomainModel
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either

interface ProfileRepository {

    /**
     * operation for local account
     */
    suspend fun getLoginUserProfile(): Either<UserDomainModel?, Failure>

    suspend fun cleanupCachedLoginUserData(): Either<Unit, Failure>

    suspend fun cleanupLocalLoginUserName(): Either<Unit, Failure>

    suspend fun cleanupLocalLoginUserPassword(): Either<Unit, Failure>

    /**
     * getter for view information
     */
    suspend fun getUserProfileById(id: Int): Either<UserDomainModel?, Failure>

    suspend fun getFollowerById(id: Int): Either<List<UserDomainModel>, Failure>

    suspend fun getFollowingById(id: Int): Either<List<UserDomainModel>, Failure>

    suspend fun getPostByUserId(id: Int): Either<List<PostDomainModel>, Failure>

    suspend fun getPostByPostId(id: Int): Either<PostDomainModel?, Failure>

    suspend fun getLikedUsersByPostId(id: Int): Either<List<UserDomainModel>, Failure>

    suspend fun consumeUserSelectedImageUri(): Either<Uri, Failure>

    suspend fun getBitmap(uri: Uri): Either<Bitmap, Failure>

    /**
     * Data update operations
     */
    suspend fun updateUserProfile(userProfile: UserProfileUploadDomainModel): Either<UserDomainModel, Failure>

    suspend fun addUserRelation(follower: Int, following: Int): Either<Unit, Failure>

    suspend fun removeUserRelation(follower: Int, following: Int): Either<Unit, Failure>

    suspend fun addLikedPost(userId: Int, postId: Int): Either<Unit, Failure>

    suspend fun removeLikedPost(userId: Int, postId: Int): Either<Unit, Failure>
}