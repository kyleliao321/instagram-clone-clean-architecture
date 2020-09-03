package com.example.instagram_clone_clean_architecture.app.data.data_source

import com.example.instagram_clone_clean_architecture.app.data.model.PostUploadDataModel
import com.example.instagram_clone_clean_architecture.app.data.model.UserProfileUploadDataModel
import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.PostUploadDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.delay
import timber.log.Timber
import java.util.*
import kotlin.random.Random

class MockRemoteDataSourceImpl : RemoteDataSource {

    private val randomSeed = 100

    private var tmpIdCount = 5

    private var tmpPostCount = 5

    private val userLoginDataList = mutableListOf(
        Pair(1, "12345"),
        Pair(2, "23456"),
        Pair(3, "34567")
    )

    private val userProfileList = mutableListOf(
        UserDomainModel(id = 1, name = "Kyle", userName = "kyle", description =  "My name is Kyle", postNum = 3, followingNum = 1, followerNum = 0, imageSrc = "https://images.unsplash.com/photo-1486728297118-82a07bc48a28?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80"),
        UserDomainModel(id = 2, name = "Anna", userName = "anna", postNum = 2, followingNum = 0,followerNum =  1, imageSrc = "https://images.unsplash.com/photo-1486728297118-82a07bc48a28?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80"),
        UserDomainModel(id = 3, name = "John", userName = "john", postNum = 0, followingNum = 0, followerNum = 0)
    )

    // follower - following relationship
    private val userRelationList = mutableListOf(
        Pair(1, 2)
    )

    private val userPostList = mutableListOf(
        PostDomainModel(id = 1, belongUserId = 1, date = Date(), location = null, description = "Moooooooooooooooooooooooooooo",
            imageSrc = "https://is4-ssl.mzstatic.com/image/thumb/Purple123/v4/9a/c1/5d/9ac15dd5-0614-52b5-6fe8-19df1b6dfad6/AppIcon-0-0-1x_U007emarketing-0-0-0-7-0-0-sRGB-0-0-0-GLES2_U002c0-512MB-85-220-0-0.png/246x0w.png"
        ),
        PostDomainModel(id = 2, belongUserId = 1, date = Date(), location = null, description = "Moooooooooooooooooooooooooooo",
            imageSrc = "https://is4-ssl.mzstatic.com/image/thumb/Purple123/v4/9a/c1/5d/9ac15dd5-0614-52b5-6fe8-19df1b6dfad6/AppIcon-0-0-1x_U007emarketing-0-0-0-7-0-0-sRGB-0-0-0-GLES2_U002c0-512MB-85-220-0-0.png/246x0w.png"
        ),
        PostDomainModel(id = 3, belongUserId = 1, date = Date(), location = null, description = "Moooooooooooooooooooooooooooo",
            imageSrc = "https://images.unsplash.com/photo-1486728297118-82a07bc48a28?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80"
        ),
        PostDomainModel(id = 4, belongUserId = 2, date = Date(), location = null, description = "Moooooooooooooooooooooooooooo",
            imageSrc = "https://images.unsplash.com/photo-1486728297118-82a07bc48a28?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80"
        ),
        PostDomainModel(id = 5, belongUserId = 2, date = Date(), location = null, description = "Moooooooooooooooooooooooooooo",
            imageSrc = "https://images.unsplash.com/photo-1486728297118-82a07bc48a28?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80"
        )
    )

    // user - likedPost relationship
    private val userLikedList = mutableListOf(
        Pair(1, 1),
        Pair(1, 2),
        Pair(1, 3),
        Pair(1, 4),
        Pair(1, 5),
        Pair(2, 1),
        Pair(2, 2),
        Pair(2, 3),
        Pair(2, 4),
        Pair(3, 2)
    )

    override suspend fun userLogin(
        userName: String,
        password: String
    ): Either<UserDomainModel, Failure> {

        if (randomBoolean()) {
            return Either.Failure(Failure.NetworkConnection)
        }

        for (userProfile in userProfileList) {
            if (userProfile.userName == userName) {
                for (loginData in userLoginDataList) {
                    if (loginData.first == userProfile.id && loginData.second == password) {
                        return Either.Success(userProfile)
                    }
                }
            }
        }

        return Either.Failure(Failure.LoginUserNameOrPasswordNotMatched)
    }

