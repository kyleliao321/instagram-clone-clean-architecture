package com.example.library_base.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.library_base.domain.exception.Failure
import timber.log.Timber
import kotlin.properties.Delegates

/**
 * BaseViewModel provide reducer for state changes, so the fragment or activity won't have to
 * observe on every LiveData it contains. All the information within the ViewModel should be
 * contained inside ViewState and updated based on Action.
 *
 * Usage: Any ViewModel extends from BaseViewModel must defined its own ViewState and Action it can take.
 *        Then, it should override onReduceState function to indicate how the state should updated based
 *        on Action.
 *
 *        When there's a need to update the state, using sendAction with Action argument.
 */
abstract class BaseViewModel<ViewState: BaseViewState, Action: BaseAction>(initialState: ViewState): ViewModel() {

    private val _state = MutableLiveData<ViewState>(initialState)
    val stateLiveData: LiveData<ViewState>
        get() = _state

    protected var state by Delegates.observable(initialState) { _, _, new ->
        _state.value = new
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