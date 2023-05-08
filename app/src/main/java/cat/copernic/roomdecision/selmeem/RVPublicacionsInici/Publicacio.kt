package cat.copernic.roomdecision.selmeem.RVPublicacionsInici

import java.util.*

data class Publicacio(
    val titol: String = "",
    val nomCreador: String = "",
    val imatge: String = "",
    val like: Int = 0,
    val dataPujada: Date = Date(),
) {
    // Constructor sin argumentos necesario para la deserialización de Firestore
    constructor() : this("", "", "", 0, Date())
}
