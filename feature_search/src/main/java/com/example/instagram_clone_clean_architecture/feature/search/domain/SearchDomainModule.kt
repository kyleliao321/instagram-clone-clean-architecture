package com.example.instagram_clone_clean_architecture.feature.search.domain

import com.example.instagram_clone_clean_architecture.feature.search.MODULE_NAME
import com.example.instagram_clone_clean_architecture.feature.search.domain.usecase.GetUserProfileListUseCase
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

internal val domainModule = DI.Module("${MODULE_NAME}DomainModule") {

    bind() from singleton { GetUserProfileListUseCase(instance()) }

}