package com.br.listadecompras.data.model

import TypeCategoryEnum

data class ListItem(
    var id: String? = null,
    val name: String = "",
    val qtde: Int = 0,
    val unit: TypeUnitEnum = TypeUnitEnum.UN,
    val category: TypeCategoryEnum = TypeCategoryEnum.OUTROS,
    val checked: Boolean = false,
    val idListAggregator: String = ""
)