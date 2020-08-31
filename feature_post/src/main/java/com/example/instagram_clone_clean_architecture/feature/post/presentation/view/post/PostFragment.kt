package com.example.instagram_clone_clean_architecture.feature.post.presentation.view.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.instagram_clone_clean_architecture.feature.post.databinding.FragmentPostBinding
import com.example.library_base.presentation.fragment.InjectionFragment
import org.kodein.di.instance

class PostFragment: InjectionFragment() {

    private val viewModel: PostViewModel by instance()

    private lateinit var binding: FragmentPostBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

}