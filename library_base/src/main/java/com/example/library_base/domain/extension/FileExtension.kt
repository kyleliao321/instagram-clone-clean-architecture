package com.example.library_base.domain.extension

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun File.asImageMultipartFormData(partName: String): MultipartBody.Part {
    val reqFile = this.asRequestBody("image/jpeg".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(partName, this.name, reqFile)
}