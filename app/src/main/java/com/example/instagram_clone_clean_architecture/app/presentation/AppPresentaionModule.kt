package com.example.instagram_clone_clean_architecture.app.presentation

import androidx.appcompat.app.AppCompatActivity
import com.example.instagram_clone_clean_architecture.app.MODULE_NAME
import com.example.library_base.domain.utility.KotlinViewModelProvider
import org.kodein.di.*
import org.kodein.di.android.x.AndroidLifecycleScope

val presentationModule = DI.Module("${MODULE_NAME}PresentationModule") {

    bind<MainViewModel>() with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        KotlinViewModelProvider.of(context) {
            MainViewModel(
                instance(),
                instance()
            )
        }
    }

}