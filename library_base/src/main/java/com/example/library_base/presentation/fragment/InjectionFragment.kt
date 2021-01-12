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

package com.example.library_base.presentation.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import org.kodein.di.DIAware
import org.kodein.di.DITrigger
import org.kodein.di.android.x.BuildConfig
import org.kodein.di.android.x.closestDI
import org.kodein.di.diContext

/**
 * Allowing all fragment classes to use DI module inside its scope.
 * (i.e: val viewModel: ExampleViewModel by instance() )
 */
abstract class InjectionFragment : Fragment(), DIAware {

    final override val di by closestDI()

    final override val diContext = diContext<Fragment>(this)

    final override val diTrigger: DITrigger? by lazy {
        if (BuildConfig.DEBUG) DITrigger() else super.diTrigger
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        diTrigger?.trigger()
    }
}