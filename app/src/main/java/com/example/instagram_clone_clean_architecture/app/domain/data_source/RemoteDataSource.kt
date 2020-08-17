package com.example.instagram_clone_clean_architecture.app.domain.data_source

import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either

interface RemoteDataSource {

    suspend fun getUserProfileById(userId: Int) : Either<UserDomainModel?, Failure>

    suspend fun getUserProfileListByUserName(userName: String) : Either<List<UserDomainModel>, Failure>

    suspend fun getFollowingUsersById(userId: Int) : Either<List<UserDomainModel>, Failure>

    suspend fun getFollowerUsersById(userId: Int) : Either<List<UserDomainModel>, Failure>

    suspend fun getPostByPostId(postId: Int) : Either<PostDomainModel?, Failure>

    suspend fun getPostListByUserId(userId: Int) : Either<List<PostDomainModel>, Failure>

    suspend fun updateUserProfile(userProfile: UserDomainModel) : Either<UserDomainModel, Failure>

    suspend fun addUserRelation(followerId: Int, followingId: Int) : Either<Unit, Failure>

    suspend fun removeUserRelation(followerId: Int, followingId: Int) : Either<Unit, Failure>

}