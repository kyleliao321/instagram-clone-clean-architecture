// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("io.gitlab.arturbosch.detekt")
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
    apply(plugin = "io.gitlab.arturbosch.detekt")

    detekt {
        config = files("${project.rootDir}/detekt.yml")
        parallel = true
    }
}

tasks.register("clean",Delete::class){
    delete(rootProject.buildDir)
}