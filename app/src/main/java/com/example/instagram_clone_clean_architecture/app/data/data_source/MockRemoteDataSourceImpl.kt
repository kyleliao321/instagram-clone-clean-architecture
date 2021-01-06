package com.example.instagram_clone_clean_architecture.app.data.data_source

import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.instagram_clone_clean_architecture.app.domain.model.*
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.delay
import java.util.*

class MockRemoteDataSourceImpl : RemoteDataSource {

    private val networkFailProbability = 20

    private val mockRemoteImageSrc = "https://raw.githubusercontent.com/kyleliao321/instagram-clone-clean-architecture/master/assets/mock-remote-image.jpg"

    private val userLoginDataList = mutableListOf(
        Pair("7375a95e-82b5-4b7a-8cf8-59338f5a8a43", "12345"),
        Pair("c9dd129f-1922-4a52-b9f5-eaa7e9453c5d", "23456"),
        Pair("663a6f68-1cee-4774-8497-bec671f85611", "34567")
    )

    private val userProfileList = mutableListOf(
        UserDomainModel(id = "7375a95e-82b5-4b7a-8cf8-59338f5a8a43", name = "Kyle", userName = "kyle", description =  "My name is Kyle", postNum = 3, followingNum = 1, followerNum = 0, imageSrc = mockRemoteImageSrc),
        UserDomainModel(id = "c9dd129f-1922-4a52-b9f5-eaa7e9453c5d", name = "Anna", userName = "anna", postNum = 2, followingNum = 0,followerNum =  1, imageSrc = mockRemoteImageSrc),
        UserDomainModel(id = "663a6f68-1cee-4774-8497-bec671f85611", name = "John", userName = "john", postNum = 0, followingNum = 0, followerNum = 0)
    )

    // follower - following relationship
    private val userRelationList = mutableListOf(
        Pair("7375a95e-82b5-4b7a-8cf8-59338f5a8a43", "c9dd129f-1922-4a52-b9f5-eaa7e9453c5d")
    )

    private val userPostList = mutableListOf(
        PostDomainModel(id = "d7347a85-f721-43a0-b3f3-aa08718abdfc", belongUserId = "7375a95e-82b5-4b7a-8cf8-59338f5a8a43", date = Date(), location = null, description = "village",
            imageSrc = mockRemoteImageSrc
        ),
        PostDomainModel(id = "9374acb7-8da8-4344-b040-02742d3d914c", belongUserId = "7375a95e-82b5-4b7a-8cf8-59338f5a8a43", date = Date(), location = null, description = "village",
            imageSrc = mockRemoteImageSrc
        ),
        PostDomainModel(id = "22a5d295-d622-45a6-bf06-47007856aa01", belongUserId = "7375a95e-82b5-4b7a-8cf8-59338f5a8a43", date = Date(), location = null, description = "village",
            imageSrc = mockRemoteImageSrc
        ),
        PostDomainModel(id = "8575cdb2-1d95-4c9f-b2ea-5631ec83bc12", belongUserId = "c9dd129f-1922-4a52-b9f5-eaa7e9453c5d", date = Date(), location = null, description = "village",
            imageSrc = mockRemoteImageSrc
        ),
        PostDomainModel(id = "289b4b07-f203-4931-9524-8ee06d39e934", belongUserId = "c9dd129f-1922-4a52-b9f5-eaa7e9453c5d", date = Date(), location = null, description = "village",
            imageSrc = mockRemoteImageSrc
        )
    )

    // user - likedPost relationship
    private val userLikedList = mutableListOf(
        Pair("7375a95e-82b5-4b7a-8cf8-59338f5a8a43", "d7347a85-f721-43a0-b3f3-aa08718abdfc"),
        Pair("7375a95e-82b5-4b7a-8cf8-59338f5a8a43", "9374acb7-8da8-4344-b040-02742d3d914c"),
        Pair("7375a95e-82b5-4b7a-8cf8-59338f5a8a43", "22a5d295-d622-45a6-bf06-47007856aa01"),
        Pair("7375a95e-82b5-4b7a-8cf8-59338f5a8a43", "8575cdb2-1d95-4c9f-b2ea-5631ec83bc12"),
        Pair("7375a95e-82b5-4b7a-8cf8-59338f5a8a43", "289b4b07-f203-4931-9524-8ee06d39e934"),
        Pair("c9dd129f-1922-4a52-b9f5-eaa7e9453c5d", "d7347a85-f721-43a0-b3f3-aa08718abdfc"),
        Pair("c9dd129f-1922-4a52-b9f5-eaa7e9453c5d", "9374acb7-8da8-4344-b040-02742d3d914c"),
        Pair("c9dd129f-1922-4a52-b9f5-eaa7e9453c5d", "22a5d295-d622-45a6-bf06-47007856aa01"),
        Pair("c9dd129f-1922-4a52-b9f5-eaa7e9453c5d", "8575cdb2-1d95-4c9f-b2ea-5631ec83bc12"),
        Pair("c9dd129f-1922-4a52-b9f5-eaa7e9453c5d", "9374acb7-8da8-4344-b040-02742d3d914c")
    )

