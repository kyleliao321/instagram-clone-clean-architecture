package com.example.instagram_clone_clean_architecture.feature.profile.presentation

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.example.feature_profile.R
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.adapters.UserPostGridViewAdapter
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.adapters.UserProfileListViewAdapter
import timber.log.Timber

@BindingAdapter("app:userPostData")
fun bindPostRecyclerView(recyclerView: RecyclerView, data: List<PostDomainModel>) {
    val adapter = recyclerView.adapter as UserPostGridViewAdapter
    adapter.submitList(data)
}

//@BindingAdapter("app:userProfileList")
//fun bindUserProfileList(recyclerView: RecyclerView, data: List<UserDomainModel>) {
//    val adapter = recyclerView.adapter as UserProfileListViewAdapter
//    adapter.submitList(data)
//}

@BindingAdapter("app:followUserList", "app:compareList")
fun bindFollowUserWithCompareList(recyclerView: RecyclerView, data: List<UserDomainModel>, compare: List<UserDomainModel>) {
    val adapter = recyclerView.adapter as UserProfileListViewAdapter
    val dataItems = data
        .map {
            when (it) {
                in compare -> UserProfileListViewAdapter.DataItem.CancelingType(it)
                !in compare -> UserProfileListViewAdapter.DataItem.FollowingItem(it)
                else -> throw IllegalStateException("Condition should be exhausted")
            }
        }
    adapter.submitList(dataItems)
}

@BindingAdapter("app:postImage")
fun loadPostImage(imageView: ImageView, data: String?) {
    data?.let {
        imageView.load(it) {
            crossfade(true)
            placeholder(R.drawable.image_loading_icon)
        }
    }
}

@BindingAdapter("app:userImage")
fun loadUserImage(imageView: ImageView, data: String?) = when (data) {
    null -> imageView.load(R.drawable.user_profile_default_image)
    else -> imageView.load(data) {
        transformations(CircleCropTransformation())
    }
}

/**
 * Show the view if provided @{condition} is met.
 */
@BindingAdapter("app:visibleCondition")
fun visibleCondition(view: View, condition: Boolean) = when (condition) {
    true -> view.visibility = View.VISIBLE
    false -> view.visibility = View.GONE
}