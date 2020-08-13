package com.example.instagram_clone_clean_architecture.feature.profile.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.feature_profile.databinding.FragmentProfileFollowUserViewItemBinding
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import org.jetbrains.annotations.NotNull
import timber.log.Timber

class UserProfileListViewAdapter(
    private val itemOnClickListener: OnClickListener<DataItem>,
    private val followButtonClickListener: OnClickListener<DataItem>,
    private val removeButtonClickListener: OnClickListener<DataItem>
) : ListAdapter<UserProfileListViewAdapter.DataItem, UserProfileListViewAdapter.UserProfileViewHolder>(DiffCallback)  {

    override fun onBindViewHolder(holder: UserProfileViewHolder, position: Int) {
        val dataItem = getItem(position)
        holder.itemView.setOnClickListener {
            itemOnClickListener.onClick(dataItem)
        }

        holder.bind(dataItem, followButtonClickListener, removeButtonClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserProfileViewHolder {
        return UserProfileViewHolder.from(parent)
    }

    class UserProfileViewHolder private constructor(
        private val binding: FragmentProfileFollowUserViewItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            dataItem: DataItem,
            followButtonClickListener: OnClickListener<DataItem>,
            removeButtonClickListener: OnClickListener<DataItem>
        ) {
            binding.dataItem = dataItem
            binding.followButton.setOnClickListener {
                followButtonClickListener.onClick(dataItem)
            }
            binding.removeFollowerButton.setOnClickListener {
                removeButtonClickListener.onClick(dataItem)
            }
        }

        companion object {
            fun from(parent: ViewGroup) : UserProfileViewHolder {
                val binding = FragmentProfileFollowUserViewItemBinding.inflate(LayoutInflater.from(parent.context))
                return UserProfileViewHolder(binding)
            }
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<DataItem>() {
        override fun areContentsTheSame(
            oldItem: DataItem,
            newItem: DataItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.userProfile.id == newItem.userProfile.id
        }
    }

    class OnClickListener<in CallbackParam>(private val clickCallback: (param: CallbackParam) -> Unit) {
        fun onClick(param: CallbackParam) = clickCallback(param)
    }

    /**
     * Since each follower-following relation view has two status: following, not-following
     * it is required to have two type of class to represent the status.
     */
    sealed class DataItem {
        abstract val userProfile : UserDomainModel
        abstract val isFollowingType : Boolean
        abstract val isCancelingType : Boolean

        data class FollowingItem(override val userProfile: UserDomainModel) : DataItem() {
            override val isFollowingType: Boolean
                get() = true
            override val isCancelingType: Boolean
                get() = false
        }

        data class CancelingType(override val userProfile: UserDomainModel) : DataItem() {
            override val isFollowingType: Boolean
                get() = false
            override val isCancelingType: Boolean
                get() = true
        }
    }
}