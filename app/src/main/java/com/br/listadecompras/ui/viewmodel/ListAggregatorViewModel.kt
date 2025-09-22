package com.br.listadecompras.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.br.listadecompras.data.model.ListItem
import com.br.listadecompras.data.repository.ListItemDAO

class ListAggregatorViewModel : ViewModel() {

    private val dao = ListItemDAO()

    private val _items = MutableLiveData<List<ListItem>>()
    val items: LiveData<List<ListItem>> = _items

    fun loadItems(listAggregatorId: Int) {
        _items.value = dao.getAllByListAggregator(listAggregatorId)
    }

    fun updateChecked(item: ListItem, checked: Boolean) {
        val updated = item.copy(checked = checked)
        dao.update(updated)
        _items.value = dao.getAllByListAggregator(item.idListAggregator)
    }

    fun filter(idListAggregator: Int, query: String) {
        _items.value = dao.filterByUserAndQuery(idListAggregator, query)
    }

    fun refresh(listAggregatorId: Int) {
        loadItems(listAggregatorId)
    }
}
