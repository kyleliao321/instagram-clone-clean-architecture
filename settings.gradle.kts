/**
 * Since version 6.0 of Gradle update, properties defined inside buildSrc cannot be
 * accessed from setting.gradle.kts. As a result, it is necessary to synchronize
 * the version of plugins and including module in two places.
 */

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }

    plugins {
        id("io.gitlab.arturbosch.detekt") version "1.9.1"
        id("com.android.application") version "4.0.0"
        id("com.android.dynamic-feature") version "4.0.0"
        id("com.android.library") version "4.0.0"
        id("org.jetbrains.kotlin.jvm") version "1.3.72"
        id("org.jetbrains.kotlin.android") version "1.3.72"
        id("org.jetbrains.kotlin.android.extensions") version "1.3.72"
        id("androidx.navigation.safeargs.kotlin") version "2.3.0"
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.android.application",
                "com.android.library",
                "com.android.dynamic-feature" -> useModule("com.android.tools.build:gradle:4.0.0")
                "androidx.navigation.safeargs.kotlin" -> useModule("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.0")
            }
        }
    }
}

include(":feature_profile")
include(":feature_search")
include("feature_login")
include("feature_post")
include("feature_feeds")
include(":library_base")
include(":library_test_utils")
include(":app")
rootProject.name = "instagram-clone-clean-architecture"