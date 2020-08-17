package com.example.instagram_clone_clean_architecture.app.presentation

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.instagram_clone_clean_architecture.R
import com.example.library_base.domain.extension.setupNavControllerWithNavCallback
import com.example.library_base.presentation.activity.InjectionActivity
import com.example.library_base.presentation.navigation.NavigationManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.instance
import timber.log.Timber
import java.lang.ref.WeakReference

class MainActivity: InjectionActivity(R.layout.activity_main) {

    private val navController get() = appNavGraph.findNavController()

    private val navigationManager: NavigationManager by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("Main Activity is created!")

        setBottomNavigationController()
        setNavEventListener()
    }

    fun navigateToProfile(userId: Int) {
        // TODO: safe-args cannot resolve navigation direction when it is in separate module.
        val args = Bundle()
        args.putInt("userId", userId)
        navController.navigate(
            R.id.featureProfileNavGraph,
            args
        )
    }

    private fun setNavEventListener() {
        navigationManager.setNavEventCallbackListener {
            navController.navigate(it)
        }
    }

    private fun setBottomNavigationController() {
        bottomNav.selectedItemId = R.id.featureSearchNavGraph

        bottomNav.setupNavControllerWithNavCallback(navController) setupNavControllerWithCallback@{
            when (it.itemId) {
                R.id.featureSearchNavGraph -> {
                    navController.navigate(R.id.featureSearchNavGraph)
                    return@setupNavControllerWithCallback true
                }
                R.id.featureProfileNavGraph -> {
                    // TODO: safe-args cannot resolve navigation direction when it is in separate module.
                    val args = Bundle()
                    args.putInt("userId", 1)
                    navController.navigate(R.id.featureProfileNavGraph, args)
                    return@setupNavControllerWithCallback true
                }
                else -> throw IllegalArgumentException("Unknown destination ${it.itemId}")
            }
        }
    }
}