package com.br.listadecompras.data.model

import java.util.Date

data class ListItemAggregator(
    var id: Int?, val imageResId: Int, val name: String, val createdAt: Date?
)