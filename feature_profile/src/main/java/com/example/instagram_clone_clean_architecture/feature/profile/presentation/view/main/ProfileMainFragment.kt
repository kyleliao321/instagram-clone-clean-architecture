package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.example.instagram_clone_clean_architecture.app.presentation.activity.MainActivity
import com.example.instagram_clone_clean_architecture.feature.profile.databinding.FragmentProfileMainBinding
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.adapters.UserPostGridViewAdapter
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

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupUserPostListAdapter(binding)
        setSupportAppBar(binding.mainProfileAppBar)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setSupportAppBar(null)
    }

    private fun setSupportAppBar(appBar: Toolbar?) {
        (requireActivity() as MainActivity).setSupportActionBar(appBar)
    }

    private fun setupUserPostListAdapter(binding: FragmentProfileMainBinding) {
        binding.userPostContainer.adapter =
            UserPostGridViewAdapter(
                UserPostGridViewAdapter.OnClickListener {
                    viewModel.onNavigateToPostDetail(it)
                })
    }
}