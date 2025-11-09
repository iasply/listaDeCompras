package com.br.listadecompras.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br.listadecompras.Session
import com.br.listadecompras.data.model.ListItem
import com.br.listadecompras.data.model.ListItemAggregator
import com.br.listadecompras.data.repository.ListItemAggregatorRepository
import com.br.listadecompras.data.repository.ListItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ListAggregatorViewModel : ViewModel() {

    private val listItemRepository = ListItemRepository()
    private val listItemAggregatorRepository = ListItemAggregatorRepository()

    sealed class UiState {
        object Loading : UiState()
        data class Success(val items: List<ListItem>, val aggregator: ListItemAggregator) :
            UiState()

        data class Error(val message: String) : UiState()
    }

    private val _state = MutableStateFlow<UiState?>(null)
    val state: StateFlow<UiState?> = _state

    private val cachedItems = mutableMapOf<String, List<ListItem>>()
    private val cachedAggregators = mutableMapOf<String, ListItemAggregator>()

    fun loadItems(listAggregatorId: String) {
        val userId = Session.userLogged?.id ?: run {
            _state.value = UiState.Error("Usuário não logado")
            return
        }
        _state.value = UiState.Loading

        viewModelScope.launch {
            try {
                val items = listItemRepository.getAllByListAggregator(userId, listAggregatorId)
                val aggregator = listItemAggregatorRepository.getById(userId, listAggregatorId)
                    ?: throw Exception("Agregador não encontrado")
                cachedItems[listAggregatorId] = items
                cachedAggregators[listAggregatorId] = aggregator
                _state.value = UiState.Success(items, aggregator)
            } catch (e: Exception) {
                _state.value = UiState.Error(e.localizedMessage ?: "Erro ao carregar itens")
            }
        }
    }

    fun updateChecked(listAggregatorId: String, item: ListItem, checked: Boolean) {
        val userId = Session.userLogged?.id ?: run {
            _state.value = UiState.Error("Usuário não logado")
            return
        }

        viewModelScope.launch {
            try {
                val updated = item.copy(checked = checked)
                listItemRepository.update(userId, listAggregatorId, updated)

                // Atualiza cache
                val updatedList = cachedItems[listAggregatorId]?.map {
                    if (it.id == item.id) updated else it
                } ?: listOf(updated)
                cachedItems[listAggregatorId] = updatedList

                val aggregator = cachedAggregators[listAggregatorId] ?: run {
                    val agg = listItemAggregatorRepository.getById(
                        Session.userLogged!!.id!!,
                        listAggregatorId
                    )
                        ?: throw Exception("Agregador não encontrado")
                    cachedAggregators[listAggregatorId] = agg
                    agg
                }

                _state.value = UiState.Success(updatedList, aggregator)
            } catch (e: Exception) {
                _state.value = UiState.Error(e.localizedMessage ?: "Erro ao atualizar item")
            }
        }
    }

    fun filter(listAggregatorId: String, query: String) {
        viewModelScope.launch {
            try {
                val items = cachedItems[listAggregatorId] ?: run {
                    _state.value = UiState.Loading
                    val userId = Session.userLogged?.id ?: throw Exception("Usuário não logado")
                    val fetched =
                        listItemRepository.getAllByListAggregator(userId, listAggregatorId)
                    cachedItems[listAggregatorId] = fetched
                    fetched
                }

                val aggregator = cachedAggregators[listAggregatorId] ?: run {
                    val agg = listItemAggregatorRepository.getById(
                        Session.userLogged!!.id!!,
                        listAggregatorId
                    )
                        ?: throw Exception("Agregador não encontrado")
                    cachedAggregators[listAggregatorId] = agg
                    agg
                }

                val filtered = if (query.isBlank()) items
                else items.filter { it.name.contains(query, ignoreCase = true) }

                _state.value = UiState.Success(filtered, aggregator)
            } catch (e: Exception) {
                _state.value = UiState.Error(e.localizedMessage ?: "Erro ao filtrar itens")
            }
        }
    }

    fun refresh(listAggregatorId: String) {
        loadItems(listAggregatorId)
    }

}
