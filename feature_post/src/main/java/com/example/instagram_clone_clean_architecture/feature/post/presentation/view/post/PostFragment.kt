package com.example.instagram_clone_clean_architecture.feature.post.presentation.view.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.feature.post.databinding.FragmentPostBinding
import com.example.library_base.presentation.fragment.InjectionFragment
import org.kodein.di.instance
import timber.log.Timber

class PostFragment: InjectionFragment() {

    private val viewModel: PostViewModel by instance()

    private val observer = Observer<PostViewModel.ViewState>() {
        Timber.d(it.toString())
    }

    private lateinit var binding: FragmentPostBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.stateLiveData.observe(viewLifecycleOwner, observer)
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadData()
    }

}