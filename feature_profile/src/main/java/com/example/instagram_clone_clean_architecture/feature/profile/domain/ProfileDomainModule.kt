package com.example.instagram_clone_clean_architecture.feature.profile.domain

import com.example.instagram_clone_clean_architecture.feature.profile.MODULE_NAME
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.*
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

internal val domainModule = DI.Module("${MODULE_NAME}DomainModule") {
    
    bind() from singleton { GetLoginUserUseCase(instance()) }

    bind() from singleton { GetUserProfileUseCase(instance()) }

    bind() from singleton { GetUserPostUseCase(instance()) }

    bind() from singleton { GetFollowingUserUseCase(instance()) }

    bind() from singleton { GetFollowerUserUseCase(instance()) }

    bind() from singleton { GetPostUseCase(instance()) }

    bind() from singleton { GetLikedUsersUseCase(instance()) }

    bind() from singleton { UpdateUserProfileUseCase(instance()) }

    bind() from singleton { AddUserRelationUseCase(instance()) }

    bind() from singleton { RemoveUserRelationUseCase(instance()) }

    bind() from singleton { UserLikePostUseCase(instance()) }

    bind() from singleton { UserUnlikePostUseCase(instance()) }

    bind() from singleton { NavigationUseCase(instance()) }

    bind() from singleton { LogoutUseCase(instance()) }

    bind() from singleton { ConsumeUserSelectedImageUseCase(instance()) }

    bind() from singleton { GetBitmapUseCase(instance()) }
}