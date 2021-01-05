package com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase

import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.usercase.UseCase
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class GetLikedUsersUseCase(
    private val profileRepository: ProfileRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<List<UserDomainModel>, GetLikedUsersUseCase.Param>(defaultDispatcher) {

    override suspend fun run(params: Param): Either<List<UserDomainModel>, Failure> =
        profileRepository.getLikedUsersByPostId(params.postId)

    data class Param(val postId: String)
}