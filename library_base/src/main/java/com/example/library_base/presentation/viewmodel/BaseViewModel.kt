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

package com.example.library_base.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber
import kotlin.properties.Delegates

/**
 * BaseViewModel provide reducer for state changes, so the fragment or activity won't have to
 * observe on every LiveData it contains. All the information within the ViewModel should be
 * contained inside ViewState and updated based on Action.
 *
 * Usage: Any ViewModel extends from BaseViewModel must defined its own ViewState and Action it can take.
 *        Then, it should override onReduceState function to indicate how the state should updated based
 *        on Action. When there's a need to update the state, using sendAction with Action argument.
 */
abstract class BaseViewModel<ViewState: BaseViewState, Action: BaseAction>(initialState: ViewState): ViewModel() {

    private val _state = MutableLiveData<ViewState>(initialState)
    val stateLiveData: LiveData<ViewState>
        get() = _state

    protected var state by Delegates.observable(initialState) { _, old, new ->
        if (old != new) {
            _state.value = new
        }
    }

    fun loadData() {
        onLoadData()
    }

    fun sendAction(action: Action) {
        state = onReduceState(action)
    }

    protected abstract fun onLoadData()

    protected abstract fun onReduceState(action: Action) : ViewState
}