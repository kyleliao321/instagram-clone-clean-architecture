
object TestLibraryVersion {
    const val KOTLIN_CO_TEST = "1.3.8"
    const val JUNIT = "4.12"
    const val MOCKK = "1.10.0"
    const val MOCKK_CO = "1.3.8"
    const val KLUENT = "1.61"
    const val ANDROIDX_ARCH_CORE_TEST = "2.1.0"
}

object TestLibraryDependency {
    const val KOTLIN_CO_TEST = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${TestLibraryVersion.KOTLIN_CO_TEST}"
    const val JUNIT = "junit:junit:${TestLibraryVersion.JUNIT}"
    const val MOCKK = "io.mockk:mockk:${TestLibraryVersion.MOCKK}"
    const val MOCKK_CO = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${TestLibraryVersion.MOCKK_CO}"
    const val KLUENT = "org.amshove.kluent:kluent:${TestLibraryVersion.KLUENT}"
    const val ANDROID_ARCH_CORE_TEST = "androidx.arch.core:core-testing:${TestLibraryVersion.ANDROIDX_ARCH_CORE_TEST}"
}