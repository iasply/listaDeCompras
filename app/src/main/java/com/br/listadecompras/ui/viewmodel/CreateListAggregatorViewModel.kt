package com.br.listadecompras.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.br.listadecompras.Session
import com.br.listadecompras.data.model.ListItemAggregator
import com.br.listadecompras.data.repository.ListItemAggregatorDAO
import com.br.listadecompras.data.repository.ListItemDAO
import java.util.Date
class CreateListAggregatorViewModel : ViewModel() {

    private val listItemAggregatorDAO = ListItemAggregatorDAO()
    private val listItemDAO = ListItemDAO()

    private val _state = MutableLiveData<UiState>()
    val state: LiveData<UiState> = _state

    private val _item = MutableLiveData<ListItemAggregator?>()
    val item: LiveData<ListItemAggregator?> = _item

    fun load(id: Int) {
        _item.value = listItemAggregatorDAO.getById(id)
    }

    fun save(uri: Uri, name: String, existingId: Int? = null) {
        val aggregator = ListItemAggregator(
            id = existingId,
            imageUri = uri.toString(),
            name = name,
            createdAt = Date(),
            Session.userLogged!!.id!!
        )

        val success = if (existingId == null) {
            listItemAggregatorDAO.create(aggregator)
        } else {
            listItemAggregatorDAO.update(aggregator)
        }

        _state.value = if (success) UiState.Success else UiState.Error("Erro ao salvar")
    }

    fun delete(id: Int) {
        listItemAggregatorDAO.delete(id)
        listItemDAO.deleteAllByListAggregator(id)
        _state.value = UiState.Deleted
    }

    sealed class UiState {
        object Success : UiState()
        object Deleted : UiState()
        data class Error(val message: String) : UiState()
    }
}
