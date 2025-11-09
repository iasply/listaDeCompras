package com.br.listadecompras.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br.listadecompras.Session
import com.br.listadecompras.data.model.ListItemAggregator
import com.br.listadecompras.data.repository.ListItemAggregatorRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val listItemAggregatorRepository = ListItemAggregatorRepository()
    private var cachedLists: List<ListItemAggregator> = emptyList()

    sealed class UiState {
        object Loading : UiState()
        data class Success(val lists: List<ListItemAggregator>) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _state = MutableLiveData<UiState>()
    val state: LiveData<UiState> = _state

    fun getAll() {
        val userId = Session.userLogged?.id ?: return
        _state.value = UiState.Loading

        viewModelScope.launch {
            try {

                val lists = listItemAggregatorRepository.getAllByUser(userId)
                cachedLists = lists
                _state.value = UiState.Success(lists)
            } catch (e: Exception) {
                _state.value = UiState.Error("Erro ao carregar listas: ${e.message}")
            }
        }
    }

    fun filter(query: String) {

        viewModelScope.launch {
            try {
                val filtered = if (query.isBlank()) {
                    cachedLists
                } else {
                    cachedLists.filter {
                        it.name.contains(query, ignoreCase = true)
                    }
                }
                _state.value = UiState.Success(filtered)
            } catch (e: Exception) {
                _state.value = UiState.Error("Erro ao filtrar listas: ${e.message}")
            }
        }
    }

    fun logout() {
        Session.clear()
        FirebaseAuth.getInstance().signOut()
        cachedLists = emptyList()
    }
}
