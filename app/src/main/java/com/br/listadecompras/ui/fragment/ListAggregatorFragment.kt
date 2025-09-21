package com.br.listadecompras.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.br.listadecompras.Const
import com.br.listadecompras.R
import com.br.listadecompras.databinding.FragmentListAggregatorBinding
import com.br.listadecompras.ui.adapter.ListItemAdapter
import com.br.listadecompras.ui.viewmodel.ListAggregatorViewModel
import kotlin.properties.Delegates

class ListAggregatorFragment : Fragment(R.layout.fragment_list_aggregator) {

    private lateinit var binding: FragmentListAggregatorBinding

    private val viewModel: ListAggregatorViewModel by viewModels()
    private var listAggregatorId: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListAggregatorBinding.bind(view)

        val recycler = binding.recyclerView
        recycler.layoutManager = LinearLayoutManager(requireContext())

        listAggregatorId = arguments?.getInt(Const.AGGREGATOR_ID_BUNDLE) ?: -1
        if (listAggregatorId != -1) {
            viewModel.loadItems(listAggregatorId)
        }

        viewModel.items.observe(viewLifecycleOwner) { items ->
            binding.recyclerView.adapter =
                ListItemAdapter(items, onCheckedChange = { item, checked ->
                    viewModel.updateChecked(item, checked)
                }, onItemClick = { item ->
                    findNavController().navigate(
                        R.id.action_listAggregatorFragment_to_createListItemFragment,
                        Bundle().apply {
                            putInt(Const.AGGREGATOR_ID_BUNDLE, item.idListAggregator)
                            putInt(Const.ITEM_ID_BUNDLE, item.id!!)
                        })
                })
        }

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_listAggregatorFragment_to_createListItemFragment, Bundle().apply {
                    putInt(
                        Const.AGGREGATOR_ID_BUNDLE, listAggregatorId
                    )
                })
        }

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_edit -> {
                    findNavController().navigate(
                        R.id.action_listAggregatorFragment_to_createListAggregatorFragment,
                        Bundle().apply {
                            putInt(
                                Const.AGGREGATOR_ID_BUNDLE, listAggregatorId
                            )
                        })
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

}
