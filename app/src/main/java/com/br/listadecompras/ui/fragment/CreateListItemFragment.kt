package com.br.listadecompras.ui.fragment

import TypeCategoryEnum
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.br.listadecompras.Const
import com.br.listadecompras.R
import com.br.listadecompras.data.model.ListItem
import com.br.listadecompras.data.model.TypeUnitEnum
import com.br.listadecompras.databinding.FragmentCreateListItemBinding
import com.br.listadecompras.ui.viewmodel.CreateListItemViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CreateListItemFragment : Fragment(R.layout.fragment_create_list_item) {

    private lateinit var binding: FragmentCreateListItemBinding
    private val viewModel: CreateListItemViewModel by viewModels()

    private var listAggregatorId: String = ""
    private var editingItemId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentCreateListItemBinding.bind(view)
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        setupSpinners()
        binding.toolbar.title = findNavController().currentDestination?.label

        listAggregatorId = arguments?.getString(Const.AGGREGATOR_ID_BUNDLE) ?: ""
        editingItemId = arguments?.getString(Const.ITEM_ID_BUNDLE)

        editingItemId?.let { viewModel.load(listAggregatorId, it) }
        editingItemId?.let { binding.buttonDelete.isEnabled = false }
        if (editingItemId == null) {
            binding.editTextName.hint = "Nome do item"
            binding.editTextQtde.hint = "Quantidade"
        }

        initObservers()

        binding.buttonSave.setOnClickListener { saveItem() }
    }

    private fun setupSpinners() {
        binding.spinnerUnit.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            TypeUnitEnum.entries.toTypedArray()
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.spinnerCategory.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            TypeCategoryEnum.entries.toTypedArray()
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun initObservers() {
        lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                when (state) {
                    is CreateListItemViewModel.UiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is CreateListItemViewModel.UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        navigateBack()
                    }

                    is CreateListItemViewModel.UiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }

                    is CreateListItemViewModel.UiState.Loaded -> {
                        binding.progressBar.visibility = View.GONE
                        binding.buttonSave.text = "Atualizar"
                        binding.buttonDelete.isEnabled = true
                        binding.buttonDelete.apply {
                            visibility = View.VISIBLE
                            setOnClickListener {
                                viewModel.delete(
                                    listAggregatorId,
                                    editingItemId!!
                                )
                            }
                        }

                        val item = state.item
                        binding.editTextName.setText(item.name)
                        binding.editTextQtde.setText(item.qtde.toString())
                        binding.spinnerUnit.setSelection(item.unit.ordinal)
                        binding.spinnerCategory.setSelection(item.category.ordinal)
                    }

                    null -> Unit
                }
            }
        }
    }

    private fun saveItem() {
        val name = binding.editTextName.text.toString().trim()
        val qtde = binding.editTextQtde.text.toString().toIntOrNull()
        val unit = binding.spinnerUnit.selectedItem as TypeUnitEnum
        val category = binding.spinnerCategory.selectedItem as TypeCategoryEnum

        if (name.isBlank() || qtde == null) {
            Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val item = ListItem(
            id = editingItemId,
            name = name,
            qtde = qtde,
            unit = unit,
            category = category,
            idListAggregator = listAggregatorId
        )

        if (editingItemId == null)
            viewModel.create(listAggregatorId, item)
        else
            viewModel.update(listAggregatorId, item)
    }

    private fun navigateBack() {
        findNavController().navigate(
            R.id.action_createListItemFragment_to_listAggregatorFragment,
            Bundle().apply { putString(Const.AGGREGATOR_ID_BUNDLE, listAggregatorId) }
        )
    }
}
