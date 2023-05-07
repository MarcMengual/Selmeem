package cat.copernic.roomdecision.selmeem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import cat.copernic.roomdecision.selmeem.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Infla el layout de l'activitat utilitzant binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        // Vincula les vistes amb les propietats corresponents a través de binding
        val edEmail = binding.edEmail
        val edPassw = binding.edPassw
        val btnIniciarSessio = binding.btnIniciarSessio
        val btnRecuperarContrasenya = binding.btnRecuperarContrasenya
        val btnRegistrar = binding.btnRegistrar

        checkIfUserIsLogged()

        btnIniciarSessio.setOnClickListener {
            val email = edEmail.text.toString()
            val password = edPassw.text.toString()
            if(email.isNotEmpty() && password.isNotEmpty()){
                loginAc(email, password)

            }else{
                showAlert()
            }
        }

        btnRecuperarContrasenya.setOnClickListener {
            startActivity(Intent(this, RecuperarContrasenya::class.java))
            finish()
        }

        btnRegistrar.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
            finish()
        }
    }

    // Comprova si l'usuari ja ha iniciat sessió anteriorment
    private fun checkIfUserIsLogged(){
        if(auth.currentUser != null){
            val currentUser = auth.currentUser
            val intent = Intent(this, ContenidorFragments::class.java)
            intent.putExtra("user", currentUser?.email)
            startActivity(intent)
            finish()
        }
    }

    // Inicia sessió amb les credencials proporcionades per l'usuari
    private fun loginAc(email: String,password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    val currentUser = auth.currentUser
                    val intent = Intent(this, ContenidorFragments::class.java)
                    intent.putExtra("user", currentUser?.email)
                    startActivity(intent)
                    finish()
                }else{
                    showAlert()
                }
            }
    }

    // Mostra una alerta en cas d'error en el login
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("S'ha produït un error en el login")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
