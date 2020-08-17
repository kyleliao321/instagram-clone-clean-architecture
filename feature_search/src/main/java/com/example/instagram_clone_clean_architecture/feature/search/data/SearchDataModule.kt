package com.example.instagram_clone_clean_architecture.feature.search.data

import com.example.instagram_clone_clean_architecture.feature.search.MODULE_NAME
import com.example.instagram_clone_clean_architecture.feature.search.data.repository.MockSearchRepositoryImpl
import com.example.instagram_clone_clean_architecture.feature.search.domain.repository.SearchRepository
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

val dataModule = DI.Module("${MODULE_NAME}DataModule") {
    bind<SearchRepository>() with singleton { MockSearchRepositoryImpl() }
}