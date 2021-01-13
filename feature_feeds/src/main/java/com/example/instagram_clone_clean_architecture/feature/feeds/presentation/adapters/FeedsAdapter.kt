package com.example.instagram_clone_clean_architecture.feature.feeds.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.feature.feeds.databinding.FragmentFeedsItemViewBinding

class FeedsAdapter : PagingDataAdapter<PostDomainModel, FeedsAdapter.FeedViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        return FeedViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

    class FeedViewHolder private constructor(
        private val binding: FragmentFeedsItemViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: PostDomainModel?) {
            binding.post = post
        }

        companion object {
            fun from(parent: ViewGroup): FeedViewHolder {
                val binding =
                    FragmentFeedsItemViewBinding.inflate(LayoutInflater.from(parent.context))
                return FeedViewHolder(binding)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<PostDomainModel>() {
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
}