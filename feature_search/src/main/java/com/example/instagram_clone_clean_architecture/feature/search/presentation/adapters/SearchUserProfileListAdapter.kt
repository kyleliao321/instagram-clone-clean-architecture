package com.example.instagram_clone_clean_architecture.feature.search.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.search.databinding.FragmentSearchItemViewBinding

class SearchUserProfileListAdapter(
    private val onClickListener: SearchUserProfileListAdapter.OnClickListener
) : ListAdapter<UserDomainModel, SearchUserProfileListAdapter.ViewHolder>(DiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userProfile = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(userProfile)
        }

        holder.bind(userProfile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(private val binding: FragmentSearchItemViewBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(userProfile: UserDomainModel) {
            binding.userProfile = userProfile
        }

        companion object {
            fun from(parent: ViewGroup) : ViewHolder {
                return ViewHolder(FragmentSearchItemViewBinding.inflate(LayoutInflater.from(parent.context)))
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<UserDomainModel>() {
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

    class OnClickListener(val clickCallback: (userProfile: UserDomainModel) -> Unit) {
        fun onClick(userProfile: UserDomainModel) = clickCallback(userProfile)
    }

}