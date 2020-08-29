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

    private val viewModel: MainViewModel by instance()

    fun loadData() {
        viewModel.loadData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("Main Activity is created!")

        setNavEventListener()
        setBottomNavigationController()
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    private fun setNavEventListener() {
        navigationManager.setNavEventCallbackListener {
            try {
                navController.navigate(it)
            } catch (exception: Exception) {
                // Safe-Args cannot resolve navigation between features.
                // Thus, we must catch the exception when it cannot find destination,
                // and use the traditional way to do navigation.
                // By assigning destination id as action id, we can extract required information
                // from safe-args-generated navDir.
                if (exception is java.lang.IllegalArgumentException) {
                    navController.navigate(it.actionId, it.arguments)
                } else {
                    throw exception
                }
            }
        }
    }

    // TODO: Hide bottom navigation when user is in login feature
    private fun setBottomNavigationController() {
        bottomNav.selectedItemId = R.id.featureSearchNavGraph

        bottomNav.setupNavControllerWithNavCallback(navController) setupNavControllerWithCallback@{
            when (it.itemId) {
                R.id.featureSearchNavGraph -> {
                    viewModel.onNavigateToSearch()
                    return@setupNavControllerWithCallback true
                }
                R.id.featureProfileNavGraph -> {
                    viewModel.onNavigateToProfile()
                    return@setupNavControllerWithCallback true
                }
                else -> throw IllegalArgumentException("Unknown destination ${it.itemId}")
            }
        }
    }
}