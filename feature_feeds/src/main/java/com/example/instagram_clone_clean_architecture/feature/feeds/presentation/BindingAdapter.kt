package com.example.instagram_clone_clean_architecture.feature.feeds.presentation

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.api.load
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.example.instagram_clone_clean_architecture.feature.feeds.R

/**
 * Fetch post image from remote using Coil.
 *
 * @param data Url source or local drawable asset for image.
 */
@BindingAdapter("app:postImage")
fun loadPostImage(imageView: ImageView, data: String?) {
    data?.let {
        imageView.load(it) {
            crossfade(true)
            placeholder(R.drawable.image_loading_icon)
        }
    }
}

/**
 * Fetch user profile image from remote using Coil.
 *
 * @param original Url source or local drawable asset for original image.
 */
@BindingAdapter("app:userImage")
fun loadUserImage(imageView: ImageView, data: String?) = when (data) {
    null -> imageView.load(R.drawable.user_profile_default_image)
    else -> imageView.load(data) {
        transformations(CircleCropTransformation())
    }
}