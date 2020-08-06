package com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase

import androidx.navigation.NavDirections
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.usercase.UseCase
import com.example.library_base.domain.utility.Either
import com.example.library_base.presentation.navigation.NavigationManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class NavigateToEditProfileUseCase(
    private val navigationManager: NavigationManager,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : UseCase<Unit, NavigateToEditProfileUseCase.Param>(defaultDispatcher) {

    override suspend fun run(params: Param): Either<Unit, Failure> {
        navigationManager.onNavEvent(params.navDirections)
        return Either.Success(Unit)
    }

    data class Param(val navDirections: NavDirections)
}