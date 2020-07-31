package com.example.instagram_clone_clean_architecture.feature.profile.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.instagram_clone_clean_architecture.feature.profile.MODULE_NAME
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.main.ProfileMainViewModel
import com.example.library_base.domain.utility.KotlinViewModelProvider
import org.kodein.di.*
import org.kodein.di.android.x.AndroidLifecycleScope

internal val presentationModule = DI.Module("${MODULE_NAME}PresentationModule") {

    bind<ProfileMainViewModel>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        KotlinViewModelProvider.of(context) { ProfileMainViewModel(instance(), instance(), instance()) }
    }
}