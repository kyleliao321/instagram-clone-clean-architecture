package com.example.instagram_clone_clean_architecture.app.data

import androidx.appcompat.app.AppCompatActivity
import com.example.instagram_clone_clean_architecture.app.MODULE_NAME
import com.example.instagram_clone_clean_architecture.app.data.data_source.LocalDataSourceImpl
import com.example.instagram_clone_clean_architecture.app.data.data_source.MockRemoteDataSourceImpl
import com.example.instagram_clone_clean_architecture.app.data.repository.MockAppRepositoryImpl
import com.example.instagram_clone_clean_architecture.app.domain.data_source.LocalDataSource
import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.instagram_clone_clean_architecture.app.domain.repository.AppRepository
import org.kodein.di.*
import org.kodein.di.android.x.AndroidLifecycleScope

val dataModule = DI.Module("${MODULE_NAME}DataModule") {

    bind<AppRepository>() with singleton { MockAppRepositoryImpl(instance()) }

    bind<RemoteDataSource>() with singleton { MockRemoteDataSourceImpl() }

    bind<LocalDataSource>() with singleton { LocalDataSourceImpl() }

}