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

class RecuperarContrasenya : AppCompatActivity() {

    private lateinit var edEmail: EditText
    private lateinit var btnIniciarSessio: Button
    private lateinit var btnIniciarSessio2: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_contrasenya)

        auth = Firebase.auth

        edEmail = findViewById(R.id.edEmail)
        btnIniciarSessio = findViewById(R.id.btnIniciarSessio)
        btnIniciarSessio2 = findViewById(R.id.btnIniciarSessio2)

        btnIniciarSessio.setOnClickListener {
            auth.sendPasswordResetEmail(edEmail.text.toString()).addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    Toast.makeText(this, "Enviat", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "No Enviat", Toast.LENGTH_SHORT).show()
                }
            }

        }

        btnIniciarSessio2.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }
}