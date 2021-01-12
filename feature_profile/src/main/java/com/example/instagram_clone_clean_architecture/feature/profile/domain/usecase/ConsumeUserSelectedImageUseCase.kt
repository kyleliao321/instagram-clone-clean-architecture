package com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase

import android.net.Uri
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.usercase.UseCase
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class ConsumeUserSelectedImageUseCase(
    private val profileRepository: ProfileRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<Uri, Unit>(defaultDispatcher) {

    override suspend fun run(params: Unit): Either<Uri, Failure> =
        profileRepository.consumeUserSelectedImageUri()

}