package com.example.instagram_clone_clean_architecture.feature.feeds.data

import com.example.instagram_clone_clean_architecture.feature.feeds.MODULE_NAME
import com.example.instagram_clone_clean_architecture.feature.feeds.data.repository.FeedRepositoryImpl
import com.example.instagram_clone_clean_architecture.feature.feeds.domain.repository.FeedRepository
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val dataModule = DI.Module("${MODULE_NAME}DataModule") {
    bind<FeedRepository>() with singleton { FeedRepositoryImpl(instance()) }
}