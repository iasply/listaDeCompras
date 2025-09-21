package com.br.listadecompras.data.model

data class ListItem(
    var id: Int?,
    val name: String,
    val qtde: Int,
    val unit: TypeUnitEnum,
    val category: TypeCategoryEnum,
    val checked: Boolean,
    val idListAggregator: Int
)
