package com.br.listadecompras.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.br.listadecompras.Const
import com.br.listadecompras.R
import com.br.listadecompras.databinding.FragmentCreateListAggregatorBinding
import com.br.listadecompras.ui.viewmodel.CreateListAggregatorViewModel
import java.io.File
import java.io.FileOutputStream

class CreateListAggregatorFragment : Fragment(R.layout.fragment_create_list_aggregator) {

    private lateinit var binding: FragmentCreateListAggregatorBinding
    private val viewModel: CreateListAggregatorViewModel by viewModels()

    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private var selectedImageUri: Uri? = null
    private var editingId: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentCreateListAggregatorBinding.bind(view)

        initPickImage()
        initObservers()

        editingId = arguments?.getInt(Const.AGGREGATOR_ID_BUNDLE)
        editingId?.let { viewModel.load(it) }

        binding.buttonSave.setOnClickListener { onSave() }
        binding.toolbar.title = findNavController().currentDestination?.label

    }

    private fun initPickImage() {
        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let {
                selectedImageUri = it
                binding.imagePreview.setImageURI(it)
            }
        }
        binding.buttonPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun initObservers() {
        viewModel.item.observe(viewLifecycleOwner) { item ->
            item?.let {
                binding.editTextName.setText(it.name)
                binding.imagePreview.setImageURI(it.imageUri.toUri())
                selectedImageUri = it.imageUri.toUri()
                binding.buttonSave.text = "Atualizar"
                binding.buttonDelete.apply {
                    visibility = View.VISIBLE
                    setOnClickListener { viewModel.delete(editingId!!) }
                }
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CreateListAggregatorViewModel.UiState.Success -> {
                    Toast.makeText(requireContext(), "Item salvo!", Toast.LENGTH_SHORT).show()

                    val bundle = Bundle()
                    val destination: Int

                    if (editingId == null) {
                        destination = R.id.action_createListAggregatorFragment_to_homeFragment
                    } else {
                        destination = R.id.action_createListAggregatorFragment_to_listAggregatorFragment
                        bundle.putInt(Const.AGGREGATOR_ID_BUNDLE, editingId!!)
                    }

                    findNavController().navigate(destination, bundle)

                }

                is CreateListAggregatorViewModel.UiState.Deleted -> {
                    Toast.makeText(requireContext(), "Item excluído!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_createListAggregatorFragment_to_homeFragment)
                }

                is CreateListAggregatorViewModel.UiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onSave() {
        val name = binding.editTextName.text.toString()
        val path = copyUriToInternalStorage(selectedImageUri)
            ?: "android.resource://${requireContext().packageName}/${R.drawable.ic_launcher_background}".toUri()

        if (name.isNotBlank() ) {
            viewModel.save(path, name, editingId)
        } else {
            Toast.makeText(requireContext(), "Preencha o nome", Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyUriToInternalStorage(uri: Uri?): Uri? {
        if (uri == null) return null

        val file = File(requireContext().filesDir, "img_${System.currentTimeMillis()}.jpg")
        requireContext().contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output -> input.copyTo(output) }
        }

        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )
    }

}
