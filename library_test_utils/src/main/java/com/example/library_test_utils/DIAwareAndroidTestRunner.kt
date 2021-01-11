package com.example.library_test_utils

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class DIAwareAndroidTestRunner: AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, DIAwareTestApplication::class.java.name, context)
    }
}