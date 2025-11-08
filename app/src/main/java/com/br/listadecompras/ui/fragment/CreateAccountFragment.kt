package com.br.listadecompras.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.br.listadecompras.R
import com.br.listadecompras.databinding.FragmentCreateAccountBinding
import com.br.listadecompras.ui.viewmodel.CreateAccountViewModel

class CreateAccountFragment : Fragment(R.layout.fragment_create_account) {

    private lateinit var binding: FragmentCreateAccountBinding
    private val viewModel: CreateAccountViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentCreateAccountBinding.bind(view)

        setupObservers()
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            viewModel.createAccount(name, email, password, confirmPassword)
        }
    }

    private fun setupObservers() {
        viewModel.createAccountResult.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()

                if (it == "Cadastro realizado com sucesso") {
                    findNavController().navigate(R.id.action_createAccountFragment_to_loginFragment)
                }

                viewModel.clearResult()
            }
        }
        binding.toolbar.title = findNavController().currentDestination?.label

    }
}
