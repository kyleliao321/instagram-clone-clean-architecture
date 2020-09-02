package com.example.instagram_clone_clean_architecture.app.presentation.service

import android.content.Intent
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.example.instagram_clone_clean_architecture.app.domain.service.IntentService
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import java.lang.ref.WeakReference

class IntentServiceImpl: IntentService {

    private lateinit var activityContext: WeakReference<AppCompatActivity>

    override fun init(activity: AppCompatActivity) {
        activityContext = WeakReference(activity)
    }

    override fun openCamera() : Either<Unit, Failure> {
        val context = activityContext.get()
            ?: throw IllegalStateException("$activityContext cannot be resolved")

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            try {
                context.startActivityForResult(intent, IntentService.CAMERA_SERVICE_CODE)
            } catch (e: Exception) {
                return Either.Failure(Failure.CameraServiceFail)
            }
        }

        return Either.Success(Unit)
    }

    override fun openPhotoGallery() : Either<Unit, Failure> {
        val context = activityContext.get()
            ?: throw IllegalStateException("$activityContext cannot be resolved")

        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { intent ->
            try {
                context.startActivityForResult(intent, IntentService.PHOTO_GALLERY_SERVICE_CODE)
            } catch (e: Exception) {
                return Either.Failure(Failure.PhotoGalleryServiceFail)
            }
        }

        return Either.Success(Unit)
    }
}