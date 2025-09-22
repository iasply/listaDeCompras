package com.br.listadecompras.data.repository

import com.br.listadecompras.data.model.ListItem
import com.br.listadecompras.data.model.ListItemAggregator

class ListItemDAO {

    private val db = Db

    fun create(item: ListItem): Boolean {
        val exist = db.listItem.find { it.id == item.id }
        if (exist == null) {
            item.id = db.idListItem++
            db.listItem.add(item)
            return true
        }
        return false
    }

    fun update(item: ListItem): Boolean {
        val index = db.listItem.indexOfFirst { it.id == item.id }
        return if (index != -1) {
            db.listItem[index] = item
            true
        } else {
            false
        }
    }

    fun delete(itemId: Int): Boolean {
        val item = db.listItem.find { it.id == itemId }
        return if (item != null) {
            db.listItem.remove(item)
            true
        } else {
            false
        }
    }

    fun deleteAllByListAggregator(listAggregatorId: Int) {
        db.listItem.removeAll { it.idListAggregator == listAggregatorId }
    }

    fun getAllByListAggregator(listAggregatorId: Int): List<ListItem> {
        return db.listItem.filter { it.idListAggregator == listAggregatorId }
    }

    fun getById(itemId: Int): ListItem? {
        return db.listItem.find { it.id == itemId }
    }

    fun filterByUserAndQuery(idAggregator: Int, query: String): List<ListItem> {
        return db.listItem.filter {
            it.idListAggregator == idAggregator && it.name.contains(query, ignoreCase = true)
        }
    }
}