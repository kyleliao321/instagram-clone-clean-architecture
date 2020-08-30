package com.example.instagram_clone_clean_architecture.app

import android.app.Application
import com.example.instagram_clone_clean_architecture.BuildConfig
import com.example.instagram_clone_clean_architecture.app.domain.data_source.LocalDataSource
import com.example.instagram_clone_clean_architecture.app.domain.di.FeatureManager
import com.example.instagram_clone_clean_architecture.app.domain.di.FragmentArgsExternalSource
import com.example.library_base.baseModule
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.instance
import timber.log.Timber

class InstagramCloneApplication: Application(), DIAware {

    private val localDataSource: LocalDataSource by instance()

    /**
     * Top-level DI module manager, includes all dependencies.
     */
    override val di: DI = DI.lazy {
        import(androidXModule(this@InstagramCloneApplication))
        import(appModule)
        import(baseModule)
        importAll(FeatureManager.diModules)

        externalSources.add(FragmentArgsExternalSource())
    }

    override fun onCreate() {
        super.onCreate()
        initLocalDataSource()
        initTimber()

        Timber.i("Application context is created!")
    }

    private fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }

    private fun initLocalDataSource() {
        localDataSource.init(this)
    }
}