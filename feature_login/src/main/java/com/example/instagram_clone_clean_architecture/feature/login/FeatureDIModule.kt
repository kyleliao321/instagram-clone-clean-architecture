package com.example.instagram_clone_clean_architecture.feature.login

import com.example.instagram_clone_clean_architecture.app.domain.di.DIModuleProvider
import com.example.instagram_clone_clean_architecture.feature.login.data.dataModule
import com.example.instagram_clone_clean_architecture.feature.login.domain.domainModule
import com.example.instagram_clone_clean_architecture.feature.login.presentation.presentationModule
import org.kodein.di.DI

internal const val MODULE_NAME = "Login"

object FeatureDIModule: DIModuleProvider {

    override val diModule = DI.Module("${MODULE_NAME}Module") {
        import(dataModule)
        import(domainModule)
        import(presentationModule)
    }

}