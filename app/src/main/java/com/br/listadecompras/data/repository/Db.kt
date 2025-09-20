package com.br.listadecompras.data.repository

import com.br.listadecompras.data.model.ListItemAggregator
import com.br.listadecompras.data.model.User

object Db {
    var idUser = 0
    var idListAggregator = 0
    var users = mutableListOf<User>()
    var listAggregator = mutableListOf<ListItemAggregator>()

    init {
        users.add(User(1, "i", "i", "i"))
    }

}