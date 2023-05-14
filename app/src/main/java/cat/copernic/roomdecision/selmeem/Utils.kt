package cat.copernic.roomdecision.selmeem

import android.app.AlertDialog
import android.content.Context
import android.util.Log

/**
 * Utils
 *
 * @constructor Create empty Utils
 */
class Utils {
    companion object {
        /**
         * Mostrar error
         *
         * @param context
         * @param mensaje
         */
        fun mostrarError(context: Context, mensaje: String) {
            // Lógica para mostrar el error, por ejemplo:
            Log.e("ERROR", mensaje)
            AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(mensaje)
                .setPositiveButton("OK", null)
                .show()
        }

        fun mostrarAlerta(context: Context, titulo: String, mensaje: String) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(titulo)
            builder.setMessage(mensaje)
            builder.setPositiveButton("Aceptar", null)
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }
}
