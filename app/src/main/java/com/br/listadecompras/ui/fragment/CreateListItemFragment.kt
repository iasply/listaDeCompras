package com.br.listadecompras.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.br.listadecompras.Const
import com.br.listadecompras.R
import com.br.listadecompras.data.model.ListItem
import com.br.listadecompras.data.model.TypeCategoryEnum
import com.br.listadecompras.data.model.TypeUnitEnum
import com.br.listadecompras.databinding.FragmentCreateListItemBinding
import com.br.listadecompras.ui.viewmodel.CreateListItemViewModel

class CreateListItemFragment : Fragment(R.layout.fragment_create_list_item) {

    private lateinit var binding: FragmentCreateListItemBinding
    private val viewModel: CreateListItemViewModel by viewModels()

    private var editingItem: ListItem? = null
    private var listAggregatorId: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreateListItemBinding.bind(view)

        // Recupera aggregatorId e itemId
        listAggregatorId = arguments?.getInt(Const.AGGREGATOR_ID_BUNDLE) ?: -1
        val itemId = arguments?.getInt(Const.ITEM_ID_BUNDLE)

        setupSpinners()
        setupSaveButton(itemId)
    }

    private fun setupSpinners() {
        binding.spinnerUnit.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            TypeUnitEnum.entries.toTypedArray()
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        binding.spinnerCategory.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            TypeCategoryEnum.entries.toTypedArray()
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
    }

    private fun setupSaveButton(itemId: Int?) {
        if (itemId != null && itemId != 0) {
            // Modo edição
            editingItem = viewModel.getById(itemId)
            editingItem?.let { item ->
                binding.editTextName.setText(item.name)
                binding.editTextQtde.setText(item.qtde.toString())

                // Seleciona valores dos spinners
                binding.spinnerUnit.setSelection(TypeUnitEnum.entries.indexOf(item.unit))
                binding.spinnerCategory.setSelection(TypeCategoryEnum.entries.indexOf(item.category))

                binding.buttonSave.text = "Atualizar"
                binding.buttonDelete.visibility = View.VISIBLE

                binding.buttonDelete.setOnClickListener {
                    viewModel.delete(item.id!!)
                    Toast.makeText(requireContext(), "Item deletado", Toast.LENGTH_SHORT).show()
                    navigateBack()
                }

                binding.buttonSave.setOnClickListener {
                    val name = binding.editTextName.text.toString()
                    val qtde = binding.editTextQtde.text.toString().toIntOrNull()
                    val unit = binding.spinnerUnit.selectedItem as TypeUnitEnum
                    val category = binding.spinnerCategory.selectedItem as TypeCategoryEnum

                    if (name.isBlank() || qtde == null) {
                        Toast.makeText(
                            requireContext(),
                            "Preencha todos os campos",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    val updatedItem = item.copy(
                        name = name,
                        qtde = qtde,
                        unit = unit,
                        category = category
                    )

                    if (viewModel.updateItem(updatedItem)) {
                        Toast.makeText(
                            requireContext(),
                            "Item atualizado com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                        navigateBack()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Erro ao atualizar item",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else {
            // Modo criação
            binding.buttonDelete.visibility = View.GONE
            binding.buttonSave.setOnClickListener {
                val name = binding.editTextName.text.toString()
                val qtde = binding.editTextQtde.text.toString().toIntOrNull()
                val unit = binding.spinnerUnit.selectedItem as TypeUnitEnum
                val category = binding.spinnerCategory.selectedItem as TypeCategoryEnum

                if (name.isBlank() || qtde == null) {
                    Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                val newItem = ListItem(
                    id = null,
                    name = name,
                    qtde = qtde,
                    unit = unit,
                    category = category,
                    checked = false,
                    idListAggregator = listAggregatorId
                )

                if (viewModel.saveItem(newItem)) {
                    Toast.makeText(requireContext(), "Item salvo com sucesso!", Toast.LENGTH_SHORT)
                        .show()
                    navigateBack()
                } else {
                    Toast.makeText(requireContext(), "Item já existe!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateBack() {
        findNavController().navigate(
            R.id.action_createListItemFragment_to_listAggregatorFragment,
            Bundle().apply { putInt(Const.AGGREGATOR_ID_BUNDLE, listAggregatorId) }
        )
    }
}
