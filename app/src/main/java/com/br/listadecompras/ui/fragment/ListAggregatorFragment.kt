package com.br.listadecompras.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.br.listadecompras.Const
import com.br.listadecompras.R
import com.br.listadecompras.databinding.FragmentListAggregatorBinding
import com.br.listadecompras.ui.adapter.ListItemAdapter
import com.br.listadecompras.ui.viewmodel.ListAggregatorViewModel

class ListAggregatorFragment : Fragment(R.layout.fragment_list_aggregator) {

    private lateinit var binding: FragmentListAggregatorBinding
    private lateinit var adapter: ListItemAdapter
    private val viewModel: ListAggregatorViewModel by viewModels()
    private var listAggregatorId: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListAggregatorBinding.bind(view)

        setupRecyclerView()
        setupToolbar()
        setupFab()

        listAggregatorId = arguments?.getInt(Const.AGGREGATOR_ID_BUNDLE) ?: -1
        if (listAggregatorId != -1) {
            viewModel.loadItems(listAggregatorId)
        }

        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = ListItemAdapter(
            emptyList(),
            onCheckedChange = { item, checked -> viewModel.updateChecked(item, checked) },
            onItemClick = { item ->
                findNavController().navigate(
                    R.id.action_listAggregatorFragment_to_createListItemFragment, Bundle().apply {
                        putInt(Const.AGGREGATOR_ID_BUNDLE, item.idListAggregator)
                        putInt(Const.ITEM_ID_BUNDLE, item.id!!)
                    })
            })
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupToolbar() {
        val searchItem = binding.toolbar.menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.filter(listAggregatorId, it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    viewModel.filter(listAggregatorId, it)
                }
                return true
            }
        })

        searchView.setOnCloseListener {
            viewModel.refresh(listAggregatorId)
            false
        }

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_edit -> {
                    findNavController().navigate(
                        R.id.action_listAggregatorFragment_to_createListAggregatorFragment,
                        Bundle().apply { putInt(Const.AGGREGATOR_ID_BUNDLE, listAggregatorId) })
                    true
                }

                else -> false
            }
        }
    }

    private fun setupFab() {
        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_listAggregatorFragment_to_createListItemFragment,
                Bundle().apply { putInt(Const.AGGREGATOR_ID_BUNDLE, listAggregatorId) })
        }
    }

    private fun observeViewModel() {
        viewModel.items.observe(viewLifecycleOwner) { items ->
            adapter.items = items
        }
    }
}
