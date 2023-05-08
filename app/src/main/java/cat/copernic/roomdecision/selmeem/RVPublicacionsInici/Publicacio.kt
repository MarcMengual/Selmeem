package cat.copernic.roomdecision.selmeem.RVPublicacionsInici

import java.util.*

data class Publicacio(
    val titol: String = "",
    val nomCreador: String = "",
    val imatge: String = "",
    var like: Int = 0,
    val dataPujada: Date = Date(),
    val llistaLike: MutableList<String> = mutableListOf(),
    val id: String = "",
    var usuarioYaDioLike: Boolean = false

)
