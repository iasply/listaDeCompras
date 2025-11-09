package com.br.listadecompras.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.br.listadecompras.R
import com.br.listadecompras.databinding.FragmentResetPasswordBinding
import com.br.listadecompras.ui.viewmodel.ResetPasswordViewModel

class ResetPasswordFragment : Fragment(R.layout.fragment_reset_password) {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ResetPasswordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentResetPasswordBinding.bind(view)
        binding.toolbar.title = findNavController().currentDestination?.label
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnReset.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            viewModel.reset(email)
        }
    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ResetPasswordViewModel.UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is ResetPasswordViewModel.UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "E-mail de recuperação enviado para ${state.email}",
                        Toast.LENGTH_LONG
                    ).show()
                    findNavController().navigate(R.id.action_resetPassword_to_loginFragment)
                }
                is ResetPasswordViewModel.UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
