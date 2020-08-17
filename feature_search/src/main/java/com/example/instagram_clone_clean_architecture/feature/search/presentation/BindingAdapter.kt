package com.example.instagram_clone_clean_architecture.feature.search.presentation

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.search.R
import com.example.instagram_clone_clean_architecture.feature.search.presentation.adapters.SearchUserProfileListAdapter
import timber.log.Timber

/**
 * Fetch user profile image from remote using Coil.
 *
 * @param data Url source or local drawable asset for image.
 */
@BindingAdapter("app:userImage")
fun loadUserImage(imageView: ImageView, data: String?) = when (data) {
    null -> imageView.load(R.drawable.user_profile_default_image)
    else -> imageView.load(data) {
        transformations(CircleCropTransformation())
    }
}

/**
 * Submit searching result list to list adapter.
 */
@BindingAdapter("app:searchResultList")
fun bindSearchResultList(recyclerView: RecyclerView, data: List<UserDomainModel>) {
    val adapter = recyclerView.adapter as? SearchUserProfileListAdapter
    adapter?.let {
        it.submitList(data)
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