package com.example.instagram_clone_clean_architecture.feature.post.domain

import com.example.instagram_clone_clean_architecture.feature.post.MODULE_NAME
import com.example.instagram_clone_clean_architecture.feature.post.domain.usecase.GetLoginUserUseCase
import com.example.instagram_clone_clean_architecture.feature.post.domain.usecase.GetUserSelectedImageUseCase
import com.example.instagram_clone_clean_architecture.feature.post.domain.usecase.UploadPostUseCase
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val domainModule = DI.Module("${MODULE_NAME}DomainModule") {

    bind() from singleton { GetUserSelectedImageUseCase(instance()) }

    bind() from singleton { GetLoginUserUseCase(instance()) }

    bind() from singleton { UploadPostUseCase(instance()) }

}