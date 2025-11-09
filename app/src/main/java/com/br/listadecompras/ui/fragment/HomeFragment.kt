package com.br.listadecompras.ui.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.br.listadecompras.R
import com.br.listadecompras.Session
import com.br.listadecompras.databinding.FragmentHomeBinding
import com.br.listadecompras.ui.adapter.ListAggregatorItemAdapter
import com.br.listadecompras.ui.viewmodel.HomeViewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ListAggregatorItemAdapter
    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        setupRecycler()
        setupToolBar()
        observeViewModel()

        viewModel.getAll()

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createListAggregator)
        }
    }

    private fun setupRecycler() {
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = ListAggregatorItemAdapter(emptyList())
        binding.recyclerView.adapter = adapter
    }

    private fun setupToolBar() {
        binding.toolbar.title = findNavController().currentDestination?.label
        binding.displayText.text = "Suas listas - ${Session.userLogged?.name ?: ""}"

        val searchItem = binding.toolbar.menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.filter(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) { viewModel.filter(newText)}
                return true
            }
        })

        searchView.setOnCloseListener {
            viewModel.getAll()
            false
        }

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_logout -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Sair da conta?")
                        .setMessage("Tem certeza de que deseja encerrar a sessão?")
                        .setPositiveButton("Sim") { _: DialogInterface, _: Int ->
                            viewModel.logout()
                            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                            Toast.makeText(
                                requireContext(),
                                "Logout realizado com sucesso",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                    true
                }
                else -> false
            }
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is HomeViewModel.UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is HomeViewModel.UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.items = state.lists
                }
                is HomeViewModel.UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
