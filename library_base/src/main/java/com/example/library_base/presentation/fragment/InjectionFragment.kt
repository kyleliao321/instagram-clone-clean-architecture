package com.example.library_base.presentation.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import org.kodein.di.DIAware
import org.kodein.di.DITrigger
import org.kodein.di.android.x.BuildConfig
import org.kodein.di.android.x.closestDI
import org.kodein.di.diContext

abstract class InjectionFragment: Fragment(), DIAware {

    final override val di by closestDI()

    final override val diContext = diContext<Fragment>(this)

    final override val diTrigger: DITrigger? by lazy {
        if (BuildConfig.DEBUG) DITrigger() else super.diTrigger
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        diTrigger?.trigger()
    }
}