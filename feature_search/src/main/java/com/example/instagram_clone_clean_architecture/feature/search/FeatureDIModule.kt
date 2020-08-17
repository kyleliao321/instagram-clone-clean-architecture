package com.example.instagram_clone_clean_architecture.feature.search

import com.example.instagram_clone_clean_architecture.app.domain.di.DIModuleProvider
import com.example.instagram_clone_clean_architecture.feature.search.data.dataModule
import com.example.instagram_clone_clean_architecture.feature.search.domain.domainModule
import com.example.instagram_clone_clean_architecture.feature.search.presentation.presentationModule
import org.kodein.di.DI

internal const val MODULE_NAME = "Search"

object FeatureDIModule : DIModuleProvider {

    override val diModule = DI.Module("${MODULE_NAME}Module") {
        import(dataModule)
        import(domainModule)
        import(presentationModule)
    }
}