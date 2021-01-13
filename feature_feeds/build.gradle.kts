plugins {
    id(GradlePluginId.ANDROID_DYNAMIC_FEATURE)
    id(GradlePluginId.KOTLIN_ANDROID)
    id(GradlePluginId.KOTLIN_ANDROID_EXTENSIONS)
    id(GradlePluginId.SAFE_ARGS)
    id(GradlePluginId.KOTLIN_KAPT)
}

android {
    compileSdkVersion(AndroidConfig.COMPILE_SDK_VERSION)

    defaultConfig {
        applicationId = AndroidConfig.ID
        minSdkVersion(AndroidConfig.MIN_SDK_VERSION)
        targetSdkVersion(AndroidConfig.TARGET_SDK_VERSION)
        buildToolsVersion(AndroidConfig.BUILD_TOOLS_VERSION)

        versionCode = AndroidConfig.VERSION_CODE
        versionName = AndroidConfig.VERSION_NAME
        testInstrumentationRunner = AndroidConfig.TEST_INSTRUMENTATION_RUNNER
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

    flavorDimensions(ServerDimension.name)
    productFlavors {
        create(FlavorType.LOCAL) {
            setDimension(LocalFlavor.dimension.name)
        }

        create(FlavorType.REMOTE) {
            setDimension(RemoteFlavor.dimension.name)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    dataBinding {
        isEnabled = true
    }

    sourceSets {
        getByName("main").res.srcDirs("src/main/res")
    }

    // Temporary solution for "kotlinx-coroutines-test" dependency conflict in library_base
    packagingOptions {
        exclude("META-INF/AL2.0")
        exclude("META-INF/LGPL2.1")
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    implementation(project(ModuleDependency.APP))
    implementation(LibraryDependency.PAGING)
    implementation(LibraryDependency.LIFECYCLE_RUNTIME)
    implementation(LibraryDependency.KOTLIN_CO_ANDROID)
    implementation(LibraryDependency.KOTLIN_CO_CORE)
    implementation(LibraryDependency.RECYCLER_VIEW)
    testImplementation(project(ModuleDependency.LIBRARY_TEST_UTILS))

    testImplementation(TestLibraryDependency.JUNIT)
    testImplementation(TestLibraryDependency.MOCKK)
    testImplementation(TestLibraryDependency.KOTLIN_CO_CORE)
    testImplementation(TestLibraryDependency.KLUENT)
    testImplementation(TestLibraryDependency.KOTLIN_CO_TEST)
    testImplementation(TestLibraryDependency.ANDROID_ARCH_CORE_TEST)
}