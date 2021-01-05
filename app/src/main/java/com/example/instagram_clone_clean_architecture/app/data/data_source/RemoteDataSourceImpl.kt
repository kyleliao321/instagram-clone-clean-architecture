package com.example.instagram_clone_clean_architecture.app.data.data_source

import com.example.instagram_clone_clean_architecture.app.data.model.PostUploadDataModel
import com.example.instagram_clone_clean_architecture.app.data.model.UserProfileUploadDataModel
import com.example.instagram_clone_clean_architecture.app.data.retrofit.requests.AccountRequest
import com.example.instagram_clone_clean_architecture.app.data.retrofit.services.AccountServices
import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import java.net.HttpURLConnection

class RemoteDataSourceImpl(
    private val accountServices: AccountServices
): RemoteDataSource {
    override suspend fun userLogin(
        userName: String,
        password: String
    ): Either<UserDomainModel, Failure> {
        val accountReq = AccountRequest(userName, password)
        val res = accountServices.loginAsync(accountReq)

        return when (res.code()) {
            HttpURLConnection.HTTP_OK -> {
                val data = res.body()?.loginCredential
                return when (data) {
                    null -> Either.Failure(Failure.ServerError)
                    else -> getUserProfileById(data.userId)
                }
            }
            HttpURLConnection.HTTP_UNAUTHORIZED -> Either.Failure(Failure.LoginUserNameOrPasswordNotMatched)
            else -> Either.Failure(Failure.ServerError)
        }
    }

    override suspend fun userRegister(
        userName: String,
        password: String
    ): Either<Unit, Failure> {
        val accountReq = AccountRequest(userName, password)
        val res = accountServices.registerNewAccountAsync(accountReq)

        return when (res.code()) {
            HttpURLConnection.HTTP_CREATED -> Either.Success(Unit)
            HttpURLConnection.HTTP_CONFLICT -> Either.Failure(Failure.DuplicatedUserName)
            else -> Either.Failure(Failure.ServerError)
        }
    }

    override suspend fun getUserProfileById(userId: String): Either<UserDomainModel, Failure> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserProfileListByUserName(userName: String): Either<List<UserDomainModel>, Failure> {
        TODO("Not yet implemented")
    }

    override suspend fun getFollowingUsersById(userId: String): Either<List<UserDomainModel>, Failure> {
        TODO("Not yet implemented")
    }

    override suspend fun getFollowerUsersById(userId: String): Either<List<UserDomainModel>, Failure> {
        TODO("Not yet implemented")
    }

    override suspend fun getPostByPostId(postId: String): Either<PostDomainModel?, Failure> {
        TODO("Not yet implemented")
    }

    override suspend fun getPostListByUserId(userId: String): Either<List<PostDomainModel>, Failure> {
        TODO("Not yet implemented")
    }

    override suspend fun getLikedUsersByPostId(postId: String): Either<List<UserDomainModel>, Failure> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserProfile(userProfile: UserProfileUploadDataModel): Either<UserDomainModel, Failure> {
        TODO("Not yet implemented")
    }

    override suspend fun addUserRelation(
        followerId: String,
        followingId: String
    ): Either<Unit, Failure> {
        TODO("Not yet implemented")
    }

    override suspend fun removeUserRelation(
        followerId: String,
        followingId: String
    ): Either<Unit, Failure> {
        TODO("Not yet implemented")
    }

    override suspend fun addUserLikePost(userId: String, postId: String): Either<Unit, Failure> {
        TODO("Not yet implemented")
    }

    override suspend fun removeUserLikePost(userId: String, postId: String): Either<Unit, Failure> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadPost(post: PostUploadDataModel): Either<PostDomainModel, Failure> {
        TODO("Not yet implemented")
    }
}