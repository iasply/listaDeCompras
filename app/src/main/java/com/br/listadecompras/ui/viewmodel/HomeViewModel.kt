package com.br.listadecompras.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.br.listadecompras.Session
import com.br.listadecompras.data.model.ListItemAggregator
import com.br.listadecompras.data.repository.ListItemAggregatorDAO

class HomeViewModel : ViewModel() {
    private val listItemAggregatorDAO: ListItemAggregatorDAO = ListItemAggregatorDAO()

    fun getAll(): List<ListItemAggregator> {
        return listItemAggregatorDAO.getAllByUser(Session.userLogged!!.id!!)
    }

    fun logout() {
        Session.userLogged = null
    }
}
