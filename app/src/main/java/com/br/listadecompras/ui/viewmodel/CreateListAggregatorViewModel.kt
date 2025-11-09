package com.br.listadecompras.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br.listadecompras.Session
import com.br.listadecompras.data.model.ListItemAggregator
import com.br.listadecompras.data.repository.ListItemAggregatorRepository
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID

class CreateListAggregatorViewModel : ViewModel() {

    private val listItemAggregatorRepository = ListItemAggregatorRepository()
    private val storage: FirebaseStorage = Firebase.storage

    private val _state = MutableLiveData<UiState>()
    val state: LiveData<UiState> = _state

    private val _item = MutableLiveData<ListItemAggregator?>()
    val item: LiveData<ListItemAggregator?> = _item


    fun load(id: String) {
        val userId = Session.userLogged?.id ?: return
        viewModelScope.launch {
            _state.value = UiState.Loading
            val result = listItemAggregatorRepository.getById(userId, id)
            _item.value = result
            _state.value = UiState.Loaded("Lista carregada com sucesso.")
        }
    }

    fun save(uri: Uri?, name: String, existingId: String? = null) {
        val user = Session.userLogged ?: return

        viewModelScope.launch {
            try {
                _state.value = UiState.Loading

                var oldImageUrl: String? = null
                if (existingId != null) {
                    oldImageUrl = _item.value?.imageUri
                }


                var imageUrl: String? = null

                if (uri != null) {
                    val imageRef =
                        storage.reference.child("aggregators/${user.id}/${UUID.randomUUID()}")
                    imageRef.putFile(uri).await()
                    imageUrl = imageRef.downloadUrl.await().toString()
                }

                val aggregator = ListItemAggregator(
                    id = existingId ?: UUID.randomUUID().toString(),
                    imageUri = imageUrl,
                    name = name,
                    createdAt = Date(),
                    idUser = user.id
                )

                val success = if (existingId == null) {
                    listItemAggregatorRepository.create(user.id, aggregator)
                } else {
                    listItemAggregatorRepository.update(user.id, aggregator)
                }

                if (success && oldImageUrl != null && oldImageUrl != imageUrl && uri != null) {
                    try {
                        storage.getReferenceFromUrl(oldImageUrl).delete().await()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                _state.value = if (success) {
                    val msg =
                        if (existingId == null) "Lista criada com sucesso!" else "Lista atualizada com sucesso!"
                    UiState.Success(msg)
                } else {
                    UiState.Error("Erro ao salvar lista.")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = UiState.Error("Erro ao enviar imagem: ${e.message}")
            }
        }
    }

    fun delete(id: String) {
        val userId = Session.userLogged?.id ?: return
        viewModelScope.launch {
            _state.value = UiState.Loading

            val aggregator = listItemAggregatorRepository.getById(userId, id)
            val imageUrl = aggregator?.imageUri

            val success = listItemAggregatorRepository.delete(userId, id)

            if (success && imageUrl != null) {
                try {
                    storage.getReferenceFromUrl(imageUrl).delete().await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            _state.value = if (success) {
                UiState.Deleted("Lista excluída com sucesso!")
            } else {
                UiState.Error("Erro ao excluir lista.")
            }
        }
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val message: String) : UiState()
        data class Loaded(val message: String) : UiState()
        data class Deleted(val message: String) : UiState()
        data class Error(val message: String) : UiState()
    }
}
