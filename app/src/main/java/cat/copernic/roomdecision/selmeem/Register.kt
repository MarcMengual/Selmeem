package cat.copernic.roomdecision.selmeem

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import cat.copernic.roomdecision.selmeem.databinding.ActivityRegisterBinding
import cat.copernic.roomdecision.selmeem.model.usuari
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.btnIniciarSessio.setOnClickListener {
            val nom = binding.edNom.text.toString()
            val password = binding.edPassw.text.toString()
            val repeatPassword = binding.edPassw2.text.toString()
            val email = binding.edEmail2.text.toString()
            val edat = binding.edEdat.text.toString()

            if (password == repeatPassword) {
                if (email.isNotEmpty() && password.isNotEmpty() && nom.isNotEmpty() && edat.isNotEmpty()) {
                    guardarUsuarioEnBD(email, nom, edat)
                } else {
                    showAlert("Debes rellenar todos los campos")
                }
            } else {
                showAlert("Las contraseñas no coinciden")
            }
        }

        binding.btnCancelar.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun guardarUsuarioEnBD(email: String, nom: String, edat: String) {
        firestore.collection("usuarios").document(email).get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                showAlert("El usuario ya existe")
            } else {
                val usuario = usuari(email, nom, edat, "default", emptyList())
                firestore.collection("usuarios").document(email).set(usuario)
                    .addOnSuccessListener {
                        Toast.makeText(applicationContext, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                        register(email, binding.edPassw.text.toString())
                    }
                    .addOnFailureListener {
                        showAlert("Error al registrar usuario")
                    }
            }
        }.addOnFailureListener {
            showAlert("Error al acceder a la base de datos")
        }
    }

    private fun register(email: String, password: String) {
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val signInMethods = task.result?.signInMethods ?: emptyList()
                if (signInMethods.isNotEmpty()) {
                    showAlert("El correo electrónico ya está en uso")
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val intent = Intent(this, ContenidorFragments::class.java)
                                intent.putExtra("email", email)
                                startActivity(intent)
                                finish()
                            } else {
                                showAlert("Error al crear cuenta de usuario")
                            }
                        }
                }
            } else {
                showAlert("Error al comprobar el correo electrónico")
            }
        }
    }

    private fun showAlert(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
