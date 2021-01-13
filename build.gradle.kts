// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id(GradlePluginId.DETEKT)
    id(GradlePluginId.KOTLIN_JVM) apply false
    id(GradlePluginId.KOTLIN_ANDROID) apply false
    id(GradlePluginId.KOTLIN_ANDROID_EXTENSIONS) apply false
    id(GradlePluginId.ANDROID_APPLICATION) apply false
    id(GradlePluginId.ANDROID_DYNAMIC_FEATURE) apply false
    id(GradlePluginId.ANDROID_LIBRARY) apply false
    id(GradlePluginId.SAFE_ARGS) apply false
}


allprojects {
    repositories {
        google()
        jcenter()
    }

    configurations.all {
        resolutionStrategy {
            force("org.jetbrains.kotlinx:kotlinx-coroutines-core:${CoreVersion.KOTLIN}")
            force("org.jetbrains.kotlinx:kotlinx-coroutines-core:${CoreVersion.KOTLIN}")
            force("org.jetbrains.kotlinx:kotlinx-coroutines-core:${CoreVersion.KOTLIN}")
            force("androidx.recyclerview:recyclerview:${LibraryVersions.RECYCLER_VIEW}")
        }
    }
}

subprojects {
    apply(plugin = GradlePluginId.DETEKT)

    detekt {
        config = files("${project.rootDir}/detekt.yml")
        parallel = true
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
buildscript {
    val kotlin_version by extra("1.4.21")
    dependencies {
        "classpath"("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    }
}
