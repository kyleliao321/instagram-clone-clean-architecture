package com.example.instagram_clone_clean_architecture.app

import com.example.instagram_clone_clean_architecture.app.data.dataModule
import com.example.instagram_clone_clean_architecture.app.domain.domainModule
import com.example.instagram_clone_clean_architecture.app.presentation.presentationModule
import okhttp3.OkHttpClient
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal const val MODULE_NAME = "App"

val appModule = DI.Module("${MODULE_NAME}Module") {
    import(dataModule)
    import(domainModule)
    import(presentationModule)

    bind<OkHttpClient>() with singleton {
        OkHttpClient.Builder()
            .build()
    }

    bind<Retrofit>() with singleton {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .client(instance())
            .build()
    }
}