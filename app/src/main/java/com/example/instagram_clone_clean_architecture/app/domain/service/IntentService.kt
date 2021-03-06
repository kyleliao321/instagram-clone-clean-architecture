package com.example.instagram_clone_clean_architecture.app.domain.service

import androidx.appcompat.app.AppCompatActivity
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either

interface IntentService {

    fun init(activity: AppCompatActivity)

    fun openCamera(): Either<Unit, Failure>

    fun openPhotoGallery(): Either<Unit, Failure>

    companion object {
        val CAMERA_SERVICE_CODE = 1

        val PHOTO_GALLERY_SERVICE_CODE = 2
    }

}