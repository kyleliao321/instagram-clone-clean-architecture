package com.example.instagram_clone_clean_architecture.feature.login.domain

import com.example.instagram_clone_clean_architecture.feature.login.MODULE_NAME
import com.example.instagram_clone_clean_architecture.feature.login.domain.usercase.GetLocalLoginUserDataUseCase
import com.example.instagram_clone_clean_architecture.feature.login.domain.usercase.UserLoginUseCase
import com.example.instagram_clone_clean_architecture.feature.login.domain.usercase.UserRegisterUseCase
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val domainModule = DI.Module("${MODULE_NAME}DomainModule") {

    bind() from singleton { UserLoginUseCase(instance()) }

    bind() from singleton { UserRegisterUseCase(instance()) }

    bind() from singleton { GetLocalLoginUserDataUseCase(instance()) }

}