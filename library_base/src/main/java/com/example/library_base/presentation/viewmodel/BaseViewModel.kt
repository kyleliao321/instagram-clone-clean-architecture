package com.example.library_base.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.library_base.domain.exception.Failure
import timber.log.Timber

abstract class BaseViewModel: ViewModel() {

    private val _failure = MutableLiveData<Failure>()
    val failure: LiveData<Failure>
        get() = _failure

    protected fun onFailure(failure: Failure) {
        _failure.value = failure
    }

    /**
     * Debug usage. Checking whether the dependency injection works or not.
     */
    final fun trigger() {
        Timber.d("ViewModel create successfully!")
    }
}