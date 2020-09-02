package com.example.instagram_clone_clean_architecture.feature.post.presentation

import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.api.load

@BindingAdapter("app:postImage")
fun loadPostImage(imageView: ImageView, bitmap: Bitmap?) {
    bitmap?.let {
        imageView.load(it) {
            crossfade(true)
        }
    }
}

/**
 * Show the view if provided @{condition} is met.
 *
 * @param condition Boolean value that indicate whether to allow the view to be visible or not.
 */
@BindingAdapter("app:visibleCondition")
fun visibleCondition(view: View, condition: Boolean) = when (condition) {
    true -> view.visibility = View.VISIBLE
    false -> view.visibility = View.GONE
}