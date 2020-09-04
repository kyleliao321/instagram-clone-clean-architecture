/*
 * MIT License
 * Copyright (c) 2019 Igor Wojda
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.example.instagram_clone_clean_architecture.app.domain.di

import com.example.instagram_clone_clean_architecture.BuildConfig
import timber.log.Timber

/**
 * By using consistent naming convention for all DI modules in feature modules,
 * we can use FeatureManager to collect all object by name, and apply them into
 * top-level DI Module.
 */
object FeatureManager {

    private const val featurePackagePrefix = "com.example.instagram_clone_clean_architecture.feature"

    private val featureModuleNames = BuildConfig.FEATURE_MODULE_NAMES

    val diModules = featureModuleNames
        .map { "${featurePackagePrefix}.$it.FeatureDIModule" }
        .map {
            try {
                Timber.i("DI Module loading $it")
                Class.forName(it).kotlin.objectInstance as DIModuleProvider
            } catch (e: ClassNotFoundException) {
                throw ClassNotFoundException("DI module class not found $it")
            }
        }
        .map { it.diModule }
}