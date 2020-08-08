package com.example.instagram_clone_clean_architecture.feature.profile.data.repository

import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.delay
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

internal class MockProfileRepositoryImpl: ProfileRepository {

    private val userProfileMap: HashMap<Int, UserDomainModel> = hashMapOf(
        1 to UserDomainModel(id = 1, name = "Kyle", userName = "kyle", description =  "My name is Kyle", postNum = 4, followingNum = 0, followerNum = 2),
        2 to UserDomainModel(id = 2, name = "Anna", userName = "anna", postNum = 0, followingNum = 1,followerNum =  0),
        3 to UserDomainModel(id = 2, name = "John", userName = "john", postNum = 0, followingNum = 1, followerNum = 0)
    )

    private val userFollowerMap: HashMap<Int, List<Int>> = hashMapOf(
        1 to listOf(2, 3),
        2 to listOf(),
        3 to listOf()
    )

    private val userFollowingMap: HashMap<Int, List<Int>> = hashMapOf(
        1 to listOf(),
        2 to listOf(1),
        3 to listOf(1)
    )

    private val postMap: HashMap<Int, PostDomainModel> = hashMapOf(
        1 to PostDomainModel(id = 1, belongUserId = 1, date = Date(), location = null, description = null,
            imageSrc = "https://is4-ssl.mzstatic.com/image/thumb/Purple123/v4/9a/c1/5d/9ac15dd5-0614-52b5-6fe8-19df1b6dfad6/AppIcon-0-0-1x_U007emarketing-0-0-0-7-0-0-sRGB-0-0-0-GLES2_U002c0-512MB-85-220-0-0.png/246x0w.png"
        ),
        2 to PostDomainModel(id = 2, belongUserId = 1, date = Date(), location = null, description = null,
            imageSrc = "https://is4-ssl.mzstatic.com/image/thumb/Purple123/v4/9a/c1/5d/9ac15dd5-0614-52b5-6fe8-19df1b6dfad6/AppIcon-0-0-1x_U007emarketing-0-0-0-7-0-0-sRGB-0-0-0-GLES2_U002c0-512MB-85-220-0-0.png/246x0w.png"
        ),
        3 to PostDomainModel(id = 3, belongUserId = 1, date = Date(), location = null, description = null,
            imageSrc = "https://images.unsplash.com/photo-1486728297118-82a07bc48a28?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80"
        ),
        4 to PostDomainModel(id = 4, belongUserId = 1, date = Date(), location = null, description = null,
            imageSrc = "https://images.unsplash.com/photo-1486728297118-82a07bc48a28?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80"
        )
    )

    override suspend fun getUserProfileById(id: Int): Either<UserDomainModel?, Failure> {
        delay(1500)

        val userProfile = userProfileMap[id]

        return Either.Success(userProfile)
    }

    override suspend fun getFollowerById(id: Int): Either<List<UserDomainModel>, Failure> {
        val result = mutableListOf<UserDomainModel>()

        val followerIdList = userFollowerMap[id]

        followerIdList?.let {
            it.forEach { userId ->
                val userProfile = userProfileMap[userId]
                if (userProfile != null) result.add(userProfile)
            }
        }

        return Either.Success(result)
    }

    override suspend fun getFollowingById(id: Int): Either<List<UserDomainModel>, Failure> {
        val result = mutableListOf<UserDomainModel>()

        val followingList = userFollowingMap[id]

        followingList?.let {
            it.forEach { userId ->
                val userProfile = userProfileMap[userId]
                if (userProfile != null) result.add(userProfile)
            }
        }

        return Either.Success(result)
    }

    override suspend fun getPostByUserId(id: Int): Either<List<PostDomainModel>, Failure> {
        delay(2000)

        val userPostMap = postMap.filterValues {
            it.belongUserId == id
        }

        return Either.Success(ArrayList(userPostMap.values))
    }

    override suspend fun getPostByPostId(id: Int): Either<PostDomainModel?, Failure> {
        val post = postMap[id]
        return Either.Success(post)
    }

    override suspend fun updateUserProfile(userProfile: UserDomainModel): Either<UserDomainModel, Failure> {
        val userId = userProfile.id

        return when(userProfileMap[userId]) {
            null -> Either.Failure(Failure.ServerError)
            else -> {
                userProfileMap[userId] = userProfile
                Either.Success(userProfileMap[userId]!!)
            }
        }
    }

}