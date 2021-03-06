package com.example.instagram_clone_clean_architecture.app.domain

import com.example.instagram_clone_clean_architecture.app.MODULE_NAME
import com.example.instagram_clone_clean_architecture.app.domain.usecase.CacheUserSelectedImageUseCase
import com.example.instagram_clone_clean_architecture.app.domain.usecase.GetCachedLoginUserUseCase
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val domainModule = DI.Module("${MODULE_NAME}DomainModule") {

    bind() from singleton { GetCachedLoginUserUseCase(instance()) }

    bind() from singleton { CacheUserSelectedImageUseCase(instance()) }

}