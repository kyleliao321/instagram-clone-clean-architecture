import kotlin.IllegalArgumentException
import com.android.build.gradle.internal.dsl.BaseFlavor
import com.android.build.gradle.internal.dsl.ProductFlavor

plugins {
    id(GradlePluginId.ANDROID_APPLICATION)
    id(GradlePluginId.KOTLIN_ANDROID)
    id(GradlePluginId.KOTLIN_ANDROID_EXTENSIONS)
    id(GradlePluginId.KOTLIN_KAPT)
    id(GradlePluginId.SAFE_ARGS)
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

        buildConfigFieldFromGradleProperty("apiBaseUrl")
        buildConfigFieldFromGradleProperty("apiStaticUrl")
        buildConfigFieldFromGradleProperty("mockNetworkFailureRate")
        buildConfigField("FEATURE_MODULE_NAMES", ModuleDependency.getDynamicFeatureModuleNames())
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
            flavorBuildConfigField("boolean", "remoteServer", "false")
        }

        create(FlavorType.REMOTE) {
            setDimension(RemoteFlavor.dimension.name)
            flavorBuildConfigField("boolean", "remoteServer", "true")
        }
    }

    // Each feature module that is included in settings.gradle.kts is added here as dynamic feature
    dynamicFeatures = ModuleDependency.getDynamicFeatureModules().toMutableSet()

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        dataBinding = true
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
    api(project(ModuleDependency.LIBRARY_BASE))
    implementation(LibraryDependency.SECURITY_CRYPTO)
    implementation(LibraryDependency.OK_HTTP_LOG_INTERCEPTOR)

    testImplementation(project(ModuleDependency.LIBRARY_TEST_UTILS))
    testImplementation(TestLibraryDependency.JUNIT)
    testImplementation(TestLibraryDependency.MOCKK)
    testImplementation(TestLibraryDependency.KOTLIN_CO_CORE)
    testImplementation(TestLibraryDependency.KLUENT)
    testImplementation(TestLibraryDependency.KOTLIN_CO_TEST)
    testImplementation(TestLibraryDependency.ANDROID_ARCH_CORE_TEST)
    testImplementation(TestLibraryDependency.MOCK_WEB_SERVER)
}

fun BaseFlavor.buildConfigFieldFromGradleProperty(propertyName: String) {
    val propertyValue = project.property(propertyName) as? String

    if (propertyValue === null) {
        throw IllegalArgumentException("Property")
    }

    val androidResourceName = "GRADLE_${propertyName.toSnakeCase()}".toUpperCase()
    buildConfigField("String", androidResourceName, propertyValue)
}

fun ProductFlavor.flavorBuildConfigField(type: String, name: String, value: String) {
    val androidResourceName = "FLAVOR_${name.toSnakeCase()}".toUpperCase()
    buildConfigField(type, androidResourceName, value)
}

fun String.toSnakeCase() = this.split(Regex("(?=[A-Z])")).joinToString("_") { it.toLowerCase() }

/**
 * Generate new field from String array inside BuildConfig.
 *
 * @param name Name of the field.
 * @param value Array of String, which will be transform into format like ["test", "test1"].
 */
fun com.android.build.gradle.internal.dsl.DefaultConfig.buildConfigField(name: String, value: Set<String>) {
    val formattedStringArray = value.joinToString(prefix = "{", postfix = "}", separator = ",", transform = { "\"$it\"" })
    buildConfigField("String[]", name, formattedStringArray)
}