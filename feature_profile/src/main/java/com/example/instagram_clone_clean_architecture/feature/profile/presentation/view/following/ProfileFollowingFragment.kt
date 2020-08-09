package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.following

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.feature_profile.databinding.FragmentProfileFollowingBinding
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.adapters.UserProfileListViewAdapter
import com.example.library_base.presentation.fragment.InjectionFragment
import org.kodein.di.instance
import timber.log.Timber

class ProfileFollowingFragment: InjectionFragment() {

    private val viewModel: ProfileFollowingViewModel by instance()

    private lateinit var binding: FragmentProfileFollowingBinding

    private val observer = Observer<ProfileFollowingViewModel.ViewState> {
        Timber.d(it.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileFollowingBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupFollowingUserListAdapter()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.stateLiveData.observe(viewLifecycleOwner, observer)

        viewModel.loadData()
    }

    private fun setupFollowingUserListAdapter() {
        binding.userFollowingListContainer.adapter = UserProfileListViewAdapter(
            UserProfileListViewAdapter.OnClickListener {
                viewModel.onNavigateToUserProfile(it)
            }
        )
    }
}