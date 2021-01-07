package com.example.instagram_clone_clean_architecture.app.domain.model

data class LoginCredentialDomainModel(
    val jwt: String,
    val userProfile: UserDomainModel
)