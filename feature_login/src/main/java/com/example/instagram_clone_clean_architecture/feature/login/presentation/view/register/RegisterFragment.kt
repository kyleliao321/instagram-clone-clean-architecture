package com.example.instagram_clone_clean_architecture.feature.login.presentation.view.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.feature.login.R
import com.example.instagram_clone_clean_architecture.feature.login.databinding.FragmentRegisterBinding
import com.example.library_base.presentation.fragment.InjectionFragment
import com.example.library_base.presentation.navigation.NavigationManager
import com.google.android.material.snackbar.Snackbar
import org.kodein.di.instance
import timber.log.Timber

class RegisterFragment : InjectionFragment() {

    private val viewModel: RegisterViewModel by instance()

    private val observer = Observer<RegisterViewModel.ViewState>() {
        if (it.isRegisterFail) {
            showSnackBar(resources.getString(R.string.register_error_message))
        }

        if (it.isNetworkError) {
            showSnackBar(resources.getString(com.example.instagram_clone_clean_architecture.R.string.network_error_message))
        }
    }

    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.stateLiveData.observe(viewLifecycleOwner, observer)
    }

    private fun showSnackBar(message: String) {
        Snackbar
            .make(requireView(), message, Snackbar.LENGTH_SHORT)
            .show()
    }

}