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
}

subprojects {
    // detekt can auto-reference the required file while working on module-level gradle.build.kts
    // Without it, any plugins that apply in module-level cannot be resolved when written.
    // (i.e: android block)
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
    val kotlin_version by extra("1.3.72")
    dependencies {
        "classpath"("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    }
}
