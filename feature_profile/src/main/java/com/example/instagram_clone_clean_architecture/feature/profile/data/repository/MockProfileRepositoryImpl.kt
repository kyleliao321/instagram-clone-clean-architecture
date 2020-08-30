package com.example.instagram_clone_clean_architecture.feature.profile.data.repository

import com.example.instagram_clone_clean_architecture.app.domain.data_source.LocalDataSource
import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either

internal class MockProfileRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
): ProfileRepository {
    
    override suspend fun getLoginUserProfile(): Either<UserDomainModel?, Failure> {
        var result: Either<UserDomainModel?, Failure>? = null

        var userId: Int? = null

        localDataSource.getLocalLoginUserId().fold(
            onSucceed = { id -> userId = id},
            onFail = { failure -> result = Either.Failure(failure) }
        )

        userId?.let {
            remoteDataSource.getUserProfileById(it).fold(
                onSucceed = { userProfile -> result = Either.Success(userProfile) },
                onFail = { failure -> result = Either.Failure(failure) }
            )
        }

        return result!!
    }

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

    override suspend fun updateUserProfile(userProfile: UserDomainModel): Either<UserDomainModel, Failure> {
        return remoteDataSource.updateUserProfile(userProfile)
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