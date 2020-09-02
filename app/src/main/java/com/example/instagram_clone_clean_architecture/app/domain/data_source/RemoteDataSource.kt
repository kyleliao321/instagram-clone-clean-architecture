package com.example.instagram_clone_clean_architecture.app.domain.data_source

import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.PostUploadDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either

interface RemoteDataSource {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////// Authentication operations ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    suspend fun userLogin(userName: String, password: String) : Either<UserDomainModel, Failure>

    suspend fun userRegister(userName: String, password: String) : Either<UserDomainModel, Failure>


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////// Remote database operations ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    suspend fun getUserProfileById(userId: Int) : Either<UserDomainModel?, Failure>

    suspend fun getUserProfileListByUserName(userName: String) : Either<List<UserDomainModel>, Failure>

    suspend fun getFollowingUsersById(userId: Int) : Either<List<UserDomainModel>, Failure>

    suspend fun getFollowerUsersById(userId: Int) : Either<List<UserDomainModel>, Failure>

    suspend fun getPostByPostId(postId: Int) : Either<PostDomainModel?, Failure>

    suspend fun getPostListByUserId(userId: Int) : Either<List<PostDomainModel>, Failure>

    suspend fun getLikedUsersByPostId(postId: Int) : Either<List<UserDomainModel>, Failure>

    suspend fun updateUserProfile(userProfile: UserDomainModel) : Either<UserDomainModel, Failure>

    suspend fun addUserRelation(followerId: Int, followingId: Int) : Either<Unit, Failure>

    suspend fun removeUserRelation(followerId: Int, followingId: Int) : Either<Unit, Failure>

    suspend fun addUserLikePost(userId: Int, postId: Int) : Either<Unit, Failure>

    suspend fun removeUserLikePost(userId: Int, postId: Int) : Either<Unit, Failure>

    suspend fun uploadPost(post: PostUploadDomainModel) : Either<PostDomainModel, Failure>

}