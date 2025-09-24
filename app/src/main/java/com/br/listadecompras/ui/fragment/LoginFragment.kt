package com.br.listadecompras.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.br.listadecompras.R
import com.br.listadecompras.databinding.FragmentLoginBinding
import com.br.listadecompras.ui.viewmodel.LoginViewModel

class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding

    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentLoginBinding.bind(view)

        binding.btnLogin.setOnClickListener {
            val username = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(username, password)
        }
        binding.imageView2.setImageResource(R.mipmap.ic_launcher)


        binding.btnCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_createAccountFragment)
        }

        viewModel.loginResult.observe(viewLifecycleOwner) { success ->
            if (success != null) {
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            } else {
                binding.etPassword.text.clear()
                binding.etEmail.text.clear()
                Toast.makeText(requireContext(), "Usuário ou senha incorretos", Toast.LENGTH_SHORT)
                    .show()
            }
        }


    }

}
