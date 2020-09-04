package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.R
import com.example.instagram_clone_clean_architecture.feature.profile.databinding.FragmentProfilePostBinding
import com.example.instagram_clone_clean_architecture.app.presentation.activity.MainActivity
import com.example.library_base.presentation.fragment.InjectionFragment
import com.google.android.material.snackbar.Snackbar
import org.kodein.di.instance
import timber.log.Timber

class ProfilePostFragment: InjectionFragment() {

    private val viewModel: ProfilePostViewModel by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentProfilePostBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        (requireActivity() as MainActivity).setSupportActionBar(binding.postProfileAppBar)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as MainActivity).setSupportActionBar(null)
    }
}