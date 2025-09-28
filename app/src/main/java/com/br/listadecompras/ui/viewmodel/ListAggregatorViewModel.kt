package com.br.listadecompras.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.br.listadecompras.data.model.ListItem
import com.br.listadecompras.data.repository.ListItemAggregatorDAO
import com.br.listadecompras.data.repository.ListItemDAO

class ListAggregatorViewModel : ViewModel() {

    private val listItemDAO = ListItemDAO()
    private val listItemAggregatorDAO = ListItemAggregatorDAO()
    private val _items = MutableLiveData<List<ListItem>>()
    val items: LiveData<List<ListItem>> = _items

    fun loadItems(listAggregatorId: Int) {
        _items.value = listItemDAO.getAllByListAggregator(listAggregatorId)
    }

    fun updateChecked(item: ListItem, checked: Boolean) {
        val updated = item.copy(checked = checked)
        listItemDAO.update(updated)
        _items.value = listItemDAO.getAllByListAggregator(item.idListAggregator)
    }

    fun filter(idListAggregator: Int, query: String) {
        _items.value = listItemDAO.filterByUserAndQuery(idListAggregator, query)
    }

    fun refresh(listAggregatorId: Int) {
        loadItems(listAggregatorId)
    }

    fun getName(listAggregatorId: Int): String {
        return listItemAggregatorDAO.getById(listAggregatorId)!!.name
    }
}
