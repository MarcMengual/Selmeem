package cat.copernic.roomdecision.selmeem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import cat.copernic.roomdecision.selmeem.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        binding =ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnIniciarSessio.setOnClickListener {
            val name = binding.edNom.text.toString()
            val password = binding.edPassw.text.toString()
            val repeatPassword = binding.edPassw2.text.toString()
            val email = binding.edEmail2.text.toString()
            val edat = binding.edEdat.text.toString()


            if (password.equals(repeatPassword)) {
                if (email.isNotEmpty() && password.isNotEmpty() && repeatPassword.isNotEmpty() && name.isNotEmpty() && edat.isNotEmpty()){
                    register(email, password)
                } else {
                    showAlertt()
                }
            }
        }

        binding.btnCancelar.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    private fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, ContenidorFragments::class.java))
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

    private fun showAlertt() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("XD")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}