package com.example.instagram_clone_clean_architecture.feature.search.data.repository

import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.search.domain.repository.SearchRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.delay

class MockSearchRepositoryImpl : SearchRepository {

    private val userProfileList = listOf(
        UserDomainModel(id = 1, name = "Kyle", userName = "kyle", description =  "My name is Kyle", postNum = 4, followingNum = 1, followerNum = 2, imageSrc = "https://images.unsplash.com/photo-1486728297118-82a07bc48a28?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80"),
        UserDomainModel(id = 2, name = "Anna", userName = "anna", postNum = 0, followingNum = 2,followerNum =  0, imageSrc = "https://images.unsplash.com/photo-1486728297118-82a07bc48a28?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80"),
        UserDomainModel(id = 3, name = "John", userName = "john", postNum = 0, followingNum = 1, followerNum = 2)
    )

    override suspend fun getUserProfileListByKeyword(keyword: String): Either<List<UserDomainModel>, Failure> {
        delay(1000)

        val result = userProfileList.filter { userProfile ->
            userProfile.userName.contains(keyword)
        }

        return Either.Success(result)
    }
}