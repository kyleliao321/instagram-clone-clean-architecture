package com.example.instagram_clone_clean_architecture.feature.search.presentation.view.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.instagram_clone_clean_architecture.feature.search.databinding.FragmentSearchBinding
import com.example.library_base.presentation.fragment.InjectionFragment
import timber.log.Timber

class SearchFragment : InjectionFragment() {

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("${this::class.java} is created!")
    }
}