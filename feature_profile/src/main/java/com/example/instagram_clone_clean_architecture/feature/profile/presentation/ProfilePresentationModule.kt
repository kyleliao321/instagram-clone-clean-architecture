package com.example.instagram_clone_clean_architecture.feature.profile.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.instagram_clone_clean_architecture.feature.profile.MODULE_NAME
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.edit.ProfileEditViewModel
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.follower.ProfileFollowerViewModel
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.following.ProfileFollowingViewModel
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.main.ProfileMainFragmentArgs
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.main.ProfileMainViewModel
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.post.ProfilePostViewModel
import com.example.library_base.domain.utility.KotlinViewModelProvider
import org.kodein.di.*
import org.kodein.di.android.x.AndroidLifecycleScope

internal val presentationModule = DI.Module("${MODULE_NAME}PresentationModule") {

    bind<ProfileMainViewModel>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        KotlinViewModelProvider.of(context) { ProfileMainViewModel(instance(), instance(), instance(), instance()) }
    }

    bind<ProfileEditViewModel>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        KotlinViewModelProvider.of(context) { ProfileEditViewModel(instance(), instance(), instance(), instance()) }
    }

    bind<ProfileFollowerViewModel>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        KotlinViewModelProvider.of(context) { ProfileFollowerViewModel(instance(), instance(), instance()) }
    }

    bind<ProfileFollowingViewModel>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        KotlinViewModelProvider.of(context) { ProfileFollowingViewModel(instance(), instance(), instance()) }
    }

    bind<ProfilePostViewModel>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        KotlinViewModelProvider.of(context) { ProfilePostViewModel(instance(), instance()) }
    }

    /**
     * Mock fragment arguments. This should be deleted when the entire data flow is setup.
     */
    bind<ProfileMainFragmentArgs>() with provider { ProfileMainFragmentArgs.Builder(1).build() }
}