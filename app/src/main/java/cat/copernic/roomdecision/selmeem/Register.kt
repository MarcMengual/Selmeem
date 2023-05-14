package cat.copernic.roomdecision.selmeem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import cat.copernic.roomdecision.selmeem.databinding.ActivityRegisterBinding
import cat.copernic.roomdecision.selmeem.model.usuari
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


/**
 * Register
 *
 * @constructor Create empty Register
 */
class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    /**
     * On create
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflem el layout amb el binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Quan es fa clic al botó d'iniciar sessió, agafem les dades dels camps i cridem una funció per a guardar l'usuari a la base de dades
        binding.btnIniciarSessio.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmar registro")
            builder.setMessage(getString(R.string.AvisPolitiques))
            builder.setPositiveButton("Aceptar") { dialog, which ->

                val nom = binding.edNom.text.toString()
                val password = binding.edPassw.text.toString()
                val repeatPassword = binding.edPassw2.text.toString()
                val email = binding.edEmail2.text.toString()
                val edat = binding.edEdat.text.toString()

                if (password == repeatPassword) {
                    if (email.isNotEmpty() && password.isNotEmpty() && nom.isNotEmpty() && edat.isNotEmpty()) {
                        // Verificar si el format del correo electrónic utiliza una expresió regular
                        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
                        if (email.matches(emailRegex)) {
                            //La contrasenya te que tenir una majuscula minim, un numero minim i una longitud minima de 8 caracters
                            val regex = Regex("^(?=.*[A-Z])(?=.*\\d).{8,}\$")
                            if (password.matches(regex)) {
                                // Cridem la funció per guardar l'usuari a la base de dades
                                guardarUsuarioEnBD(email, nom, edat, password)
                            } else {
                                Utils.mostrarAlerta(
                                    this,
                                    "Error",
                                    "La contrasenya te que contenir com a minim una lletra majúscula, un número i tenir 8 caracteres de longitud"
                                )
                            }
                        } else {
                            Utils.mostrarAlerta(
                                this,
                                "Error",
                                "El correu electrònic no té un format vàlid"
                            )
                        }
                    } else {
                        Utils.mostrarAlerta(this, "Error", getString(R.string.errorCampsBuits))
                    }
                } else {
                    Utils.mostrarAlerta(this, "Error", getString(R.string.errorContrasenyes))
                }

            }


            builder.setNegativeButton(getString(R.string.noAceptar)) { dialog, which ->


            }
            builder.setNeutralButton(getString(R.string.llegirPolitiques)) { dialog, which ->
                // Redirigir al usuari a la página de polítiques de privacitat
                startActivity(Intent(this, Politiques::class.java))
                finish()
            }


            // Quan es fa clic al botó de cancel·lar, tornem a l'activitat principal
            binding.btnCancelar.setOnClickListener {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            val dialog = builder.create()
            dialog.show()

        }
    }

    // Funció per a guardar l'usuari a la base de dades
    private fun guardarUsuarioEnBD(email: String, nom: String, edat: String, password: String) {
        val db = FirebaseFirestore.getInstance()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val documentSnapshot = db.collection("usuarios").document(email).get().await()
                if (documentSnapshot.exists()) {
                    // Mostrem una alerta si l'usuari ja existeix
                    withContext(Dispatchers.Main) {
                        showAlert("L'usuari ja exsisteix")
                    }
                } else {
                    // Creem un objecte  i guardem l'usuari a la base de dades
                    val usuario = usuari(email, nom, edat, "default.PNG", emptyList())
                    db.collection("usuarios").document(email).set(usuario).await()
                    withContext(Dispatchers.Main) {
                        // Cridem la funció per registrar l'usuari amb Firebase
                        register(email, password)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Mostrem una alerta si hi ha hagut un error en accedir a la base de dades
                    showAlert("Error al accedir a la base de dades")
                }
            }
        }
    }

    /**
     * Register
     *
     * @param email
     * @param password
     */
    private fun register(email: String, password: String) {
        // Creem un nou usuari amb el correo i la contrasenya
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Si el registre es exitos, iniciem sesió amb el nou usuari
                    val intent = Intent(this@Register, ContenidorFragments::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                    finish()
                } else {
                    // Si el registre falla, mostrem un error
                    Utils.mostrarAlerta(this, "Error", "Error al registrar el usuario")
                }
            }
    }


    /**
     * Show alert
     *
     * @param message
     */
    private fun showAlert(message: String) {
        Utils.mostrarAlerta(this, "Error", message)
    }
}