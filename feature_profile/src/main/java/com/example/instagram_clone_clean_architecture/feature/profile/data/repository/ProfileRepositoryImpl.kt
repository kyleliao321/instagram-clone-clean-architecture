package com.example.instagram_clone_clean_architecture.feature.profile.data.repository

import android.graphics.Bitmap
import android.net.Uri
import com.example.instagram_clone_clean_architecture.app.domain.data_source.CacheDataSource
import com.example.instagram_clone_clean_architecture.app.domain.data_source.LocalDataSource
import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserProfileUploadDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import java.io.File

internal class ProfileRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val cacheDataSource: CacheDataSource
) : ProfileRepository {

    override suspend fun getLoginUserProfile(): Either<UserDomainModel?, Failure> {
        return cacheDataSource.getLoginUser()
    }

    override suspend fun getUserProfileById(id: String): Either<UserDomainModel?, Failure> {
        return remoteDataSource.getUserProfileById(id)
    }

    override suspend fun getFollowerById(id: String): Either<List<UserDomainModel>, Failure> {
        return remoteDataSource.getFollowerUsersById(id)
    }

    override suspend fun getFollowingById(id: String): Either<List<UserDomainModel>, Failure> {
        return remoteDataSource.getFollowingUsersById(id)
    }

    override suspend fun getPostByUserId(id: String): Either<List<PostDomainModel>, Failure> {
        return remoteDataSource.getPostListByUserId(id)
    }

    override suspend fun getPostByPostId(id: String): Either<PostDomainModel?, Failure> {
        return remoteDataSource.getPostByPostId(id)
    }

    override suspend fun getLikedUsersByPostId(id: String): Either<List<UserDomainModel>, Failure> {
        return remoteDataSource.getLikedUsersByPostId(id)
    }

    override suspend fun consumeUserSelectedImageUri(): Either<Uri, Failure> {
        return cacheDataSource.consumeCachedSelectedImageUri()
    }

    override suspend fun getBitmap(uri: Uri): Either<Bitmap, Failure> {
        return localDataSource.getBitmap(uri)
    }

    override suspend fun updateUserProfile(userProfile: UserProfileUploadDomainModel): Either<UserDomainModel, Failure> {
        val updateUserProfileResult = remoteDataSource.updateUserProfile(userProfile)

        if (updateUserProfileResult is Either.Success) {
            val updatedUserProfile = updateUserProfileResult.a
            val cacheResult = cacheDataSource.cacheLoginUserProfile(updatedUserProfile)

            if (cacheResult is Either.Failure) {
                return cacheResult
            }
        }

        return updateUserProfileResult
    }

    override suspend fun addUserRelation(
        follower: String,
        following: String
    ): Either<Unit, Failure> {
        return remoteDataSource.addUserRelation(follower, following)
    }

    override suspend fun removeUserRelation(
        follower: String,
        following: String
    ): Either<Unit, Failure> {
        return remoteDataSource.removeUserRelation(follower, following)
    }

    override suspend fun addLikedPost(userId: String, postId: String): Either<Unit, Failure> {
        return remoteDataSource.addUserLikePost(userId, postId)
    }

    override suspend fun removeLikedPost(userId: String, postId: String): Either<Unit, Failure> {
        return remoteDataSource.removeUserLikePost(userId, postId)
    }

    override suspend fun cacheCompressedUploadImage(
        fileName: String,
        imageByteArray: ByteArray
    ): Either<File, Failure> {
        return cacheDataSource.cacheCompressedUploadImage(fileName, imageByteArray)
    }

    override suspend fun cleanUserLocalData(): Either<Unit, Failure> {
        cacheDataSource.cacheAuthToken(null)

        val cleanUserCacheResult = cacheDataSource.cacheLoginUserProfile(null)
        if (cleanUserCacheResult is Either.Failure) {
            return cleanUserCacheResult
        }

        val cleanLocalUserNameResult = localDataSource.updateLocalLoginUserName(null)
        if (cleanLocalUserNameResult is Either.Failure) {
            return cleanLocalUserNameResult
        }

        val cleanLocalPasswordResult = localDataSource.updateLocalLoginUserPassword(null)
        if (cleanLocalPasswordResult is Either.Failure) {
            return cleanLocalPasswordResult
        }

        return Either.Success(Unit)
    }

}