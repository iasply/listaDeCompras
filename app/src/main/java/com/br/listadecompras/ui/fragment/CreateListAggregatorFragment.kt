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
import com.bumptech.glide.Glide
import java.io.File
import java.io.FileOutputStream

class CreateListAggregatorFragment : Fragment(R.layout.fragment_create_list_aggregator) {

    private var _binding: FragmentCreateListAggregatorBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreateListAggregatorViewModel by viewModels()

    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private var selectedImageUri: Uri? = null
    private var editingId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreateListAggregatorBinding.bind(view)
        binding.toolbar.title = findNavController().currentDestination?.label
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        initPickImage()
        initObservers()

        editingId = arguments?.getString(Const.AGGREGATOR_ID_BUNDLE)
        editingId?.let(viewModel::load)

        binding.buttonSave.setOnClickListener { onSave() }
    }

    private fun initPickImage() {
        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let {
                selectedImageUri = it
                Glide.with(this)
                    .load(it)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(binding.imagePreview)
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
                binding.imagePreview.setImageURI(it.imageUri?.toUri())
                selectedImageUri = it.imageUri?.toUri()
                binding.buttonSave.text = "Atualizar"
                Glide.with(this)
                    .load(it.imageUri?.toUri() ?: R.drawable.ic_launcher_background)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(binding.imagePreview)
                binding.buttonDelete.apply {
                    isEnabled = true
                    setOnClickListener { viewModel.delete(editingId!!) }
                }
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {

                is CreateListAggregatorViewModel.UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()

                    val bundle = Bundle()
                    if (editingId == null) {
                        findNavController().navigate(
                            R.id.action_createListAggregatorFragment_to_homeFragment
                        )
                    } else {
                        bundle.putString(Const.AGGREGATOR_ID_BUNDLE, editingId!!)
                        findNavController().navigate(
                            R.id.action_createListAggregatorFragment_to_listAggregatorFragment,
                            bundle
                        )
                    }
                }

                is CreateListAggregatorViewModel.UiState.Deleted -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Item excluído!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(
                        R.id.action_createListAggregatorFragment_to_homeFragment
                    )
                }

                is CreateListAggregatorViewModel.UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }

                CreateListAggregatorViewModel.UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is CreateListAggregatorViewModel.UiState.Loaded ->{
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun onSave() {
        val name = binding.editTextName.text.toString().trim()
        if (name.isBlank()) {
            Toast.makeText(requireContext(), "Preencha o nome", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.save(selectedImageUri, name, editingId)
    }

    private fun copyUriToInternalStorage(uri: Uri?): Uri? {
        if (uri == null) return null

        val file = File(requireContext().filesDir, "img_saved.jpg")

        requireContext().contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        return file.toUri()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
