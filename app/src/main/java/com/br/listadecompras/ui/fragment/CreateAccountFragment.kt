package com.br.listadecompras.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.br.listadecompras.R
import com.br.listadecompras.databinding.FragmentCreateAccountBinding
import com.br.listadecompras.databinding.FragmentLoginBinding
import com.br.listadecompras.ui.viewmodel.CreateAccountViewModel
import com.br.listadecompras.ui.viewmodel.LoginViewModel

class CreateAccountFragment : Fragment(R.layout.fragment_create_account) {

    private lateinit var binding : FragmentCreateAccountBinding
    private val viewModel: CreateAccountViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentCreateAccountBinding.bind(view)

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            viewModel.createAccount(name, email, password, confirmPassword)
        }

        viewModel.createAccountResult.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

            if (message == "Cadastro realizado com sucesso") {
                findNavController().navigate(R.id.action_createAccountFragment_to_loginFragment)
            }
        }
    }


}