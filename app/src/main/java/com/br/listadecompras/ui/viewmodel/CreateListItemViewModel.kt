package com.br.listadecompras.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.br.listadecompras.data.model.ListItem
import com.br.listadecompras.data.repository.ListItemDAO

class CreateListItemViewModel : ViewModel() {

    private val dao = ListItemDAO()

    fun saveItem(item: ListItem): Boolean {
        return dao.create(item)
    }

    fun updateItem(item: ListItem): Boolean {
        return dao.update(item)
    }

    fun delete(itemId: Int): Boolean {
        return dao.delete(itemId)
    }

    fun getById(itemId: Int): ListItem? {
        return dao.getById(itemId)
    }

}
