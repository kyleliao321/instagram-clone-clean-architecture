package com.example.instagram_clone_clean_architecture.app

import android.app.Application
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.instagram_clone_clean_architecture.R
import com.example.instagram_clone_clean_architecture.app.domain.data_source.CacheDataSource
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

    // keep reference to prevent memory cache been clear by GC
    // ! Temporary solution. Should cache data both in memory and DataStore
    private val cacheDataSource: CacheDataSource by instance()

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
        initContentResolver()
        initEncryptedSharedPreference()
        initCacheDir()
        initTimber()

        Timber.i("Application context is created!")
    }

    private fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }

    private fun initContentResolver() {
        val contentResolver = applicationContext.contentResolver

        localDataSource.init(contentResolver)
    }

    private fun initCacheDir() {
        cacheDataSource.init(cacheDir)
    }

    private fun initEncryptedSharedPreference() {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        val sharedPrefsKey = resources.getString(R.string.shared_preference_key)

        val encryptedSharedPref = EncryptedSharedPreferences
            .create(
                sharedPrefsKey,
                masterKeyAlias,
                applicationContext,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

        localDataSource.init(encryptedSharedPref)
    }
}