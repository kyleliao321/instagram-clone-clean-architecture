package com.example.library_base.domain.extension

import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.ref.WeakReference

/**
 * Extension function to allow client-side can custom the navigation logic when BottomNavigationView's
 * menu item selected, also synchronize the selected item with actual destination.
 *
 * @param navHandler Handle the logic of navigation when menu-item is selected.
 */
fun BottomNavigationView.setupNavControllerWithNavCallback(navController: NavController, navHandler: (menuItem: MenuItem) -> Boolean) {
    this.setOnNavigationItemSelectedListener {
        navHandler(it)
    }

    // Using weak reference to avoid memory leak
    val viewRef = WeakReference<BottomNavigationView>(this)
    val listener = object : NavController.OnDestinationChangedListener {
        override fun onDestinationChanged(
            controller: NavController,
            destination: NavDestination,
            arguments: Bundle?
        ) {
            val view = viewRef.get()
            if (view == null) {
                controller.removeOnDestinationChangedListener(this)
                return
            }

            val menu = view.menu
            for (i in 0 until menu.size()) {
                val item = menu.getItem(i)
                if (matchDestination(destination, item.itemId)) {
                    item.isChecked = true
                }
            }
        }
    }
    navController.addOnDestinationChangedListener(listener)
}

/**
 * Determine whether destination is or nested inside given destination id.
 */
inline fun matchDestination(destination: NavDestination, destId: Int) : Boolean {
    var currentDest = destination
    while (currentDest.id != destId && currentDest.parent != null) {
        currentDest = currentDest.parent!!
    }
    return currentDest.id == destId
}
