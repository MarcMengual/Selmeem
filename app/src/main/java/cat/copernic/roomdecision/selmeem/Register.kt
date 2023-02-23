package cat.copernic.roomdecision.selmeem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity() {

    private lateinit var edNom: EditText
    private lateinit var edPassw: EditText
    private lateinit var edPassw2: EditText
    private lateinit var edEmail2: EditText
    private lateinit var edEdat: EditText
    private lateinit var btnIniciarSessio: Button
    private lateinit var btnCancelar: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth

        edNom = findViewById(R.id.edNom)
        edPassw = findViewById(R.id.edPassw)
        edPassw2 = findViewById(R.id.edPassw2)
        edEmail2 = findViewById(R.id.edEmail2)
        edEdat = findViewById(R.id.edEdat)
        btnIniciarSessio = findViewById(R.id.btnIniciarSessio)
        btnCancelar = findViewById(R.id.btnCancelar)

        btnIniciarSessio.setOnClickListener {
            val email = edEmail2.text.toString()
            val password = edPassw.text.toString()
            val repeatPassword = edPassw2.text.toString()
            val name = edNom.text.toString()
            val edat = edEdat.text.toString()


            if (password.equals(repeatPassword)) {
                if (email.isNotEmpty() && password.isNotEmpty() && repeatPassword.isNotEmpty() && name.isNotEmpty() && edat.isNotEmpty()){
                    register(email, password)
                } else {
                    showAlert()
                }
            }
        }

        btnCancelar.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    private fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, Pantalla_inicial::class.java))
                    finish()
                } else {
                    showAlert()
                }

            }
    }

    private fun showAlert() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("S'ha produit un error en la creació del compte")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}