    private val mockToken = "mockToken"

    override suspend fun userLogin(
        userName: String,
        password: String
    ): Either<LoginCredentialDomainModel, Failure> {

        delay(1000)

        if (randomBoolean()) {
            return Either.Failure(Failure.NetworkConnection)
        }

        for (userProfile in userProfileList) {
            if (userProfile.userName == userName) {
                for (loginData in userLoginDataList) {
                    if (loginData.first == userProfile.id && loginData.second == password) {
                        return Either.Success(
                            LoginCredentialDomainModel(mockToken, userProfile)
                        )
                    }
                }
            }
        }

        return Either.Failure(Failure.LoginUserNameOrPasswordNotMatched)
    }

    override suspend fun userRegister(
        userName: String,
        password: String
    ): Either<Unit, Failure> {

        delay(1000)

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
            id = generateId(), name = userName, userName = userName, postNum = 0, followingNum = 0, followerNum = 0
        )
        userProfileList.add(newUserProfile)

        // second, add id-password pair in database
        userLoginDataList.add(Pair(newUserProfile.id, password))

        return Either.Success(Unit)
    }

    override suspend fun getUserProfileById(userId: String): Either<UserDomainModel, Failure> {

        if (randomBoolean()) {
            return Either.Failure(Failure.NetworkConnection)
        }

        delay(1000)

        for (userProfile in userProfileList) {
            if (userProfile.id == userId) {
                return Either.Success(userProfile)
            }
        }

        return Either.Failure(Failure.ServerError)
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

    override suspend fun getFollowingUsersById(userId: String): Either<List<UserDomainModel>, Failure> {
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

    override suspend fun getFollowerUsersById(userId: String): Either<List<UserDomainModel>, Failure> {
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

    override suspend fun getPostByPostId(postId: String): Either<PostDomainModel, Failure> {
        delay(1000)

        if (randomBoolean()) {
            return Either.Failure(Failure.NetworkConnection)
        }

        for (post in userPostList) {
            if (post.id == postId) {
                return Either.Success(post)
            }
        }

        return Either.Failure(Failure.ServerError)
    }

    override suspend fun getPostListByUserId(userId: String): Either<List<PostDomainModel>, Failure> {
        delay(1000)
        val result = userPostList.filter { it.belongUserId == userId }

        return Either.Success(result)
    }

    override suspend fun getLikedUsersByPostId(postId: String): Either<List<UserDomainModel>, Failure> {
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

    override suspend fun updateUserProfile(userProfile: UserProfileUploadDomainModel): Either<UserDomainModel, Failure> {
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

    override suspend fun addUserRelation(followerId: String, followingId: String): Either<Unit, Failure> {
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

    override suspend fun removeUserRelation(followerId: String, followingId: String): Either<Unit, Failure> {
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

    override suspend fun addUserLikePost(userId: String, postId: String): Either<Unit, Failure> {

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

    override suspend fun removeUserLikePost(userId: String, postId: String): Either<Unit, Failure> {

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

    override suspend fun uploadPost(post: PostUploadDomainModel): Either<PostDomainModel, Failure> {

        if (randomBoolean()) {
            return Either.Failure(Failure.NetworkConnection)
        }

        val mockNewPost = userPostList[0].copy(id = generateId(), belongUserId = post.belongUserId!!)
        userPostList.add(mockNewPost)
        return Either.Success(mockNewPost)
    }

    private fun randomBoolean(): Boolean =
        (0..10).random() < (networkFailProbability/10)

    private fun generateId(): String =
        UUID.randomUUID().toString()

}