package com.br.listadecompras.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
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
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_logout -> {
                    viewModel.logout()
                    findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                    true
                }

                R.id.action_search -> {
                    val searchView = item.actionView as SearchView
                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            Toast.makeText(requireContext(), "Buscando: $query", Toast.LENGTH_SHORT)
                                .show()
                            return true
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            return true
                        }
                    })
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
        binding.recyclerView.adapter = ListAggregatorItemAdapter(viewModel.getAll())
    }
}