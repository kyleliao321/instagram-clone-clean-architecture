package com.example.library_base

import com.example.library_base.presentation.navigation.NavigationManager
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

internal const val MODULE_NAME = "Base"

val baseModule = DI.Module("${MODULE_NAME}Module") {

    bind<NavigationManager>() with singleton { NavigationManager() }

}