package com.example.library_base.presentation.navigation

import androidx.navigation.NavDirections

class NavigationManager {

    private lateinit var navEventCallbackListener: (NavDirections) -> Unit

    fun onNavEvent(navDir: NavDirections) {
        navEventCallbackListener(navDir)
    }

    fun setNavEventCallbackListener(callback: (NavDirections) -> Unit) {
        navEventCallbackListener = callback
    }
}