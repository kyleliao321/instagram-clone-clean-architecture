package com.example.instagram_clone_clean_architecture.feature.profile.presentation.follower

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.feature_profile.databinding.FragmentProfileFollowerBinding
import com.example.library_base.presentation.fragment.InjectionFragment

class ProfileFollowerFragment: InjectionFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentProfileFollowerBinding.inflate(inflater, container, false)
        return binding.root
    }

}