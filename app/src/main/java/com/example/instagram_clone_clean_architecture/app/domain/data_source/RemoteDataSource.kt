package com.example.instagram_clone_clean_architecture.app.domain.data_source

import com.example.instagram_clone_clean_architecture.app.data.model.PostUploadDataModel
import com.example.instagram_clone_clean_architecture.app.data.model.UserProfileUploadDataModel
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.PostUploadDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserProfileUploadDomainModel
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either

interface RemoteDataSource {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////// Authentication operations ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    suspend fun userLogin(userName: String, password: String) : Either<UserDomainModel, Failure>

    suspend fun userRegister(userName: String, password: String) : Either<Unit, Failure>


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////// Remote database operations ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    suspend fun getUserProfileById(userId: String) : Either<UserDomainModel, Failure>

    suspend fun getUserProfileListByUserName(userName: String) : Either<List<UserDomainModel>, Failure>

    suspend fun getFollowingUsersById(userId: String) : Either<List<UserDomainModel>, Failure>

    suspend fun getFollowerUsersById(userId: String) : Either<List<UserDomainModel>, Failure>

    suspend fun getPostByPostId(postId: String) : Either<PostDomainModel, Failure>

    suspend fun getPostListByUserId(userId: String) : Either<List<PostDomainModel>, Failure>

    suspend fun getLikedUsersByPostId(postId: String) : Either<List<UserDomainModel>, Failure>

    suspend fun updateUserProfile(userProfile: UserProfileUploadDomainModel) : Either<UserDomainModel, Failure>

    suspend fun addUserRelation(followerId: String, followingId: String) : Either<Unit, Failure>

    suspend fun removeUserRelation(followerId: String, followingId: String) : Either<Unit, Failure>

    suspend fun addUserLikePost(userId: String, postId: String) : Either<Unit, Failure>

    suspend fun removeUserLikePost(userId: String, postId: String) : Either<Unit, Failure>

    suspend fun uploadPost(post: PostUploadDataModel) : Either<PostDomainModel, Failure>

}