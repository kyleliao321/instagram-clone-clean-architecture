package com.example.instagram_clone_clean_architecture.app.domain.di

import com.example.instagram_clone_clean_architecture.BuildConfig
import timber.log.Timber

/**
 * By using consistent naming convention for all DI modules in feature modules,
 * we can use FeatureManager to collect all object by name, and apply them into
 * top-level DI Module.
 */
object FeatureManager {

    private const val featurePackagePrefix = "com.example.instagram_clone_clean_architecture.feature"

    private val featureModuleNames = BuildConfig.FEATURE_MODULE_NAMES

    val diModules = featureModuleNames
        .map { "${featurePackagePrefix}.$it.FeatureDIModule" }
        .map {
            try {
                Timber.i("DI Module loading $it")
                Class.forName(it).kotlin.objectInstance as DIModuleProvider
            } catch (e: ClassNotFoundException) {
                throw ClassNotFoundException("DI module class not found $it")
            }
        }
        .map { it.diModule }
}