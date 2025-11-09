package com.br.listadecompras.data.model

enum class TypeUnitEnum(val label: String, val symbol: String) {
    UN("Unidade", "un"),
    CX("Caixa", "cx"),
    KG("Quilo", "kg"),
    G("Grama", "g"),
    L("Litro", "l"),
    ML("Mililitro", "ml"),
    PACOTE("Pacote", "pct"),
    FRASCO("Frasco", "fr"),
    POTE("Pote", "pt");

    override fun toString() = label

    companion object {
        fun fromLabel(label: String): TypeUnitEnum? =
            TypeUnitEnum.entries.firstOrNull { it.label.equals(label, ignoreCase = true) }

        fun fromSymbol(symbol: String): TypeUnitEnum? =
            TypeUnitEnum.entries.firstOrNull { it.symbol.equals(symbol, ignoreCase = true) }
    }
}
