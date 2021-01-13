package com.example.instagram_clone_clean_architecture.feature.feeds.presentation.views.feeds

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.instagram_clone_clean_architecture.feature.feeds.databinding.FragmentFeedsBinding
import com.example.instagram_clone_clean_architecture.feature.feeds.presentation.adapters.FeedsAdapter
import com.example.library_base.presentation.fragment.InjectionFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.kodein.di.instance

class FeedsFragment : InjectionFragment() {

    private val viewModel: FeedsViewModel by instance()

    private lateinit var feedsAdapter: FeedsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFeedsBinding.inflate(inflater, container, false)

        feedsAdapter = FeedsAdapter()
        binding.feedsContainer.adapter = feedsAdapter

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getFeeds().collectLatest {
                feedsAdapter.submitData(it)
            }
        }
    }

}