package com.example.instagram_clone_clean_architecture.app.presentation

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.instagram_clone_clean_architecture.R
import com.example.library_base.presentation.activity.InjectionActivity
import com.example.library_base.presentation.navigation.NavigationManager
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.instance
import timber.log.Timber

class MainActivity: InjectionActivity(R.layout.activity_main) {

    private val navigationManager: NavigationManager by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("Main Activity is created!")

        setNavEventListener()
    }

    private fun setNavEventListener() {
        navigationManager.setNavEventCallbackListener {
            appNavGraph.findNavController().navigate(it)
        }
    }
}