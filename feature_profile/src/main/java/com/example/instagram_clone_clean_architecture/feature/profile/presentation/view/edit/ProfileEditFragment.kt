package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.feature_profile.databinding.FragmentProfileEditBinding
import com.example.library_base.presentation.fragment.InjectionFragment
import org.kodein.di.instance

class ProfileEditFragment: InjectionFragment() {

    private val viewModel: ProfileEditViewModel by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        return binding.root
    }
}