    override suspend fun userRegister(
        userName: String,
        password: String
    ): Either<UserDomainModel, Failure> {

        if (randomBoolean()) {
            return Either.Failure(Failure.NetworkConnection)
        }

        // check if the user name already exist
        for (userProfile in userProfileList) {
            if (userProfile.userName == userName) {
                return Either.Failure(Failure.DuplicatedUserName)
            }
        }

        // first, create a user profile in database
        val newUserProfile = UserDomainModel(
            id = ++tmpIdCount, name = userName, userName = userName, postNum = 0, followingNum = 0, followerNum = 0
        )
        userProfileList.add(newUserProfile)

        // second, add id-password pair in database
        userLoginDataList.add(Pair(newUserProfile.id, password))

        return Either.Success(newUserProfile)
    }

    override suspend fun getUserProfileById(userId: Int): Either<UserDomainModel?, Failure> {

        if (randomBoolean()) {
            return Either.Failure(Failure.NetworkConnection)
        }

        delay(1000)

        for (userProfile in userProfileList) {
            if (userProfile.id == userId) {
                return Either.Success(userProfile)
            }
        }

        return Either.Success(null)
    }

    override suspend fun getUserProfileListByUserName(userName: String): Either<List<UserDomainModel>, Failure> {
        delay(1000)

        if (randomBoolean()) {
            return Either.Failure(Failure.NetworkConnection)
        }

        val result = userProfileList.filter { userProfile ->
            userProfile.userName.contains(userName)
        }

        return Either.Success(result)
    }

    override suspend fun getFollowingUsersById(userId: Int): Either<List<UserDomainModel>, Failure> {
        delay(1000)

        if (randomBoolean()) {
            return Either.Failure(Failure.NetworkConnection)
        }

        val result = mutableListOf<UserDomainModel>()

        userRelationList
            .filter { it.first == userId }
            .forEach {
                for (userProfile in userProfileList) {
                    if (userProfile.id == it.second) {
                        result.add(userProfile)
                    }
                }
            }

        return Either.Success(result)
    }

    override suspend fun getFollowerUsersById(userId: Int): Either<List<UserDomainModel>, Failure> {
        delay(1000)

        if (randomBoolean()) {
            return Either.Failure(Failure.NetworkConnection)
        }

        val result = mutableListOf<UserDomainModel>()

        userRelationList
            .filter { it.second == userId }
            .forEach {
                for (userProfile in userProfileList) {
                    if (userProfile.id == it.first) {
                        result.add(userProfile)
                    }
                }
            }

        return Either.Success(result)
    }

    override suspend fun getPostByPostId(postId: Int): Either<PostDomainModel?, Failure> {
        delay(1000)

        if (randomBoolean()) {
            return Either.Failure(Failure.NetworkConnection)
        }

        for (post in userPostList) {
            if (post.id == postId) {
                return Either.Success(post)
            }
        }

        return Either.Success(null)
    }

    override suspend fun getPostListByUserId(userId: Int): Either<List<PostDomainModel>, Failure> {
        delay(1000)
        val result = userPostList.filter { it.belongUserId == userId }

        return Either.Success(result)
    }

    override suspend fun getLikedUsersByPostId(postId: Int): Either<List<UserDomainModel>, Failure> {
        var result = mutableListOf<UserDomainModel>()

        if (randomBoolean()) {
            return Either.Failure(Failure.NetworkConnection)
        }

        userLikedList
            .filter { it.second == postId }
            .forEach { pair ->
                userProfileList.forEach { userProfile ->
                    if (userProfile.id == pair.first) {
                        result.add(userProfile)
                    }
                }
            }

        return Either.Success(result)
    }

