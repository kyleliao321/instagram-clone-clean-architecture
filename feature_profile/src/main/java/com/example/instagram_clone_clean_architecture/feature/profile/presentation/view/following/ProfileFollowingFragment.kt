package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.following

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.example.instagram_clone_clean_architecture.app.presentation.activity.MainActivity
import com.example.instagram_clone_clean_architecture.feature.profile.databinding.FragmentProfileFollowingBinding
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.adapters.UserProfileListViewAdapter
import com.example.library_base.presentation.fragment.InjectionFragment
import org.kodein.di.instance

class ProfileFollowingFragment: InjectionFragment() {

    private val viewModel: ProfileFollowingViewModel by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentProfileFollowingBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setSupportAppBar(binding.followingProfileAppBar)
        setupFollowingUserListAdapter(binding)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setSupportAppBar(null)
    }

    private fun setSupportAppBar(appBar: Toolbar?) {
        (requireActivity() as MainActivity).setSupportActionBar(appBar)
    }

    private fun setupFollowingUserListAdapter(binding: FragmentProfileFollowingBinding) {
        binding.userFollowingListContainer.adapter = UserProfileListViewAdapter(
            itemOnClickListener = UserProfileListViewAdapter.OnClickListener {
                viewModel.onNavigateToUserProfile(it.userProfile)
            },
            followButtonClickListener = UserProfileListViewAdapter.OnClickListener {
                viewModel.addUserRelation(it.userProfile)
            },
            removeButtonClickListener = UserProfileListViewAdapter.OnClickListener {
                viewModel.removeUserRelation(it.userProfile)
            }
        )
    }
}