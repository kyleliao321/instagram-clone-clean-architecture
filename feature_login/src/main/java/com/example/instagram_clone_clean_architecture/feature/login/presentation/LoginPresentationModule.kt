package com.example.instagram_clone_clean_architecture.feature.login.presentation

import androidx.fragment.app.Fragment
import com.example.instagram_clone_clean_architecture.feature.login.MODULE_NAME
import com.example.instagram_clone_clean_architecture.feature.login.presentation.view.login.LoginViewModel
import com.example.instagram_clone_clean_architecture.feature.login.presentation.view.register.RegisterViewModel
import com.example.library_base.domain.utility.KotlinViewModelProvider
import org.kodein.di.*
import org.kodein.di.android.x.AndroidLifecycleScope

val presentationModule = DI.Module("${MODULE_NAME}PresentationModule") {

    bind<LoginViewModel>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        KotlinViewModelProvider.of(context) {
            LoginViewModel(
                instance(),
                instance(),
                instance()
            )
        }
    }

    bind<RegisterViewModel>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        KotlinViewModelProvider.of(context) {
            RegisterViewModel(
                instance(),
                instance()
            )
        }
    }
}