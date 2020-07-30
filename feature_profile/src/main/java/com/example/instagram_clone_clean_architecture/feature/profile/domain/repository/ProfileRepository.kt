package com.example.instagram_clone_clean_architecture.feature.profile.domain.repository

import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel

internal interface ProfileRepository {

    suspend fun getUserProfileById(id: Int): UserDomainModel?

    suspend fun getFollowerById(id: Int): List<UserDomainModel>

    suspend fun getFollowingById(id: Int): List<UserDomainModel>

    suspend fun getPostByUserId(id: Int): List<PostDomainModel>

    suspend fun getPostByPostId(id: Int): PostDomainModel?
}