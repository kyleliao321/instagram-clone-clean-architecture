package com.example.instagram_clone_clean_architecture.app.domain.data_source

import android.net.Uri
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either

interface CacheDataSource {

    // When prompt user to choose image either for avatar or post, the activity/fragment will
    // paused and await for result. So, in order to provide user a select-and-return-experience,
    // the application divides the process into three steps:
    //      1. Prompt user to select/take photo either from fragment or activity, and the activity/
    //         fragment will pause to wait for return.
    //      2. After return from camera/gallery, the `onActivityResult` of activity should be triggered,
    //         it is this point we call `loadImage` through dedicated UseCase to cache file in LocalDataSource.
    //      3. After `onActivityResult`, activity/fragment will resume. So, by calling `loadImage` through
    //         dedicated UseCase in `onStart` can return cache image in LocalDataSource.
    //         (If there's no loaded-image, just allow user to select again.)

    suspend fun cacheUserSelectedImageUri(uri: Uri) : Either<Unit, Failure>

    suspend fun consumeCachedSelectedImageUri() : Either<Uri, Failure>

}