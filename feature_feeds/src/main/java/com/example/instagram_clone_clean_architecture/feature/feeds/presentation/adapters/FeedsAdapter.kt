package com.example.instagram_clone_clean_architecture.feature.feeds.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram_clone_clean_architecture.app.domain.model.FeedDomainModel
import com.example.instagram_clone_clean_architecture.feature.feeds.databinding.FragmentFeedsItemViewBinding

class FeedsAdapter(
    private val userImageClickListener: UserImageClickListener
) : PagingDataAdapter<FeedDomainModel, FeedsAdapter.FeedViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        return FeedViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val feed = getItem(position)
        holder.bind(feed, userImageClickListener)
    }

    class FeedViewHolder private constructor(
        private val binding: FragmentFeedsItemViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(feed: FeedDomainModel?, imageClickListener: UserImageClickListener) {
            binding.feed = feed
            binding.postUserImage.setOnClickListener {
                feed?.let {
                    imageClickListener.onClick(it.userId)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): FeedViewHolder {
                val binding =
                    FragmentFeedsItemViewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return FeedViewHolder(binding)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<FeedDomainModel>() {
        override fun areContentsTheSame(
            oldItem: FeedDomainModel,
            newItem: FeedDomainModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: FeedDomainModel, newItem: FeedDomainModel): Boolean {
            return oldItem.postId == newItem.postId
        }
    }

    class UserImageClickListener(val callback: (userId: String) -> Unit) {
        fun onClick(userId: String) = callback(userId)
    }
}