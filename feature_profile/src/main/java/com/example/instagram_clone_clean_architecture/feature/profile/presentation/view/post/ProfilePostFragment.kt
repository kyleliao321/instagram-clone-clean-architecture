package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.feature_profile.databinding.FragmentProfilePostBinding
import com.example.library_base.presentation.fragment.InjectionFragment
import org.kodein.di.instance
import timber.log.Timber

class ProfilePostFragment: InjectionFragment() {

    private val viewModel: ProfilePostViewModel by instance()

    private lateinit var binding: FragmentProfilePostBinding

    private val observer = Observer<ProfilePostViewModel.ViewState> {
        Timber.d(it.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfilePostBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.stateLiveData.observe(viewLifecycleOwner, observer)

        viewModel.loadData()
    }
}