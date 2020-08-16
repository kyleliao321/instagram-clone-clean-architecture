package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.feature_profile.databinding.FragmentProfileMainBinding
import com.example.instagram_clone_clean_architecture.app.presentation.MainActivity
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.adapters.UserPostGridViewAdapter
import com.example.library_base.presentation.fragment.InjectionFragment
import org.kodein.di.instance
import timber.log.Timber

class ProfileMainFragment: InjectionFragment() {

    private val viewModel: ProfileMainViewModel by instance()

    private lateinit var binding: FragmentProfileMainBinding

    private val observer = Observer<ProfileMainViewModel.ViewState> {
        Timber.d(it.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileMainBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupUserPostListAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).setSupportActionBar(binding.mainProfileAppBar)

        viewModel.stateLiveData.observe(viewLifecycleOwner, observer)

        viewModel.loadData()
    }

    private fun setupUserPostListAdapter() {
        binding.userPostContainer.adapter =
            UserPostGridViewAdapter(
                UserPostGridViewAdapter.OnClickListener {
                    viewModel.onNavigateToPostDetail(it)
                })
    }
}