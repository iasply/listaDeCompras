package com.br.listadecompras.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br.listadecompras.Session
import com.br.listadecompras.data.model.ListItem
import com.br.listadecompras.data.repository.ListItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateListItemViewModel : ViewModel() {

    private val listItemRepository = ListItemRepository()

    sealed class UiState {
        object Loading : UiState()
        data class Success(val message: String) : UiState()
        data class Error(val message: String) : UiState()
        data class Loaded(val item: ListItem) : UiState()
    }

    private val _state = MutableStateFlow<UiState?>(null)
    val state: StateFlow<UiState?> = _state

    fun load(listId: String, itemId: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val userId = Session.userLogged?.id ?: throw IllegalStateException("Usuário não logado")
                val item = listItemRepository.getById(userId, listId, itemId)
                if (item != null) {
                    _state.value = UiState.Loaded(item)
                } else {
                    _state.value = UiState.Error("Item não encontrado")
                }
            } catch (e: Exception) {
                _state.value = UiState.Error(e.localizedMessage ?: "Erro ao carregar item")
            }
        }
    }

    fun create(listId: String, item: ListItem) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val userId = Session.userLogged?.id ?: throw IllegalStateException("Usuário não logado")
                val success = listItemRepository.create(userId, listId, item)
                _state.value = if (success)
                    UiState.Success("Item criado com sucesso!")
                else
                    UiState.Error("Erro ao criar item")
            } catch (e: Exception) {
                _state.value = UiState.Error(e.localizedMessage ?: "Erro desconhecido")
            }
        }
    }

    fun update(listId: String, item: ListItem) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val userId = Session.userLogged?.id ?: throw IllegalStateException("Usuário não logado")
                val success = listItemRepository.update(userId, listId, item)
                _state.value = if (success)
                    UiState.Success("Item atualizado com sucesso!")
                else
                    UiState.Error("Erro ao atualizar item")
            } catch (e: Exception) {
                _state.value = UiState.Error(e.localizedMessage ?: "Erro desconhecido")
            }
        }
    }

    fun delete(listId: String, itemId: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val userId = Session.userLogged?.id ?: throw IllegalStateException("Usuário não logado")
                val success = listItemRepository.delete(userId, listId, itemId)
                _state.value = if (success)
                    UiState.Success("Item excluído com sucesso!")
                else
                    UiState.Error("Erro ao excluir item")
            } catch (e: Exception) {
                _state.value = UiState.Error(e.localizedMessage ?: "Erro desconhecido")
            }
        }
    }
}
