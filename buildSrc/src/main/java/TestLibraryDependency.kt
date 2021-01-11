
object TestLibraryVersion {
    const val KOTLIN_CO_TEST = "1.3.6"
    const val JUNIT = "4.12"
    const val MOCKK = "1.10.0"
    const val KLUENT = "1.61"
    const val ANDROIDX_ARCH_CORE_TEST = "2.1.0"
    const val MOCK_WEB_SERVER = "4.9.0"
    const val ANDROID_TEST = "1.1.0"
    const val KAKAO = "2.4.0"
    const val ESPRESSO = "3.1.0"
}

object TestLibraryDependency {
    const val KOTLIN_CO_TEST = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${TestLibraryVersion.KOTLIN_CO_TEST}"
    const val JUNIT = "junit:junit:${TestLibraryVersion.JUNIT}"
    const val MOCKK = "io.mockk:mockk:${TestLibraryVersion.MOCKK}"
    const val KOTLIN_CO_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${TestLibraryVersion.KOTLIN_CO_TEST}"
    const val KLUENT = "org.amshove.kluent:kluent:${TestLibraryVersion.KLUENT}"
    const val ANDROID_ARCH_CORE_TEST = "androidx.arch.core:core-testing:${TestLibraryVersion.ANDROIDX_ARCH_CORE_TEST}"
    const val MOCK_WEB_SERVER = "com.squareup.okhttp3:mockwebserver:${TestLibraryVersion.MOCK_WEB_SERVER}"

    const val ANDROID_TEST_RUNNER = "androidx.test:runner:${TestLibraryVersion.ANDROID_TEST}"
    const val ANDROID_TEST_RULES = "androidx.test:rules:${TestLibraryVersion.ANDROID_TEST}"
    const val MOCKK_ANDROID = "io.mockk:mockk-android:${TestLibraryVersion.MOCKK}"
    const val KAKAO = "com.agoda.kakao:kakao:${TestLibraryVersion.KAKAO}"
    const val ESPRESSO = "androidx.test.espresso:espresso-core:${TestLibraryVersion.ESPRESSO}"
}