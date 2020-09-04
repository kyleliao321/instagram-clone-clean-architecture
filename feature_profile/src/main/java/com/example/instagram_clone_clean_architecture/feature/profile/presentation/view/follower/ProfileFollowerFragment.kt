package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.follower

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.feature.profile.databinding.FragmentProfileFollowerBinding
import com.example.instagram_clone_clean_architecture.app.presentation.activity.MainActivity
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.adapters.UserProfileListViewAdapter
import com.example.library_base.presentation.fragment.InjectionFragment
import org.kodein.di.instance
import timber.log.Timber

class ProfileFollowerFragment: InjectionFragment() {

    private val viewModel: ProfileFollowerViewModel by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentProfileFollowerBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setFollowerListAdapter(binding)

        setSupportAppBar(binding.followerProfileAppBar)

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

    private fun setFollowerListAdapter(binding: FragmentProfileFollowerBinding) {
        binding.userFollowerListContainer.adapter = UserProfileListViewAdapter(
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