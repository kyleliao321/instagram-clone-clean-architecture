package com.example.instagram_clone_clean_architecture.feature.feeds

import com.example.instagram_clone_clean_architecture.app.domain.di.DIModuleProvider
import com.example.instagram_clone_clean_architecture.feature.feeds.data.dataModule
import com.example.instagram_clone_clean_architecture.feature.feeds.domain.domainModule
import com.example.instagram_clone_clean_architecture.feature.feeds.presentation.presentationModule
import org.kodein.di.DI

internal const val MODULE_NAME = "Feeds"

object FeatureDIModule : DIModuleProvider {

    override val diModule = DI.Module("${MODULE_NAME}Module") {
        import(dataModule)
        import(domainModule)
        import(presentationModule)
    }

}