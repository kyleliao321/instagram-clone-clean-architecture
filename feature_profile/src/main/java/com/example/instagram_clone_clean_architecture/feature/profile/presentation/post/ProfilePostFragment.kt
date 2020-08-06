package com.example.instagram_clone_clean_architecture.feature.profile.presentation.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.feature_profile.databinding.FragmentProfilePostBinding
import com.example.library_base.presentation.fragment.InjectionFragment
import org.kodein.di.instance

class ProfilePostFragment: InjectionFragment() {

    private val viewModel: ProfilePostViewModel by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentProfilePostBinding.inflate(inflater, container, false)
        return binding.root
    }
}