package com.example.instagram_clone_clean_architecture.feature.login.data

import com.example.instagram_clone_clean_architecture.feature.login.MODULE_NAME
import com.example.instagram_clone_clean_architecture.feature.login.data.repository.MockLoginRepositoryImpl
import com.example.instagram_clone_clean_architecture.feature.login.domain.LoginRepository
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val dataModule = DI.Module("${MODULE_NAME}DataModule") {
    bind<LoginRepository>() with singleton {
        MockLoginRepositoryImpl(instance())
    }
}