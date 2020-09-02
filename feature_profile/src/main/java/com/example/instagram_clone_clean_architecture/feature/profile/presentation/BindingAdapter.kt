package com.example.instagram_clone_clean_architecture.feature.profile.presentation

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.example.instagram_clone_clean_architecture.feature.profile.R
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.adapters.UserPostGridViewAdapter
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.adapters.UserProfileListViewAdapter
import timber.log.Timber

/**
 * Submit the list of post to recycler view's adapter
 *
 * @param data List of post.
 */
@BindingAdapter("app:userPostData")
fun bindPostRecyclerView(recyclerView: RecyclerView, data: List<PostDomainModel>) {
    val adapter = recyclerView.adapter as? UserPostGridViewAdapter
    if (adapter == null) {
        throw IllegalArgumentException("Given recycler view should implement ${UserPostGridViewAdapter.javaClass} as adapter")
    } else {
        adapter.submitList(data)
    }
}

/**
 * User for checking how to show the userProfile view item in following and follower list view.
 *
 * Although this behaviour can be done by allowing each view to call userProfile.getType(...),
 * it will have to do data manipulation for N times. In contrast, by implementing comparing logic on
 * recyclerView level, it can reduce amount of CPU cycles for manipulation.
 *
 * @param data Actual list of user profile that will be shown on screen.
 * @param compare List of use profile for checking whether the item of @{data} is inside it.
 * @param loginUser Currently login user, to check whether the item of @{data} is user it-self.
 */
@BindingAdapter("app:followUserList", "app:compareList", "app:loginUser")
fun bindFollowUserWithCompareList(recyclerView: RecyclerView, data: List<UserDomainModel>, compare: List<UserDomainModel>, loginUser: UserDomainModel?) {
    val adapter = recyclerView.adapter as? UserProfileListViewAdapter
    val dataItems = data
        .map {
            if (it == loginUser) {
                UserProfileListViewAdapter.DataItem.GoneType(it)
            } else {
                when (it) {
                    in compare -> UserProfileListViewAdapter.DataItem.CancelingType(it)
                    !in compare -> UserProfileListViewAdapter.DataItem.FollowingItem(it)
                    else -> throw IllegalStateException("Condition should be exhausted")
                }
            }
        }

    if (adapter == null) {
        throw IllegalArgumentException("Given recycler view should implement ${UserProfileListViewAdapter.javaClass} as adapter")
    } else {
        adapter.submitList(dataItems)
    }
}

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

/**
 * Load user profile image for EditFragment.
 */
@BindingAdapter("app:oldUserImage", "app:newUserImage")
fun loadUserImage(imageView: ImageView, original: String?, new: Bitmap?) {
    if (new == null) {
        when (original) {
            null -> imageView.load(R.drawable.user_profile_default_image)
            else -> imageView.load(original) {
                transformations(CircleCropTransformation())
            }
        }
    } else {
        // TODO: Fail to load bitmap
        imageView.load(new) {
            transformations(CircleCropTransformation())
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