package com.example.instagram_clone_clean_architecture.app

import android.app.Application
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.androidXModule
import timber.log.Timber

class InstagramCloneApplication: Application(), DIAware {

    override val di: DI = DI.lazy {
        import(androidXModule(this@InstagramCloneApplication))
    }

    override fun onCreate() {
        super.onCreate()
        initTimber()

        Timber.i("Application context is created!")
    }

    private fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }
}