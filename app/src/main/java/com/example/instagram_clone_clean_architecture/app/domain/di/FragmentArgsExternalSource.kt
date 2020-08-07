package com.example.instagram_clone_clean_architecture.app.domain.di

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgs
import androidx.navigation.NavArgsLazy
import org.kodein.di.DI
import org.kodein.di.bindings.BindingDI
import org.kodein.di.bindings.ExternalSource
import org.kodein.type.jvmType
import timber.log.Timber
import kotlin.reflect.KClass

/**
 * Provide kodein di the answer for fragment's argument dependency when kodein di cannot find one.
 */
class FragmentArgsExternalSource : ExternalSource {

    override fun getFactory(di: BindingDI<*>, key: DI.Key<*, *, *>): ((Any?) -> Any)? {
        val fragment = di.context as? Fragment

        if (fragment != null) {
            // Navigation's safe-args plugin will generate the class to hold argument for fragments.
            // The pattern is always the canonicalName of the class with "Args" in the end.
            val deductedArgsName = fragment.javaClass.canonicalName + "Args"

            Timber.d("Deducted argument class : $deductedArgsName")
            Timber.d("Actual java class canonical name : ${key.type.jvmType.javaClass.canonicalName}")
            if (deductedArgsName == key.type.jvmType.javaClass.canonicalName) {
                val navArgsInstance = getNavArgsInstance(fragment)

                if (navArgsInstance != null) {
                    return { navArgsInstance }
                }
            }
        }

        return null
    }

    /**
     * Generate the actual argument instance as return value.
     */
    @MainThread
    private fun getNavArgsInstance(fragment: Fragment): NavArgs? {
        val arguments = fragment.arguments ?: return null

        val safeArgsClassSuffix = "Args"
        val className = "${fragment::class.java.canonicalName}$safeArgsClassSuffix"

        @Suppress("UNCHECKED_CAST")
        val navArgsClass = requireNotNull(getArgsClass(className)) {
            // This may happen when nav graph resource doest not define arguments for particular fragment
            "Fragment $className has arguments, but corresponding navArgs class $className does not exist."
        }

        val navArgs by NavArgsLazy(navArgsClass) { arguments }
        return navArgs
    }

    /**
     * Get the given class object by class name.
     */
    private fun getArgsClass(className: String): KClass<NavArgs>? = try {
        @Suppress("UNCHECKED_CAST")
        Class.forName(className).kotlin as KClass<NavArgs>
    } catch (e: ClassNotFoundException) {
        null
    }


}