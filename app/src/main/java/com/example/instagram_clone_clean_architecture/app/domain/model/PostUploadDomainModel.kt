package com.example.instagram_clone_clean_architecture.app.domain.model

import android.net.Uri
import java.util.*

data class PostUploadDomainModel(
    var imageFile: Uri? = null,
    var description: String? = null,
    var location: String? = null,
    var date: Date? = null,
    var belongUserId: String? = null,
    var imageByteArray: ByteArray? = null
) {

    val isPostReady get() =
        imageFile != null && belongUserId != null

    val isPostUploadReady get() =
        imageByteArray != null && belongUserId != null && date != null

}