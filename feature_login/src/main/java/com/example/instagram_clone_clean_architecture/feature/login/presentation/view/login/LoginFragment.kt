package com.example.instagram_clone_clean_architecture.feature.login.presentation.view.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.instagram_clone_clean_architecture.feature.login.databinding.FragmentLoginBinding
import com.example.library_base.presentation.fragment.InjectionFragment

class LoginFragment : InjectionFragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

}