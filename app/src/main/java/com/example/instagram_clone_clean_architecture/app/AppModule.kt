package com.example.instagram_clone_clean_architecture.app

import com.example.instagram_clone_clean_architecture.app.data.dataModule
import org.kodein.di.DI

internal const val MODULE_NAME = "App"

val appModule = DI.Module("${MODULE_NAME}Module") {
    import(dataModule)
}