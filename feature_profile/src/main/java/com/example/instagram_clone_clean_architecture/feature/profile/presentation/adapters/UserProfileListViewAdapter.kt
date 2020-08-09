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
    private val onClickListener: OnClickListener
) : ListAdapter<UserDomainModel, UserProfileListViewAdapter.UserProfileViewHolder>(DiffCallback)  {

    override fun onBindViewHolder(holder: UserProfileViewHolder, position: Int) {
        val userProfile = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(userProfile)
        }

        holder.bind(userProfile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserProfileViewHolder {
        return UserProfileViewHolder.from(parent)
    }

    class UserProfileViewHolder private constructor(
        private val binding: FragmentProfileFollowUserViewItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(userProfile: UserDomainModel) {
            binding.userProfile = userProfile
        }

        companion object {
            fun from(parent: ViewGroup) : UserProfileViewHolder {
                val binding = FragmentProfileFollowUserViewItemBinding.inflate(LayoutInflater.from(parent.context))
                return UserProfileViewHolder(binding)
            }
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<UserDomainModel>() {
        override fun areContentsTheSame(
            oldItem: UserDomainModel,
            newItem: UserDomainModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: UserDomainModel, newItem: UserDomainModel): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class OnClickListener(private val clickCallback: (userProfile: UserDomainModel) -> Unit) {
        fun onClick(userProfile: UserDomainModel) = clickCallback(userProfile)
    }
}