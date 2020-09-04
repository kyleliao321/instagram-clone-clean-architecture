package com.example.instagram_clone_clean_architecture.feature.login.presentation.view.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.app.presentation.activity.MainActivity
import com.example.instagram_clone_clean_architecture.feature.login.R
import com.example.instagram_clone_clean_architecture.feature.login.databinding.FragmentLoginBinding
import com.example.library_base.presentation.fragment.InjectionFragment
import com.google.android.material.snackbar.Snackbar
import org.kodein.di.instance
import timber.log.Timber

class LoginFragment : InjectionFragment() {

    private val viewModel: LoginViewModel by instance()

    private val observer = Observer<LoginViewModel.ViewState> {
        if (it.isLoginFail) {
            showSnackBar(resources.getString(R.string.login_error_message))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setSupportAppBar(binding.loginAppBar)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.stateLiveData.observe(viewLifecycleOwner, observer)
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

    private fun showSnackBar(message: String) {
        Snackbar
            .make(requireView(), message, Snackbar.LENGTH_SHORT)
            .show()
    }

}