package com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.follower

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.feature_profile.databinding.FragmentProfileFollowerBinding
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.adapters.UserProfileListViewAdapter
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.main.ProfileMainViewModel
import com.example.library_base.presentation.fragment.InjectionFragment
import org.kodein.di.instance
import timber.log.Timber

class ProfileFollowerFragment: InjectionFragment() {

    private val viewModel: ProfileFollowerViewModel by instance()

    private lateinit var binding: FragmentProfileFollowerBinding

    private val observer = Observer<ProfileFollowerViewModel.ViewState> {
        Timber.d(it.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileFollowerBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setFollowerListAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.stateLiveData.observe(viewLifecycleOwner, observer)

        viewModel.loadData()
    }

    private fun setFollowerListAdapter() {
        binding.userFollowerListContainer.adapter = UserProfileListViewAdapter(
            UserProfileListViewAdapter.OnClickListener {
                viewModel.onNavigateToUserProfile(it.userProfile)
            }
        )
    }

}