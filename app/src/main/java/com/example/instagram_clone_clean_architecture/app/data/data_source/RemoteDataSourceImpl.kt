package com.example.instagram_clone_clean_architecture.app.data.data_source

import com.example.instagram_clone_clean_architecture.app.data.retrofit.requests.AccountRequest
import com.example.instagram_clone_clean_architecture.app.data.retrofit.requests.AddRelationRequest
import com.example.instagram_clone_clean_architecture.app.data.retrofit.requests.LikePostRequest
import com.example.instagram_clone_clean_architecture.app.data.retrofit.services.*
import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.PostUploadDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserProfileUploadDomainModel
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.net.HttpURLConnection

class RemoteDataSourceImpl(
    private val accountServices: AccountServices,
    private val userServices: UserServices,
    private val postServices: PostServices,
    private val relationServices: RelationServices,
    private val likeServices: LikeServices
): RemoteDataSource {
    override suspend fun userLogin(
        userName: String,
        password: String
    ): Either<UserDomainModel, Failure> {
        val accountReq = AccountRequest(userName, password)
        val res = accountServices.loginAsync(accountReq)
        
        return when (res.code()) {
            HttpURLConnection.HTTP_OK -> {
                val data = res.body()?.credential
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
        val res = userServices.getUserProfileAsync(userId)
        val status = res.code()
        val data = res.body()?.user

        return if (status === HttpURLConnection.HTTP_OK && data !== null) {
            Either.Success(UserDomainModel.from(data))
        } else {
            Either.Failure(Failure.ServerError)
        }
    }

    override suspend fun getUserProfileListByUserName(userName: String): Either<List<UserDomainModel>, Failure> {
        val res = userServices.searchUserProfilesAsync(userName)
        val status = res.code()
        val data = res.body()?.users

        return if (status === HttpURLConnection.HTTP_OK && data !== null) {
            Either.Success(data.map { UserDomainModel.from(it) })
        } else {
            Either.Failure(Failure.ServerError)
        }
    }

    override suspend fun getFollowingUsersById(userId: String): Either<List<UserDomainModel>, Failure> {
        val res = relationServices.getFollowingsAsync(userId)
        val status = res.code()
        val data = res.body()?.followings

        return if (status === HttpURLConnection.HTTP_OK && data !== null) {
            Either.Success(data.map { UserDomainModel.from(it) })
        } else {
            Either.Failure(Failure.ServerError)
        }
    }

    override suspend fun getFollowerUsersById(userId: String): Either<List<UserDomainModel>, Failure> {
        val res = relationServices.getFollowersAsync(userId)
        val status = res.code()
        val data = res.body()?.followers

        return if (status === HttpURLConnection.HTTP_OK && data !== null) {
            Either.Success(data.map { UserDomainModel.from(it) })
        } else {
            Either.Failure(Failure.ServerError)
        }
    }

    override suspend fun getPostByPostId(postId: String): Either<PostDomainModel, Failure> {
        val res = postServices.getPostAsync(postId)
        val status = res.code()
        val data = res.body()?.post

        return if (status === HttpURLConnection.HTTP_OK && data !== null) {
            Either.Success(PostDomainModel.from(data))
        } else {
            Either.Failure(Failure.ServerError)
        }
    }

    override suspend fun getPostListByUserId(userId: String): Either<List<PostDomainModel>, Failure> {
        val res = postServices.getPostsAsync(userId)
        val status = res.code()
        val data = res.body()?.posts

        return if (status === HttpURLConnection.HTTP_OK && data !== null) {
            Either.Success(data.map { PostDomainModel.from(it) })
        } else {
            Either.Failure(Failure.ServerError)
        }
    }

    override suspend fun getLikedUsersByPostId(postId: String): Either<List<UserDomainModel>, Failure> {
        val res = likeServices.getLikesAsync(postId)
        val status = res.code()
        val data = res.body()?.likedUsers

        return if (status === HttpURLConnection.HTTP_OK && data !== null) {
            Either.Success(data.map { UserDomainModel.from(it) })
        } else {
            Either.Failure(Failure.ServerError)
        }
    }

    override suspend fun updateUserProfile(userProfile: UserProfileUploadDomainModel): Either<UserDomainModel, Failure> {
        val idField = userProfile.id.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val userNameField = userProfile.userName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val aliasField = userProfile.name.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val descriptionField = userProfile.description.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val userImage = userProfile.imageByteArray?.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val res = userServices.updateUserProfileAsync(
            userProfile.id,
            idField,
            userNameField,
            aliasField,
            descriptionField,
            userImage
        )
        val status = res.code()
        val data = res.body()?.userId

        return if (status === HttpURLConnection.HTTP_CREATED && data !== null) {
            getUserProfileById(data)
        } else {
            Either.Failure(Failure.ServerError)
        }
    }

    override suspend fun addUserRelation(
        followerId: String,
        followingId: String
    ): Either<Unit, Failure> {
        val reqBody = AddRelationRequest(followerId, followingId)
        val res = relationServices.addRelationAsync(reqBody)
        val status = res.code()
        val data = res.body()?.followings

        return if (status === HttpURLConnection.HTTP_CREATED && data !== null) {
            Either.Success(Unit)
        } else {
            Either.Failure(Failure.ServerError)
        }
    }

    override suspend fun removeUserRelation(
        followerId: String,
        followingId: String
    ): Either<Unit, Failure> {
        val res = relationServices.removeRelationAsync(followerId, followingId)
        val status = res.code()
        val data = res.body()?.followings

        return if (status === HttpURLConnection.HTTP_OK && data !== null) {
            Either.Success(Unit)
        } else {
            Either.Failure(Failure.ServerError)
        }
    }

    override suspend fun addUserLikePost(userId: String, postId: String): Either<Unit, Failure> {
        val reqBody = LikePostRequest(userId, postId)
        val res = likeServices.likePostAsync(reqBody)
        val status = res.code()
        val data = res.body()?.likedUsers

        return if (status === HttpURLConnection.HTTP_CREATED && data !== null) {
            Either.Success(Unit)
        } else {
            Either.Failure(Failure.ServerError)
        }
    }

    override suspend fun removeUserLikePost(userId: String, postId: String): Either<Unit, Failure> {
        val res = likeServices.dislikePostAsync(userId, postId)
        val status = res.code()
        val data = res.body()?.likedUsers

        return if (status === HttpURLConnection.HTTP_OK && data !== null) {
            Either.Success(Unit)
        } else {
            Either.Failure(Failure.ServerError)
        }
    }

    override suspend fun uploadPost(post: PostUploadDomainModel): Either<PostDomainModel, Failure> {
        if (!post.isPostUploadReady) {
            return Either.Failure(Failure.PostNotComplete)
        }

        val descriptionField = post.description?.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val locationField = post.location?.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val timestampField = post.date!!.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val postedUserIdField = post.belongUserId!!.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val postImageField = post.imageByteArray!!.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val res = postServices.addNewPostAsync(
            descriptionField,
            locationField,
            timestampField,
            postedUserIdField,
            postImageField
        )
        val status = res.code()
        val data = res.body()?.post

        return if (status === HttpURLConnection.HTTP_CREATED && data !== null) {
            Either.Success(PostDomainModel.from(data))
        } else {
            Either.Failure(Failure.ServerError)
        }
    }
}