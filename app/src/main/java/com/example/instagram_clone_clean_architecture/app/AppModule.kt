package com.example.instagram_clone_clean_architecture.app

import com.example.instagram_clone_clean_architecture.app.data.dataModule
import com.example.instagram_clone_clean_architecture.app.domain.domainModule
import com.example.instagram_clone_clean_architecture.app.presentation.presentationModule
import org.kodein.di.DI

internal const val MODULE_NAME = "App"

val appModule = DI.Module("${MODULE_NAME}Module") {
    import(dataModule)
    import(domainModule)
    import(presentationModule)
}