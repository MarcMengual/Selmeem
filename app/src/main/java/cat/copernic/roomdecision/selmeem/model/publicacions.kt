package cat.copernic.roomdecision.selmeem.model
import java.util.*

data class publicacions(
    val id: String,
    val titol: String,
    val imatge: String,
    val nomCreador: String,
    val dataPujada: Date,
    val like: Int = 0,
    val llistaLike: MutableList<String> = mutableListOf(),
    val llistaFavorits: MutableList<String> = mutableListOf()
)

