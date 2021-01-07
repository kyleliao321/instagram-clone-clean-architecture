package com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase

import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserProfileUploadDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.extension.getJpegByteArray
import com.example.library_base.domain.extension.resizeAndCrop
import com.example.library_base.domain.usercase.UseCase
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.util.*

class UpdateUserProfileUseCase(
    private val profileRepository: ProfileRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<UserDomainModel, UpdateUserProfileUseCase.Param>(defaultDispatcher) {

    override suspend fun run(params: Param): Either<UserDomainModel, Failure> {
        val getImageBitmapResult = params.userProfile.imageUri?.let { profileRepository.getBitmap(it) }

        if (getImageBitmapResult !== null && getImageBitmapResult is Either.Failure) {
            return getImageBitmapResult
        }

        val imageBitmap = (getImageBitmapResult as? Either.Success)?.a
        val resizedImageBitmap = imageBitmap?.resizeAndCrop(400, 400)
        val imageByteArray = resizedImageBitmap?.getJpegByteArray(50)
        val randomFileName = UUID.randomUUID().toString()

        val cacheFileResult = imageByteArray?.let { profileRepository.cacheCompressedUploadImage(randomFileName, it) }

        if (cacheFileResult !== null && cacheFileResult is Either.Failure) {
            return cacheFileResult
        }

        val cachedFile = (cacheFileResult as? Either.Success)?.a

        params.userProfile.cachedImageFile = cachedFile

        return profileRepository.updateUserProfile(params.userProfile).also {
            cachedFile?.let { it.delete() }
        }
    }

    data class Param(val userProfile: UserProfileUploadDomainModel)
}