package com.example.instagram_clone_clean_architecture.app.data

import com.example.instagram_clone_clean_architecture.BuildConfig
import com.example.instagram_clone_clean_architecture.app.MODULE_NAME
import com.example.instagram_clone_clean_architecture.app.data.data_source.CacheDataSourceImpl
import com.example.instagram_clone_clean_architecture.app.data.data_source.LocalDataSourceImpl
import com.example.instagram_clone_clean_architecture.app.data.data_source.MockRemoteDataSourceImpl
import com.example.instagram_clone_clean_architecture.app.data.data_source.RemoteDataSourceImpl
import com.example.instagram_clone_clean_architecture.app.data.repository.AppRepositoryImpl
import com.example.instagram_clone_clean_architecture.app.data.retrofit.intercetors.AuthorizeInterceptor
import com.example.instagram_clone_clean_architecture.app.data.retrofit.services.*
import com.example.instagram_clone_clean_architecture.app.domain.data_source.CacheDataSource
import com.example.instagram_clone_clean_architecture.app.domain.data_source.LocalDataSource
import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.instagram_clone_clean_architecture.app.domain.repository.AppRepository
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import retrofit2.Retrofit

val dataModule = DI.Module("${MODULE_NAME}DataModule") {

    bind<AppRepository>() with singleton { AppRepositoryImpl(instance()) }

    bind<RemoteDataSource>() with singleton {
        RemoteDataSourceImpl(
            instance(),
            instance(),
            instance(),
            instance(),
            instance(),
            instance()
        )
    }

    bind<LocalDataSource>() with singleton { LocalDataSourceImpl() }

    bind<CacheDataSource>() with singleton { CacheDataSourceImpl() }

    // Retrofit Interceptor
    bind() from singleton { AuthorizeInterceptor(instance()) }

    // Retrofit services
    bind() from singleton { instance<Retrofit>().create(AccountServices::class.java) }

    bind() from singleton { instance<Retrofit>().create(UserServices::class.java) }

    bind() from singleton { instance<Retrofit>().create(PostServices::class.java) }

    bind() from singleton { instance<Retrofit>().create(RelationServices::class.java) }

    bind() from singleton { instance<Retrofit>().create(LikeServices::class.java) }

    bind() from singleton { instance<Retrofit>().create(FeedServices::class.java) }
}