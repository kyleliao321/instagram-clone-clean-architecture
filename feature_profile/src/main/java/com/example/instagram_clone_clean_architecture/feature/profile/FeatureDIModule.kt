package com.example.instagram_clone_clean_architecture.feature.profile

import com.example.instagram_clone_clean_architecture.app.domain.di.DIModuleProvider
import org.kodein.di.DI

internal const val MODULE_NAME = "Profile"

object FeatureDIModule : DIModuleProvider {

    override val diModule: DI.Module = DI.Module("${MODULE_NAME}Module") {

    }
}