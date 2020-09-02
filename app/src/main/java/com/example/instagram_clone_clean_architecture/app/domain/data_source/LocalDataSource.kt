package com.example.instagram_clone_clean_architecture.app.domain.data_source

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import java.io.File

interface LocalDataSource {

    fun init(context: Context)

    suspend fun getLocalLoginUserId(): Either<Int, Failure>

    suspend fun updateLocalLoginUserId(userId: Int?): Either<Unit, Failure>

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

    /**
     * Load image file into loaded-temporary-image.
     */
    suspend fun loadImage(image: Uri?): Either<Unit, Failure>

    /**
     * Consume previously loaded image.
     * The function should clean-up the loaded-temporary-image before return.
     */
    suspend fun consumeLoadedImage(): Either<Uri?, Failure>
}