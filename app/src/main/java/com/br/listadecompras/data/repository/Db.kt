package com.br.listadecompras.data.repository

import com.br.listadecompras.data.model.ListItem
import com.br.listadecompras.data.model.ListItemAggregator
import com.br.listadecompras.data.model.User

object Db {
    var idUser = 1
    var idListAggregator = 1
    var idListItem = 1
    var users = mutableListOf<User>()
    var listAggregator = mutableListOf<ListItemAggregator>()
    var listItem = mutableListOf<ListItem>()

    init {
        users.add(User(1, "i", "i", "i"))
    }

}