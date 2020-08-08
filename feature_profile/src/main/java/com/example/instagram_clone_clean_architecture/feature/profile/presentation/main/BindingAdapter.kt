package com.example.instagram_clone_clean_architecture.feature.profile.presentation.main

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.main.adapter.UserPostGridViewAdapter

@BindingAdapter("app:listData")
fun bindPostRecyclerView(recyclerView: RecyclerView, data: List<PostDomainModel>) {
    val adapter = recyclerView.adapter as UserPostGridViewAdapter
    adapter.submitList(data)
}