package com.example.library_base.presentation.activity

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.DITrigger
import org.kodein.di.android.BuildConfig
import org.kodein.di.android.closestDI
import org.kodein.di.android.retainedDI
import org.kodein.di.diContext

/**
 * Allowing all activity classes to use DI module inside its scope.
 * (i.e: val navManager: ExampleNavManager by instance() )
 */
abstract class InjectionActivity(): AppCompatActivity(), DIAware {

    private val parentDI by closestDI()

    final override val diContext = diContext<AppCompatActivity>(this)

    final override val di: DI by retainedDI {
        extend(parentDI)
    }

    final override val diTrigger: DITrigger? by lazy {
        if (BuildConfig.DEBUG) DITrigger() else super.diTrigger
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        diTrigger?.trigger()
    }

}