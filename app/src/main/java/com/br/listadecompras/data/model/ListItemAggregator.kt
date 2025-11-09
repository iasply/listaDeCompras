package com.br.listadecompras.data.model

import java.util.Date

data class ListItemAggregator(
    var id: String? = null,
    val imageUri: String? = null,
    val name: String = "",
    val createdAt: Date = Date(),
    val idUser: String = ""
)
