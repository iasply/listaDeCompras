package com.br.listadecompras.data.model

import java.util.Date

data class ListItemAggregator(
    var id: Int?, val imageUri: String, val name: String, val createdAt: Date,val idUser : Int
)