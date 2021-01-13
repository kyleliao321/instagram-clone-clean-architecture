object LibraryVersions {
    const val CORE_KTX = "1.3.1"
    const val APP_COMPAT = "1.1.0"
    const val CONSTRAINT_LAYOUT = "1.1.3"
    const val NAVIGATION = "2.3.0"
    const val LIFECYCLE = "2.2.0"
    const val KODEIN = "7.0.0"
    const val TIMBER = "4.7.1"
    const val COIL = "0.11.0"
    const val SHIMMER = "0.5.0"
    const val MATERIAL_DESIGN = "1.2.0"
    const val SPIN_KIT = "1.4.0"
    const val SWIPE_REFRESH_LAYOUT = "1.1.0"
    const val LEAK_CANARY = "2.4"
    const val SECURITY_CRYPTO = "1.0.0-rc03"
    const val RETROFIT = "2.9.0"
    const val OK_HTTP = "4.8.1"
    const val GSON = "2.8.0"
    const val OK_HTTP_LOG_INTERCEPTOR = "4.9.0"
    const val PAGING = "3.0.0-alpha11"
}

object LibraryDependency {
    const val KOTLIN_STDLIB = "org.jetbrains.kotlin:kotlin-stdlib:${CoreVersion.KOTLIN}"
    const val KOTLIN_REFLECTION = "org.jetbrains.kotlin:kotlin-reflect:${CoreVersion.KOTLIN}"
    const val CORE_KTX = "androidx.core:core-ktx:${LibraryVersions.CORE_KTX}"
    const val APP_COMPAT = "androidx.appcompat:appcompat:${LibraryVersions.APP_COMPAT}"
    const val CONSTRAINT_LAYOUT =
        "androidx.constraintlayout:constraintlayout:${LibraryVersions.CONSTRAINT_LAYOUT}"
    const val NAVIGATION_FRAGMENT_KTX =
        "androidx.navigation:navigation-fragment-ktx:${LibraryVersions.NAVIGATION}"
    const val NAVIGATION_UI_KTX =
        "androidx.navigation:navigation-ui-ktx:${LibraryVersions.NAVIGATION}"
    const val NAVIGATION_DYNAMIC_FEATURE =
        "androidx.navigation:navigation-dynamic-features-fragment:${LibraryVersions.NAVIGATION}"
    const val VIEWMODEL = "androidx.lifecycle:lifecycle-viewmodel-ktx:${LibraryVersions.LIFECYCLE}"
    const val LIVEDATA = "androidx.lifecycle:lifecycle-livedata-ktx:${LibraryVersions.LIFECYCLE}"
    const val KODEIN_DI = "org.kodein.di:kodein-di:${LibraryVersions.KODEIN}"
    const val KODEIN_DI_GENERIC = "org.kodein.di:kodein-di-generic-jvm:${LibraryVersions.KODEIN}"
    const val KODEIN_ANDROID_X =
        "org.kodein.di:kodein-di-framework-android-x:${LibraryVersions.KODEIN}"
    const val TIMBER = "com.jakewharton.timber:timber:${LibraryVersions.TIMBER}"
    const val COIL = "io.coil-kt:coil:${LibraryVersions.COIL}"
    const val SHIMMER = "com.facebook.shimmer:shimmer:${LibraryVersions.SHIMMER}"
    const val MATERIAL_DESIGN =
        "com.google.android.material:material:${LibraryVersions.MATERIAL_DESIGN}"
    const val SPIN_KIT = "com.github.ybq:Android-SpinKit:${LibraryVersions.SPIN_KIT}"
    const val SWIPE_REFRESH_LAYOUT =
        "androidx.swiperefreshlayout:swiperefreshlayout:${LibraryVersions.SWIPE_REFRESH_LAYOUT}"
    const val LEAK_CANARY =
        "com.squareup.leakcanary:leakcanary-android:${LibraryVersions.LEAK_CANARY}"
    const val SECURITY_CRYPTO =
        "androidx.security:security-crypto:${LibraryVersions.SECURITY_CRYPTO}"
    const val RETROFIT = "com.squareup.retrofit2:retrofit:${LibraryVersions.RETROFIT}"
    const val OK_HTTP = "com.squareup.okhttp3:okhttp:${LibraryVersions.OK_HTTP}"
    const val GSON = "com.squareup.retrofit2:converter-gson:${LibraryVersions.GSON}"
    const val OK_HTTP_LOG_INTERCEPTOR =
        "com.squareup.okhttp3:logging-interceptor:${LibraryVersions.OK_HTTP_LOG_INTERCEPTOR}"
    const val PAGING = "androidx.paging:paging-runtime:${LibraryVersions.PAGING}"
}