package com.example.instagram_clone_clean_architecture.feature.feeds.presentation.views.feeds

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.instagram_clone_clean_architecture.app.presentation.activity.MainActivity
import com.example.instagram_clone_clean_architecture.feature.feeds.databinding.FragmentFeedsBinding
import com.example.instagram_clone_clean_architecture.feature.feeds.presentation.adapters.FeedsAdapter
import com.example.instagram_clone_clean_architecture.feature.feeds.presentation.decorators.FeedViewItemDecorator
import com.example.library_base.presentation.fragment.InjectionFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.kodein.di.instance
import java.lang.ref.SoftReference

class FeedsFragment : InjectionFragment() {

    private val viewModel: FeedsViewModel by instance()

    private lateinit var feedsAdapterRef: SoftReference<FeedsAdapter>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFeedsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        setupFeedsAppbar(binding.feedsAppBar)

        setupFeedsAdapter(binding)
        setupFeedItemViewDecorator(binding)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val feedsAdapter = feedsAdapterRef.get()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getFeeds().collectLatest {
                feedsAdapter?.submitData(it)
            }
        }
    }

    private fun setupFeedsAppbar(appBar: Toolbar?) {
        (requireActivity() as MainActivity).setSupportActionBar(appBar)
    }

    private fun setupFeedsAdapter(binding: FragmentFeedsBinding) {
        val feedsAdapter = FeedsAdapter(FeedsAdapter.UserImageClickListener {
            viewModel.navigateToProfile(it)
        })
        feedsAdapterRef = SoftReference(feedsAdapter)
        binding.feedsContainer.adapter = feedsAdapter
    }

    private fun setupFeedItemViewDecorator(binding: FragmentFeedsBinding) {
        val decorator = FeedViewItemDecorator(10)
        binding.feedsContainer.addItemDecoration(decorator)
    }

}