package com.example.instagram_clone_clean_architecture.feature.search.data.repository

import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.search.domain.repository.SearchRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either

class MockSearchRepositoryImpl : SearchRepository {

    private val userProfileList = listOf(
        UserDomainModel(id = 1, name = "Kyle", userName = "kyle0321", postNum = 1, followerNum = 1, followingNum = 2),
        UserDomainModel(id = 2, name = "Kyle", userName = "kyle0221", postNum = 1, followerNum = 1, followingNum = 2),
        UserDomainModel(id = 3, name = "Kyle", userName = "kyle7878", postNum = 1, followerNum = 1, followingNum = 2),
        UserDomainModel(id = 4, name = "Kyle", userName = "kyle3332", postNum = 1, followerNum = 1, followingNum = 2),
        UserDomainModel(id = 5, name = "Kyle", userName = "kyle2143", postNum = 1, followerNum = 1, followingNum = 2),
        UserDomainModel(id = 6, name = "Kyle", userName = "kyle1245", postNum = 1, followerNum = 1, followingNum = 2)
    )

    override suspend fun getUserProfileListByKeyword(keyword: String): Either<List<UserDomainModel>, Failure> {
        val result = userProfileList.filter { userProfile ->
            userProfile.userName.contains(keyword)
        }

        return Either.Success(result)
    }
}