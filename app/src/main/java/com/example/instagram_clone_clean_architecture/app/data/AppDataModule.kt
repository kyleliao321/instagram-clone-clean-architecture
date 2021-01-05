package com.example.instagram_clone_clean_architecture.app.data

import com.example.instagram_clone_clean_architecture.app.MODULE_NAME
import com.example.instagram_clone_clean_architecture.app.data.data_source.CacheDataSourceImpl
import com.example.instagram_clone_clean_architecture.app.data.data_source.LocalDataSourceImpl
import com.example.instagram_clone_clean_architecture.app.data.data_source.MockRemoteDataSourceImpl
import com.example.instagram_clone_clean_architecture.app.data.repository.AppRepositoryImpl
import com.example.instagram_clone_clean_architecture.app.data.retrofit.services.AccountServices
import com.example.instagram_clone_clean_architecture.app.domain.data_source.CacheDataSource
import com.example.instagram_clone_clean_architecture.app.domain.data_source.LocalDataSource
import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.instagram_clone_clean_architecture.app.domain.repository.AppRepository
import org.kodein.di.*
import retrofit2.Retrofit

val dataModule = DI.Module("${MODULE_NAME}DataModule") {

    bind<AppRepository>() with singleton { AppRepositoryImpl(instance()) }

    bind<RemoteDataSource>() with singleton { MockRemoteDataSourceImpl() }

    bind<LocalDataSource>() with singleton { LocalDataSourceImpl() }

    bind<CacheDataSource>() with singleton { CacheDataSourceImpl() }

    // Retrofit services
    bind() from singleton { instance<Retrofit>().create(AccountServices::class.java) }

}