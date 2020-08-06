package com.example.instagram_clone_clean_architecture.feature.profile.presentation.following

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.feature_profile.databinding.FragmentProfileFollowingBinding
import com.example.library_base.presentation.fragment.InjectionFragment

class ProfileFollowingFragment: InjectionFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentProfileFollowingBinding.inflate(inflater, container, false)
        return binding.root
    }
}