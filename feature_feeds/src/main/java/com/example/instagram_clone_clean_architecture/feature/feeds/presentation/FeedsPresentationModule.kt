package com.example.instagram_clone_clean_architecture.feature.feeds.presentation

import androidx.fragment.app.Fragment
import com.example.instagram_clone_clean_architecture.feature.feeds.MODULE_NAME
import com.example.instagram_clone_clean_architecture.feature.feeds.presentation.views.feeds.FeedsViewModel
import com.example.library_base.domain.utility.KotlinViewModelProvider
import org.kodein.di.*
import org.kodein.di.android.x.AndroidLifecycleScope

val presentationModule = DI.Module("${MODULE_NAME}PresentationModule") {
    bind<FeedsViewModel>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        KotlinViewModelProvider.of(context) {
            FeedsViewModel(
                instance(),
                instance()
            )
        }
    }
}