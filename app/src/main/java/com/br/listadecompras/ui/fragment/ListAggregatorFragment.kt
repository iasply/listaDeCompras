package com.br.listadecompras.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.br.listadecompras.Const
import com.br.listadecompras.R
import com.br.listadecompras.databinding.FragmentListAggregatorBinding
import com.br.listadecompras.ui.adapter.ListItemAdapter
import com.br.listadecompras.ui.viewmodel.ListAggregatorViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ListAggregatorFragment : Fragment(R.layout.fragment_list_aggregator) {

    private lateinit var binding: FragmentListAggregatorBinding
    private lateinit var adapter: ListItemAdapter
    private val viewModel: ListAggregatorViewModel by viewModels()
    private var listAggregatorId: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListAggregatorBinding.bind(view)
        listAggregatorId = arguments?.getString(Const.AGGREGATOR_ID_BUNDLE) ?: ""
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        setupRecyclerView()
        setupToolbar()
        setupFab()


        if (listAggregatorId.isNotEmpty()) {
            viewModel.loadItems(listAggregatorId)
        }

        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = ListItemAdapter(
            emptyList(),
            onCheckedChange = { item, checked ->
                viewModel.updateChecked(listAggregatorId, item, checked)
            },
            onItemClick = { item ->
                findNavController().navigate(
                    R.id.action_listAggregatorFragment_to_createListItemFragment,
                    Bundle().apply {
                        putString(Const.AGGREGATOR_ID_BUNDLE, item.idListAggregator)
                        putString(Const.ITEM_ID_BUNDLE, item.id)
                    }
                )
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupToolbar() {
        val searchItem = binding.toolbar.menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        binding.toolbar.title = findNavController().currentDestination?.label
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.filter(listAggregatorId, it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { viewModel.filter(listAggregatorId, it) }
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
                        Bundle().apply { putString(Const.AGGREGATOR_ID_BUNDLE, listAggregatorId) })
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
                Bundle().apply { putString(Const.AGGREGATOR_ID_BUNDLE, listAggregatorId) })
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                when (state) {
                    is ListAggregatorViewModel.UiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is ListAggregatorViewModel.UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        adapter.items = state.items
                        binding.displayText.text = state.aggregator.name

                    }

                    is ListAggregatorViewModel.UiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.displayText.text = state.message
                    }

                    null -> Unit
                }
            }
        }
    }
}
