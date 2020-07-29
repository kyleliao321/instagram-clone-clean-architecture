package com.example.instagram_clone_clean_architecture.app.presentation

import android.os.Bundle
import com.example.instagram_clone_clean_architecture.R
import com.example.library_base.presentation.activity.InjectionActivity
import timber.log.Timber

class MainActivity: InjectionActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("Main Activity is created!")
    }
}