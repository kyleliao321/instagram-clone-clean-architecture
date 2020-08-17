package com.example.instagram_clone_clean_architecture.feature.search.presentation

import androidx.fragment.app.Fragment
import com.example.instagram_clone_clean_architecture.feature.search.MODULE_NAME
import com.example.instagram_clone_clean_architecture.feature.search.presentation.view.search.SearchViewModel
import com.example.library_base.domain.utility.KotlinViewModelProvider
import org.kodein.di.*
import org.kodein.di.android.x.AndroidLifecycleScope

val presentationModule = DI.Module("${MODULE_NAME}PresentationModule") {

    bind<SearchViewModel>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        KotlinViewModelProvider.of(context) { SearchViewModel(instance()) }
    }

}