package com.example.instagram_clone_clean_architecture.feature.search.presentation.view.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.app.presentation.MainActivity
import com.example.instagram_clone_clean_architecture.feature.search.databinding.FragmentSearchBinding
import com.example.library_base.presentation.fragment.InjectionFragment
import org.kodein.di.instance
import timber.log.Timber

class SearchFragment : InjectionFragment() {

    private val viewModel : SearchViewModel by instance()

    private lateinit var binding: FragmentSearchBinding

    private val observer = Observer<SearchViewModel.ViewState> {
        Timber.d(it.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).setSupportActionBar(binding.searchFragmentAppBar)

        viewModel.stateLiveData.observe(viewLifecycleOwner, observer)
    }
}