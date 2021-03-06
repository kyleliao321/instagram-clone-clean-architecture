object AndroidConfig {
    const val COMPILE_SDK_VERSION = 30
    const val MIN_SDK_VERSION = 23
    const val TARGET_SDK_VERSION = 30
    const val BUILD_TOOLS_VERSION = "30.0.0"

    const val VERSION_CODE = 1
    const val VERSION_NAME = "1.0"

    const val ID = "com.example.instagram_clone_clean_architecture"
    const val TEST_INSTRUMENTATION_RUNNER =
        "com.example.library_test_utils.DIAwareAndroidTestRunner"
}

interface BuildType {

    companion object {
        const val RELEASE = "release"
        const val DEBUG = "debug"
    }

    val isMinifyEnabled: Boolean
    val isDebuggable: Boolean
}

object BuildTypeRelease : BuildType {
    override val isMinifyEnabled = false
    override val isDebuggable = false
}

object BuildTypeDebug : BuildType {
    override val isMinifyEnabled = false
    override val isDebuggable = true
}

interface DimensionType {
    val name: String
}

object ServerDimension : DimensionType {
    override val name = "server"
}

interface FlavorType {
    companion object {
        const val LOCAL = "local"
        const val REMOTE = "remote"
    }

    val dimension: DimensionType
}

object LocalFlavor : FlavorType {
    override val dimension = ServerDimension
}

object RemoteFlavor : FlavorType {
    override val dimension = ServerDimension
}