package com.example.instagram_clone_clean_architecture.feature.post

import com.example.instagram_clone_clean_architecture.app.domain.di.DIModuleProvider
import com.example.instagram_clone_clean_architecture.feature.post.domain.domainModule
import org.kodein.di.DI

internal const val MODULE_NAME = "Post"

object FeatureDIModule : DIModuleProvider {

    override val diModule = DI.Module("${MODULE_NAME}Module") {
        import(domainModule)
    }
}