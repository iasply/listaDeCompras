package com.br.listadecompras.data.repository

import com.br.listadecompras.data.model.ListItemAggregator

class ListItemAggregatorDAO {
    private val db = Db

    fun create(list: ListItemAggregator): Boolean {
        val exist = db.listAggregator.find { it.id == list.id }
        if (exist == null) {
            list.id = db.idListAggregator++
            db.listAggregator.add(list)
            return true
        }
        return false
    }

    fun update(list: ListItemAggregator): Boolean {
        val index = db.listAggregator.indexOfFirst { it.id == list.id }
        return if (index != -1) {
            db.listAggregator[index] = list
            true
        } else {
            false
        }
    }

    fun delete(id: Int): Boolean {
        val list = db.listAggregator.find { it.id == id }
        return if (list != null) {
            db.listAggregator.remove(list)
            true
        } else {
            false
        }
    }

    fun getAllByUser(idUser: Int): List<ListItemAggregator> =
        db.listAggregator.filter { it.idUser == idUser }

    fun getById(id: Int): ListItemAggregator? = db.listAggregator.find { it.id == id }

    fun filterByUserAndQuery(idUser: Int, query: String): List<ListItemAggregator> {
        return db.listAggregator.filter {
            it.idUser == idUser && it.name.contains(query, ignoreCase = true)
        }
    }
}