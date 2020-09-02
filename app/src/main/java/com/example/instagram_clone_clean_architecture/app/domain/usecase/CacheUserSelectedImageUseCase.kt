package com.example.instagram_clone_clean_architecture.app.domain.usecase

import android.net.Uri
import com.example.instagram_clone_clean_architecture.app.domain.repository.AppRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.usercase.UseCase
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class CacheUserSelectedImageUseCase(
    private val appRepository: AppRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<Unit, CacheUserSelectedImageUseCase.Param>(defaultDispatcher) {

    override suspend fun run(params: Param): Either<Unit, Failure> =
        appRepository.cacheUserSelectedImage(params.imageUri)

    data class Param(val imageUri: Uri)
}