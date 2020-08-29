package com.example.library_base.domain.utility

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class KotlinViewModelProvider private constructor() {

    companion object {
        inline fun <reified T : ViewModel> of(fragment: Fragment, crossinline factory: () -> T): T {
            @Suppress("UNCHECKED_CAST")
            val vmFactory = object : ViewModelProvider.Factory {
                override fun <U: ViewModel> create(modelClass: Class<U>): U = factory() as U
            }

            return ViewModelProvider(fragment, vmFactory)[T::class.java]
        }

        inline fun <reified T : ViewModel> of(activity: AppCompatActivity, crossinline factory: () -> T): T {
            @Suppress("UNCHECKED_CAST")
            val vmFactory = object : ViewModelProvider.Factory {
                override fun <U: ViewModel> create(modelClass: Class<U>): U = factory() as U
            }

            return ViewModelProvider(activity, vmFactory)[T::class.java]
        }
    }
}