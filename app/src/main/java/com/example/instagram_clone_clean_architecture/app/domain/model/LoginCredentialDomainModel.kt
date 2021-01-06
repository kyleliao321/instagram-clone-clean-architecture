package com.example.instagram_clone_clean_architecture.app.domain.model

import com.example.instagram_clone_clean_architecture.app.data.model.LoginCredentialDataModel

data class LoginCredentialDomainModel(
    val jwt: String,
    val userProfile: UserDomainModel
)