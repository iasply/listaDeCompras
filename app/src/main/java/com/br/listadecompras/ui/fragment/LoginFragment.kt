package com.br.listadecompras.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.br.listadecompras.R
import com.br.listadecompras.Session
import com.br.listadecompras.databinding.FragmentLoginBinding
import com.br.listadecompras.ui.viewmodel.LoginViewModel
import com.bumptech.glide.Glide

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Session.userLogged?.let{ findNavController().navigate(R.id.action_loginFragment_to_homeFragment)}
        binding = FragmentLoginBinding.bind(view)

        Glide.with(this)
            .load(R.mipmap.ic_launcher)
            .into(binding.imageView2)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(email, password)
        }

        binding.btnCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_createAccountFragment)
        }
        binding.btnResetPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_resetPasswordFragment)
        }
        binding.toolbar.title = findNavController().currentDestination?.label
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoginViewModel.UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is LoginViewModel.UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(), "Login realizado com sucesso!", Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }

                is LoginViewModel.UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.etPassword.text.clear()
                    binding.etEmail.text.clear()
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
