package com.br.listadecompras.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.br.listadecompras.R
import com.br.listadecompras.data.repository.ListItemAggregatorDAO
import com.br.listadecompras.databinding.FragmentHomeBinding
import com.br.listadecompras.ui.adapter.ListAggregatorItemAdapter
import com.br.listadecompras.ui.viewmodel.CreateListItemViewModel
import com.br.listadecompras.ui.viewmodel.HomeViewModel
import kotlin.getValue

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
                    viewModel.logout()
                    findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                    true
                }

                else -> false
            }
        }
    }

    private fun setupRecycler() {
//        val exampleItems = listOf(
//            ListItemAggregator(null, "android.resource://android/" + android.R.drawable.ic_menu_camera, "Câmera", Date()),
//            ListItemAggregator(null, "android.resource://android/" + android.R.drawable.ic_menu_gallery, "Galeria", Date()),
//            ListItemAggregator(null, "android.resource://android/" + android.R.drawable.ic_menu_compass, "Mapa",
//                Date()
//            ),
//            ListItemAggregator(null, "android.resource://android/" + android.R.drawable.ic_menu_call, "Telefone", Date())
//        )


        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = ListAggregatorItemAdapter(viewModel.getAll())
        binding.recyclerView.adapter = adapter
    }
}