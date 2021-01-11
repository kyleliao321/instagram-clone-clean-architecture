package com.example.library_test_utils

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.androidXModule

class DIAwareTestApplication: Application(), DIAware {

    private lateinit var injectedModule: DI.Module

    override val di: DI = DI.lazy {
        import(androidXModule(this@DIAwareTestApplication))
        import(injectedModule)
    }

    internal fun injectModule(module: DI.Module) {
        injectedModule = module
    }
}

abstract class DIAwareTestRule<F: Fragment>: ActivityTestRule<FragmentActivity>(FragmentActivity::class.java, true) {

    override fun afterActivityLaunched() {
        super.afterActivityLaunched()
        activity.runOnUiThread {
            val fm = activity.supportFragmentManager
            val transaction = fm.beginTransaction()
            transaction.replace(
                android.R.id.content,
                createFragment()
            ).commit()
        }
    }

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        val application = InstrumentationRegistry.getInstrumentation()
            .targetContext.applicationContext as? DIAwareTestApplication
            ?: throw IllegalStateException("Test is not running on ${DIAwareAndroidTestRunner::class.java.name}")

        application.injectModule(getModule())
    }

    abstract fun createFragment(): F
    abstract fun getModule(): DI.Module
}

fun <F: Fragment> DIAwareTestRule<F>.runOnUiThread(block: () -> Unit) = activity.run {
    block()
}

fun <F: Fragment> makeDIAwareTestRule(fragment: F, module: DI.Module): DIAwareTestRule<F> = object: DIAwareTestRule<F>() {
    override fun createFragment(): F = fragment
    override fun getModule(): DI.Module = module
}