    override suspend fun updateUserProfile(userProfile: UserProfileUploadDataModel): Either<UserDomainModel, Failure> {
        delay(1000)

        if (randomBoolean()) {
            return Either.Failure(Failure.NetworkConnection)
        }

        val id = userProfile.id

        var targetIndex = -1
        var originalUserProfile: UserDomainModel? = null
        for ((index, value) in userProfileList.withIndex()) {
            if (value.id == id) {
                targetIndex = index
                originalUserProfile = value
            }
        }

        return if (originalUserProfile == null) {
            Either.Failure(Failure.ServerError)
        } else {
            val newUserProfile = UserDomainModel(
                id = id,
                name = userProfile.name,
                userName = userProfile.userName,
                description = userProfile.description,
                imageSrc = originalUserProfile!!.imageSrc,
                postNum = originalUserProfile!!.postNum,
                followerNum = originalUserProfile!!.followerNum,
                followingNum = originalUserProfile!!.followerNum
            )
            userProfileList.removeAt(targetIndex)
            userProfileList.add(newUserProfile)
            Either.Success(newUserProfile)
        }
    }

    override suspend fun addUserRelation(followerId: Int, followingId: Int): Either<Unit, Failure> {
        delay(1000)

        if (randomBoolean()) {
            return Either.Failure(Failure.NetworkConnection)
        }

        for (relation in userRelationList) {
            if (relation.first == followerId && relation.second == followingId) {
                throw IllegalArgumentException("Cannot add relationship that is already exist")
//                return Either.Failure(Failure.ServerError)
            }
        }

        userRelationList.add(Pair(followerId, followingId))

        for (userProfile in userProfileList) {
            if (userProfile.id == followerId) {
                userProfile.followingNum += 1
            } else if (userProfile.id == followingId) {
                userProfile.followerNum += 1
            }
        }

        return Either.Success(Unit)
    }

    override suspend fun removeUserRelation(followerId: Int, followingId: Int): Either<Unit, Failure> {
        delay(1000)

        if (randomBoolean()) {
            return Either.Failure(Failure.NetworkConnection)
        }

        var targetIndex = -1
        for ((index, relation) in userRelationList.withIndex()) {
            if (relation.first == followerId && relation.second == followingId) {
                targetIndex = index
            }
        }

        if (targetIndex == -1) {
            throw IllegalArgumentException("Cannot remove relationship that is not exist")
//            return Either.Failure(Failure.ServerError)
        }

        userRelationList.removeAt(targetIndex)

        for (userProfile in userProfileList) {
            if (userProfile.id == followerId) {
                userProfile.followingNum -= 1
            } else if (userProfile.id == followingId) {
                userProfile.followerNum -= 1
            }
        }

        return Either.Success(Unit)
    }

    override suspend fun addUserLikePost(userId: Int, postId: Int): Either<Unit, Failure> {

        if (randomBoolean()) {
            return Either.Failure(Failure.NetworkConnection)
        }

        for (liked in userLikedList) {
            if (liked.first == userId && liked.second == postId) {
                throw IllegalArgumentException("Cannot add relationship that is already exist")
            }
        }

        userLikedList.add(Pair(userId, postId))
        return Either.Success(Unit)
    }

    override suspend fun removeUserLikePost(userId: Int, postId: Int): Either<Unit, Failure> {

        if (randomBoolean()) {
            return Either.Failure(Failure.NetworkConnection)
        }

        var targetIndex = -1
        for ((index, liked) in userLikedList.withIndex()) {
            if (liked.first == userId && liked.second == postId) {
                targetIndex = index
            }
        }

        if (targetIndex == -1) {
            throw IllegalArgumentException("Cannot remove relationship that is not exist")
        }

        userLikedList.removeAt(targetIndex)
        return Either.Success(Unit)
    }

    override suspend fun uploadPost(post: PostUploadDataModel): Either<PostDomainModel, Failure> {

        if (randomBoolean()) {
            return Either.Failure(Failure.NetworkConnection)
        }

        val mockNewPost = userPostList[0].copy(id = ++tmpPostCount, belongUserId = post.belongUser)
        userPostList.add(mockNewPost)
        return Either.Success(mockNewPost)
    }

    private fun randomBoolean(): Boolean =
        (0..10).random() <= 3 // 20% chance

}