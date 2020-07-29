package com.example.instagram_clone_clean_architecture.app.domain.di

import org.kodein.di.DI

/**
 * An interface used for FeatureManager to extract bindings from feature modules.
 */
interface DIModuleProvider {
    val diModule: DI.Module
}