import kotlin.reflect.full.memberProperties

internal const val FEATURE_PREFIX = ":feature_"

object ModuleDependency {
    const val APP = ":app"
    const val FEATURE_PROFILE = ":feature_profile"
    const val FEATURE_SEARCH = ":feature_search"
    const val LIBRARY_BASE = ":library_base"

    fun getAllModules() = ModuleDependency::class.memberProperties
        .filter { it.isConst } // only const values are the module names
        .map { it.getter.call().toString() } // get the name by calling getter
        .toSet()

    fun getDynamicFeatureModules() = getAllModules()
        .filter { it.startsWith(FEATURE_PREFIX) }
        .toSet()
}