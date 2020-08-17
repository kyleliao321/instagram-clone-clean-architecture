package com.example.instagram_clone_clean_architecture.feature.profile.data.repository

import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.delay
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

internal class MockProfileRepositoryImpl(
    private val remoteDataSource: RemoteDataSource
): ProfileRepository {

    private val loginUserId = 1

    override suspend fun getLoginUserProfile(): Either<UserDomainModel?, Failure> {
        return remoteDataSource.getUserProfileById(loginUserId)
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

    override suspend fun updateUserProfile(userProfile: UserDomainModel): Either<UserDomainModel, Failure> {
        return remoteDataSource.updateUserProfile(userProfile)
    }

    override suspend fun addUserRelation(follower: Int, following: Int): Either<Unit, Failure> {
        return remoteDataSource.addUserRelation(follower, following)
    }

    override suspend fun removeUserRelation(follower: Int, following: Int): Either<Unit, Failure> {
        return remoteDataSource.removeUserRelation(follower, following)
    }

}