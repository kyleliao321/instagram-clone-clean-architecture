package com.example.instagram_clone_clean_architecture.feature.profile.presentation.main

import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetUserProfileUseCase
import com.example.library_base.presentation.viewmodel.BaseViewModel
import timber.log.Timber

class ProfileMainViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase
): BaseViewModel() {
}