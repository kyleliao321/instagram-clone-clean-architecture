package com.example.instagram_clone_clean_architecture.feature.post.data

import com.example.instagram_clone_clean_architecture.feature.post.MODULE_NAME
import com.example.instagram_clone_clean_architecture.feature.post.data.repository.PostRepositoryImpl
import com.example.instagram_clone_clean_architecture.feature.post.domain.repository.PostRepository
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val dataModule = DI.Module("${MODULE_NAME}DataModule") {

    bind<PostRepository>() with singleton { PostRepositoryImpl(instance(), instance(), instance()) }

}