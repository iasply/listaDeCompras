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
        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createListAggregator)
        }
        setupToolBar()
        setupRecycler()
    }

    private fun setupToolBar() {
        val searchItem = binding.toolbar.menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                query?.let { adapter.items = viewModel.filter(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    viewModel.filter(it)
                }
                return true
            }

        })

        searchView.setOnCloseListener {
            Toast.makeText(requireContext(), "Pesquisa fechada", Toast.LENGTH_SHORT).show()
            adapter.items = viewModel.getAll()
            false
        }

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_logout -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Sair da conta?")
                        .setMessage("Tem certeza de que deseja encerrar a sessão?")
                        .setPositiveButton("Sim") { dialogInterface: DialogInterface, which: Int ->
                            viewModel.logout()
                            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                            Toast.makeText(requireContext(), "Logout realizado com sucesso", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("Cancelar") { dialogInterface: DialogInterface, which: Int ->
                            dialogInterface.dismiss()
                        }
                        .show()
                    true
                }


                else -> false
            }
        }
        binding.toolbar.title = findNavController().currentDestination?.label


        binding.displayText.text = "Suas listas  - ${Session.userLogged?.name}"
    }

    private fun setupRecycler() {
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = ListAggregatorItemAdapter(viewModel.getAll())
        binding.recyclerView.adapter = adapter
    }
}