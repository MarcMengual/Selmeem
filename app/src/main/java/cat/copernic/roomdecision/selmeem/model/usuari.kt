package cat.copernic.roomdecision.selmeem.model

data class usuari(
    val Email: String = "",
    val Nom: String = "",
    val Edat: String = "",
    val Imatge: String = "default",
    val Favorits: List<String> = emptyList()
)
