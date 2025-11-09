enum class TypeCategoryEnum(val displayName: String, val colorHex: String) {
    FRUTA("Frutas", "#FF7043"),
    VERDURA("Verduras", "#66BB6A"),
    CARNE("Carnes", "#E53935"),
    PEIXE("Peixes", "#42A5F5"),
    LACTEO("Laticínios", "#FFD54F"),
    PANIFICADO("Pães e Massas", "#FFB74D"),
    BEBIDA("Bebidas", "#4DD0E1"),
    DOCES("Doces e Sobremesas", "#EC407A"),
    GRAOS("Grãos", "#FFCA28"),
    HIGIENE("Higiene e Limpeza", "#9E9E9E"),
    OUTROS("Outros", "#BDBDBD");

    override fun toString() = displayName
}
