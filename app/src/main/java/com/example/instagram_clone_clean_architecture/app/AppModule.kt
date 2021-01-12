package com.example.instagram_clone_clean_architecture.app

import com.example.instagram_clone_clean_architecture.BuildConfig
import com.example.instagram_clone_clean_architecture.app.data.dataModule
import com.example.instagram_clone_clean_architecture.app.data.retrofit.intercetors.AuthorizeInterceptor
import com.example.instagram_clone_clean_architecture.app.domain.domainModule
import com.example.instagram_clone_clean_architecture.app.presentation.presentationModule
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

internal const val MODULE_NAME = "App"

val appModule = DI.Module("${MODULE_NAME}Module") {
    import(dataModule)
    import(domainModule)
    import(presentationModule)

    bind<HttpLoggingInterceptor>() with singleton {
        HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Timber.tag("HTTP").d(message)
            }
        }).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    bind<OkHttpClient.Builder>() with singleton {
        OkHttpClient.Builder()
    }

    bind<Retrofit.Builder>() with singleton {
        Retrofit.Builder()
    }

    bind<OkHttpClient>() with singleton {
        instance<OkHttpClient.Builder>()
            .addInterceptor(instance<AuthorizeInterceptor>())
            .addInterceptor(instance<HttpLoggingInterceptor>())
            .build()
    }

    bind<Retrofit>() with singleton {
        instance<Retrofit.Builder>()
            .baseUrl(BuildConfig.GRADLE_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(instance())
            .build()
    }
}