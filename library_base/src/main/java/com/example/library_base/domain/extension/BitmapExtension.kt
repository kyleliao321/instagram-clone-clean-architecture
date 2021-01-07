package com.example.library_base.domain.extension

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

fun Bitmap.getJpegByteArray(quality: Int = 100): ByteArray {
    val buffer = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, quality, buffer)

    val byteArray = buffer.toByteArray()
    buffer.flush()
    buffer.close()

    return byteArray
}

fun Bitmap.resizeAndCrop(newWidth: Int, newHeight: Int): Bitmap {
    return Bitmap.createScaledBitmap(this, newWidth, newHeight, false)
}