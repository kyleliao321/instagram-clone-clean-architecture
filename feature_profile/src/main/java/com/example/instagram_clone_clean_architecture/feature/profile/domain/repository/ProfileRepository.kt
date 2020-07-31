package com.example.instagram_clone_clean_architecture.feature.profile.domain.repository

import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either

interface ProfileRepository {

    suspend fun getUserProfileById(id: Int): Either<UserDomainModel?, Failure>

    suspend fun getFollowerById(id: Int): Either<List<UserDomainModel>, Failure>

    suspend fun getFollowingById(id: Int): Either<List<UserDomainModel>, Failure>

    suspend fun getPostByUserId(id: Int): Either<List<PostDomainModel>, Failure>

    suspend fun getPostByPostId(id: Int): Either<PostDomainModel?, Failure>

    suspend fun updateUserProfile(userProfile: UserDomainModel): Either<UserDomainModel, Failure>
}