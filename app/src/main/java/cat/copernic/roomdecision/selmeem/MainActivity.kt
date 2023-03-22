package cat.copernic.roomdecision.selmeem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var edEmail: EditText
    private lateinit var edPassw: EditText
    private lateinit var btnIniciarSessio: Button
    private lateinit var btnRecuperarContrasenya: Button
    private lateinit var btnRegistrar: Button

    private lateinit var auth:FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        edEmail = findViewById(R.id.edEmail)
        edPassw = findViewById(R.id.edPassw)
        btnIniciarSessio = findViewById(R.id.btnIniciarSessio)
        btnRecuperarContrasenya = findViewById(R.id.btnRecuperarContrasenya)
        btnRegistrar = findViewById(R.id.btnRegistrar)



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


    private fun checkIfUserIsLogged(){
        if(auth.currentUser != null){
            val currentUser = auth.currentUser
            val intent = Intent(this, ContenidorFragments::class.java)
            intent.putExtra("user", currentUser?.email)
            startActivity(intent)
            finish()
        }
    }

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


    private fun showAlert(){

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("S'ha produit un error en el login")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}