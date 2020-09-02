package com.example.instagram_clone_clean_architecture.feature.profile.data.repository

import android.graphics.Bitmap
import android.net.Uri
import com.example.instagram_clone_clean_architecture.app.data.model.UserProfileUploadDataModel
import com.example.instagram_clone_clean_architecture.app.domain.data_source.CacheDataSource
import com.example.instagram_clone_clean_architecture.app.domain.data_source.LocalDataSource
import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserProfileUploadDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either

internal class ProfileRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val cacheDataSource: CacheDataSource
): ProfileRepository {
    
    override suspend fun getLoginUserProfile(): Either<UserDomainModel?, Failure> {
        return cacheDataSource.getLoginUser()
    }

    override suspend fun cleanupLocalLoginUser(): Either<Unit, Failure> =
        cacheDataSource.cacheLoginUserProfile(null)

    override suspend fun getUserProfileById(id: Int): Either<UserDomainModel?, Failure> {
        return remoteDataSource.getUserProfileById(id)
    }

    override suspend fun getFollowerById(id: Int): Either<List<UserDomainModel>, Failure> {
        return remoteDataSource.getFollowerUsersById(id)
    }

    override suspend fun getFollowingById(id: Int): Either<List<UserDomainModel>, Failure> {
        return remoteDataSource.getFollowingUsersById(id)
    }

    override suspend fun getPostByUserId(id: Int): Either<List<PostDomainModel>, Failure> {
        return remoteDataSource.getPostListByUserId(id)
    }

    override suspend fun getPostByPostId(id: Int): Either<PostDomainModel?, Failure> {
        return remoteDataSource.getPostByPostId(id)
    }

    override suspend fun getLikedUsersByPostId(id: Int): Either<List<UserDomainModel>, Failure> {
        return remoteDataSource.getLikedUsersByPostId(id)
    }

    override suspend fun consumeUserSelectedImageUri(): Either<Uri, Failure> {
        return cacheDataSource.consumeCachedSelectedImageUri()
    }

    override suspend fun getBitmap(uri: Uri): Either<Bitmap, Failure> {
        return localDataSource.getBitmap(uri)
    }

    override suspend fun updateUserProfile(userProfile: UserProfileUploadDomainModel): Either<UserDomainModel, Failure> {
        val uri = userProfile.image

        val newProfile = if (uri == null) {
            UserProfileUploadDataModel.from(userProfile, null)
        } else {
            var bitmap: Bitmap? = null
            localDataSource.getBitmap(uri).fold(
                onSucceed = { bitmap = it },
                onFail = { bitmap = null }
            )
            UserProfileUploadDataModel.from(userProfile, bitmap)
        }

        return remoteDataSource.updateUserProfile(newProfile)
    }

    override suspend fun addUserRelation(follower: Int, following: Int): Either<Unit, Failure> {
        return remoteDataSource.addUserRelation(follower, following)
    }

    override suspend fun removeUserRelation(follower: Int, following: Int): Either<Unit, Failure> {
        return remoteDataSource.removeUserRelation(follower, following)
    }

    override suspend fun addLikedPost(userId: Int, postId: Int): Either<Unit, Failure> {
        return remoteDataSource.addUserLikePost(userId, postId)
    }

    override suspend fun removeLikedPost(userId: Int, postId: Int): Either<Unit, Failure> {
        return remoteDataSource.removeUserLikePost(userId, postId)
    }

}