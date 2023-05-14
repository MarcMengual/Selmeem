package cat.copernic.roomdecision.selmeem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Recuperar contrasenya
 *
 * @constructor Create empty Recuperar contrasenya
 */
class RecuperarContrasenya : AppCompatActivity() {

    // Declarem els elements de la UI que farem servir
    private lateinit var edEmail: EditText
    private lateinit var btnIniciarSessio: Button
    private lateinit var btnIniciarSessio2: Button

    // Declarem l'objecte d'autenticació de Firebase
    private lateinit var auth: FirebaseAuth

    /**
     * On create
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_contrasenya)

        // Inicialitzem l'objecte d'autenticació
        auth = Firebase.auth

        // Busquem els elements de la UI pel seu identificador i els assignem a les variables corresponents
        edEmail = findViewById(R.id.edEmail)
        btnIniciarSessio = findViewById(R.id.btnIniciarSessio)
        btnIniciarSessio2 = findViewById(R.id.btnIniciarSessio2)

        // Afegim un listener de clic al botó d'enviar correu de recuperació de contrasenya
        btnIniciarSessio.setOnClickListener {
            val email = edEmail.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Enviem el correu de recuperació de contrasenya
                    auth.sendPasswordResetEmail(email).await()
                    withContext(Dispatchers.Main) {
                        // Si s'ha enviat el correu amb èxit, mostrem un missatge d'èxit
                        Toast.makeText(this@RecuperarContrasenya, "Enviat", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        // Si no s'ha pogut enviar el correu, mostrem un missatge d'error
                        Toast.makeText(this@RecuperarContrasenya, "No Enviat", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Afegim un listener de clic al botó de tornar i anem a l'activitat principal, finalitzem aquesta activitat perquè no es pugui tornar enrere
        btnIniciarSessio2.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

}
