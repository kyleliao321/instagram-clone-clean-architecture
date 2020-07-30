package com.example.instagram_clone_clean_architecture.feature.profile.data

import com.example.instagram_clone_clean_architecture.feature.profile.MODULE_NAME
import com.example.instagram_clone_clean_architecture.feature.profile.data.repository.MockProfileRepositoryImpl
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

internal val dataModule = DI.Module("${MODULE_NAME}DataModule") {

    bind<ProfileRepository>() with singleton { MockProfileRepositoryImpl() }

}