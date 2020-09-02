package com.example.instagram_clone_clean_architecture.feature.post.presentation

import androidx.fragment.app.Fragment
import com.example.instagram_clone_clean_architecture.feature.post.MODULE_NAME
import com.example.instagram_clone_clean_architecture.feature.post.presentation.view.post.PostViewModel
import com.example.library_base.domain.utility.KotlinViewModelProvider
import org.kodein.di.*
import org.kodein.di.android.x.AndroidLifecycleScope

val presentationModule = DI.Module("${MODULE_NAME}PresentationModule") {

    bind<PostViewModel>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        KotlinViewModelProvider.of(context) {
            PostViewModel(
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance()
            )
        }
    }

}