plugins {
    id(GradlePluginId.ANDROID_LIBRARY)
    id(GradlePluginId.KOTLIN_ANDROID)
    id(GradlePluginId.KOTLIN_ANDROID_EXTENSIONS)
}

android {
    compileSdkVersion(AndroidConfig.COMPILE_SDK_VERSION)
    buildToolsVersion(AndroidConfig.BUILD_TOOLS_VERSION)

    defaultConfig {
        minSdkVersion(AndroidConfig.MIN_SDK_VERSION)
        targetSdkVersion(AndroidConfig.TARGET_SDK_VERSION)
        versionCode = AndroidConfig.VERSION_CODE
        versionName = AndroidConfig.VERSION_NAME

        testInstrumentationRunner = AndroidConfig.TEST_INSTRUMENTATION_RUNNER
        consumerProguardFiles("consumer_rules.pro")
    }

    buildTypes {
        getByName(BuildType.RELEASE) {
            isMinifyEnabled = BuildTypeRelease.isMinifyEnabled
            isDebuggable = BuildTypeRelease.isDebuggable
            proguardFiles("proguard-android.txt", "proguard-rules.pro")
        }

        getByName(BuildType.DEBUG) {
            isMinifyEnabled = BuildTypeDebug.isMinifyEnabled
            isDebuggable = BuildTypeDebug.isDebuggable
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    // Temporary solution for "kotlinx-coroutines-test" dependency conflict in library_base
    packagingOptions {
        // May not be needed after updating to AGP 4.x - check
        exclude("META-INF/AL2.0")
        exclude("META-INF/LGPL2.1")
    }
}

dependencies {
    api(LibraryDependency.KOTLIN_REFLECTION)
    api(LibraryDependency.KOTLIN_STDLIB)
    api(LibraryDependency.CORE_KTX)
    api(LibraryDependency.APP_COMPAT)
    api(LibraryDependency.CONSTRAINT_LAYOUT)
    api(LibraryDependency.NAVIGATION_FRAGMENT_KTX)
    api(LibraryDependency.NAVIGATION_UI_KTX)
    api(LibraryDependency.NAVIGATION_DYNAMIC_FEATURE)
    api(LibraryDependency.VIEWMODEL)
    api(LibraryDependency.LIVEDATA)
    api(LibraryDependency.KODEIN_DI)
    api(LibraryDependency.KODEIN_ANDROID_X)
    api(LibraryDependency.TIMBER)
    api(LibraryDependency.SHIMMER)
    api(LibraryDependency.COIL)
    api(LibraryDependency.SPIN_KIT)
    api(LibraryDependency.MATERIAL_DESIGN)
    api(LibraryDependency.SWIPE_REFRESH_LAYOUT)

    debugApi(LibraryDependency.LEAK_CANARY)

    testImplementation(project(ModuleDependency.LIBRARY_TEST_UTILS))
    testImplementation(TestLibraryDependency.JUNIT)
    testImplementation(TestLibraryDependency.MOCKK)
    testImplementation(TestLibraryDependency.MOCKK_CO)
    testImplementation(TestLibraryDependency.KLUENT)
    testImplementation(TestLibraryDependency.KOTLIN_CO_TEST)
    testImplementation(TestLibraryDependency.ANDROID_ARCH_CORE_TEST)
}