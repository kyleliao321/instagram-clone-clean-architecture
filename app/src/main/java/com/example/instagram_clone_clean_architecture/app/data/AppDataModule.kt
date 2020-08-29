package com.example.instagram_clone_clean_architecture.app.data

import com.example.instagram_clone_clean_architecture.app.MODULE_NAME
import com.example.instagram_clone_clean_architecture.app.data.data_source.MockLocalDataSourceImpl
import com.example.instagram_clone_clean_architecture.app.data.data_source.MockRemoteDataSourceImpl
import com.example.instagram_clone_clean_architecture.app.domain.data_source.LocalDataSource
import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

val dataModule = DI.Module("${MODULE_NAME}DataModule") {

    bind<RemoteDataSource>() with singleton { MockRemoteDataSourceImpl() }

    bind<LocalDataSource>() with singleton { MockLocalDataSourceImpl() }

}