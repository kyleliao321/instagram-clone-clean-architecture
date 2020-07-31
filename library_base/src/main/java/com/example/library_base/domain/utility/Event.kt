package com.example.library_base.domain.utility

import androidx.lifecycle.Observer

/**
 * One-Time event object, for the event that should only trigger once.
 */
class Event<out T>(val data: T) {

    private var isHandled: Boolean = false

    fun getDataIfNotHandled(): T? {
        if (!isHandled) {
            isHandled = true
            return data
        }

        return null
    }
}

/**
 * The event observer that only callback if the given event is not handled by other observers.
 */
class EventObserver<T>(private val onEventCallback: (T) -> Unit) : Observer<Event<T>> {

    override fun onChanged(event: Event<T>) {
        event.getDataIfNotHandled()?.let {
            onEventCallback(it)
        }
    }
}