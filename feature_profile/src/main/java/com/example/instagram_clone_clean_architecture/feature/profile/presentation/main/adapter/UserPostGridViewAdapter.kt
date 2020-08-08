package com.example.instagram_clone_clean_architecture.feature.profile.presentation.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.feature_profile.databinding.FragmentProfileMainUserPostsGridViewItemBinding
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.main.ProfileMainViewModel

class UserPostGridViewAdapter(
    private val onClickListener: OnClickListener
) : ListAdapter<PostDomainModel, UserPostGridViewAdapter.UserPostViewHolder>(DiffCallback) {

    override fun onBindViewHolder(holder: UserPostViewHolder, position: Int) {
        val post = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(post)
        }
        holder.bind(post)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPostViewHolder {
        return UserPostViewHolder.from(parent)
    }

    class UserPostViewHolder private constructor(
        private var binding: FragmentProfileMainUserPostsGridViewItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: PostDomainModel) {
            binding.post = post
        }

        companion object {
            fun from(parent: ViewGroup): UserPostViewHolder {
                val binding = FragmentProfileMainUserPostsGridViewItemBinding.inflate(
                    LayoutInflater.from(parent.context)
                )
                return UserPostViewHolder(binding)
            }
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<PostDomainModel>() {
        override fun areContentsTheSame(
            oldItem: PostDomainModel,
            newItem: PostDomainModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: PostDomainModel, newItem: PostDomainModel): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class OnClickListener(val clickCallback: (post: PostDomainModel) -> Unit) {
        fun onClick(post: PostDomainModel) = clickCallback(post)
    }
}