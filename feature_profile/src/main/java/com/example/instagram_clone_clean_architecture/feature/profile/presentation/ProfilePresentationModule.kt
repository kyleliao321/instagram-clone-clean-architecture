package com.example.instagram_clone_clean_architecture.feature.profile.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.instagram_clone_clean_architecture.feature.profile.MODULE_NAME
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.edit.ProfileEditViewModel
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.follower.ProfileFollowerViewModel
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.following.ProfileFollowingViewModel
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.main.ProfileMainFragmentArgs
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.main.ProfileMainViewModel
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.post.ProfilePostViewModel
import com.example.library_base.domain.utility.KotlinViewModelProvider
import org.kodein.di.*
import org.kodein.di.android.x.AndroidLifecycleScope

internal val presentationModule = DI.Module("${MODULE_NAME}PresentationModule") {

    bind<ProfileMainViewModel>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        KotlinViewModelProvider.of(context) {
            ProfileMainViewModel(
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance()
            )
        }
    }

    bind<ProfileEditViewModel>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        KotlinViewModelProvider.of(context) {
            ProfileEditViewModel(
                instance(),
                instance(),
                instance(),
                instance()
            )
        }
    }

    bind<ProfileFollowerViewModel>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        KotlinViewModelProvider.of(context) {
            ProfileFollowerViewModel(
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance()
            )
        }
    }

    bind<ProfileFollowingViewModel>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        KotlinViewModelProvider.of(context) {
            ProfileFollowingViewModel(
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance()
            )
        }
    }

    bind<ProfilePostViewModel>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        KotlinViewModelProvider.of(context) {
            ProfilePostViewModel(
                instance(),
                instance(),
                instance(),
                instance()
            )
        }
    }
}