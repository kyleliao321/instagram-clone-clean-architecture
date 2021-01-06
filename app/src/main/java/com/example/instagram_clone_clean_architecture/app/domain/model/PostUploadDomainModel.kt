package com.example.instagram_clone_clean_architecture.app.domain.model

import android.net.Uri
import java.io.File
import java.util.*

data class PostUploadDomainModel(
    var imageUri: Uri? = null,
    var description: String? = null,
    var location: String? = null,
    var date: Date? = null,
    var belongUserId: String? = null,
    var imageByteArray: ByteArray? = null,
    var cachedImageFile: File? = null
) {

    val isPostReady get() =
        imageUri != null && belongUserId != null

    val isPostUploadReady get() =
        cachedImageFile != null && belongUserId != null && date != null

}