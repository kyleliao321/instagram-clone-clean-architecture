package com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.usercase.UseCase
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class GetBitmapUseCase(
    private val profileRepository: ProfileRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<Bitmap, GetBitmapUseCase.Param>(defaultDispatcher) {

    override suspend fun run(params: Param): Either<Bitmap, Failure> =
        profileRepository.getBitmap(params.uri)

    data class Param(val uri: Uri)
}