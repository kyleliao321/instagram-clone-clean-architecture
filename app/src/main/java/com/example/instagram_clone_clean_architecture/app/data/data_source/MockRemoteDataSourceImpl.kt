package com.example.instagram_clone_clean_architecture.app.data.data_source

import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.delay
import java.util.*

class MockRemoteDataSourceImpl : RemoteDataSource {

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

    override suspend fun userLogin(
        userName: String,
        password: String
    ): Either<UserDomainModel, Failure> {
        for (userProfile in userProfileList) {
            if (userProfile.userName == userName) {
                for (loginData in userLoginDataList) {
                    if (loginData.first == userProfile.id && loginData.second == password) {
                        return Either.Success(userProfile)
                    }
                }
            }
        }

        return Either.Failure(Failure.ServerError)
    }

    override suspend fun userRegister(
        userName: String,
        password: String
    ): Either<UserDomainModel, Failure> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserProfileById(userId: Int): Either<UserDomainModel?, Failure> {
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

        val result = userProfileList.filter { userProfile ->
            userProfile.userName.contains(userName)
        }

        return Either.Success(result)
    }

    override suspend fun getFollowingUsersById(userId: Int): Either<List<UserDomainModel>, Failure> {
        delay(1000)
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

    override suspend fun updateUserProfile(userProfile: UserDomainModel): Either<UserDomainModel, Failure> {
        delay(1000)
        for (currentUser in userProfileList) {
            if (currentUser.id == userProfile.id) {
                userProfileList.remove(currentUser)
                userProfileList.add(userProfile)
                return Either.Success(userProfile)
            }
        }

        return Either.Failure(Failure.ServerError)
    }

    override suspend fun addUserRelation(followerId: Int, followingId: Int): Either<Unit, Failure> {
        delay(1000)
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


}