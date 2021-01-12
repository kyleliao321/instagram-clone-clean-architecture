package com.example.instagram_clone_clean_architecture.feature.search.presentation.view.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.example.instagram_clone_clean_architecture.app.presentation.activity.MainActivity
import com.example.instagram_clone_clean_architecture.feature.search.databinding.FragmentSearchBinding
import com.example.instagram_clone_clean_architecture.feature.search.presentation.adapters.SearchUserProfileListAdapter
import com.example.library_base.presentation.fragment.InjectionFragment
import org.kodein.di.instance

class SearchFragment : InjectionFragment() {

    private val viewModel: SearchViewModel by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupRecyclerViewAdapter(binding)
        setSupportAppBar(binding.searchFragmentAppBar)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setSupportAppBar(null)
    }

    private fun setSupportAppBar(appBar: Toolbar?) {
        val parentActivity = requireActivity() as? MainActivity
        parentActivity?.let {
            it.setSupportActionBar(appBar)
        }
    }

    private fun setupRecyclerViewAdapter(binding: FragmentSearchBinding) {
        binding.searchResultContainer.adapter = SearchUserProfileListAdapter(
            SearchUserProfileListAdapter.OnClickListener {
                viewModel.onNavigateToProfileFeature(it.id)
            }
        )
    }
}