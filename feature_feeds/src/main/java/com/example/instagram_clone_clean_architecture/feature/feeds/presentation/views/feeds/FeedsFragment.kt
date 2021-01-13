package com.example.instagram_clone_clean_architecture.feature.feeds.presentation.views.feeds

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.instagram_clone_clean_architecture.feature.feeds.databinding.FragmentFeedsBinding
import com.example.library_base.presentation.fragment.InjectionFragment

class FeedsFragment: InjectionFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFeedsBinding.inflate(inflater, container, false)
        return binding.root
    }
}