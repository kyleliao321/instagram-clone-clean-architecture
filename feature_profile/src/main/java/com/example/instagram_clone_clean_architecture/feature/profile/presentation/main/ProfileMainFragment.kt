package com.example.instagram_clone_clean_architecture.feature.profile.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.feature_profile.databinding.FragmentProfileMainBinding
import com.example.library_base.presentation.fragment.InjectionFragment
import org.kodein.di.instance

class ProfileMainFragment: InjectionFragment() {

    private val viewModel: ProfileMainViewModel by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentProfileMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.trigger()
    }
}