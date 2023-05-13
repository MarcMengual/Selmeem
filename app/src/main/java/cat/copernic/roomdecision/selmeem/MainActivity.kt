package cat.copernic.roomdecision.selmeem


import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.PopupMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import cat.copernic.roomdecision.selmeem.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Infla el layout de l'activitat utilitzant binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicialitza l'objecte auth amb l'autenticació de Firebase
        auth = Firebase.auth

        // Recuperar el idioma seleccionat de las SharedPreferences
        val prefs = applicationContext.getSharedPreferences("Idioma", Context.MODE_PRIVATE)
        val idioma = prefs.getString("idioma", "default")

        // Actualitzar la configuració regional de la aplicació
        val locale = if (idioma == "default") {
            // Si no s'ha seleccionat un idioma, utilizar el idioma del sistema
            Locale.getDefault()
        } else {
            // Si s'ha seleccionat un idioma, utilizar el idioma seleccionat
            Locale(idioma)
        }
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)



        // Vincula les vistes amb les propietats corresponents a través de binding
        val edEmail = binding.edEmail
        val edPassw = binding.edPassw
        val btnIniciarSessio = binding.btnIniciarSessio
        val btnRecuperarContrasenya = binding.btnRecuperarContrasenya
        val btnRegistrar = binding.btnRegistrar

        // Comprova si l'usuari ja te la sessió iniciada
        checkIfUserIsLogged()

        //Botó Iniciar Sessió
        btnIniciarSessio.setOnClickListener {
            val email = edEmail.text.toString()
            val password = edPassw.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Intenta iniciar sessió amb les credencials proporcionades per l'usuari
                loginAc(email, password)

            } else {
                // Si el correu electrònic o la contrasenya estan buits, mostra un error
                Utils.mostrarAlerta(this, "Error", "S'ha produït un error en el login")
            }
        }

        //Botó Recuperar Contrasenya
        btnRecuperarContrasenya.setOnClickListener {
            startActivity(Intent(this, RecuperarContrasenya::class.java))
            finish()
        }

        // Botó Registrar
        btnRegistrar.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
            finish()
        }

        binding.idiom.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            popupMenu.menuInflater.inflate(R.menu.menu_idioma, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menuCatalan -> {
                        // Canviar la configuració regional a català
                        val locale = Locale("ca")
                        Locale.setDefault(locale)
                        val config = Configuration()
                        config.locale = locale
                        resources.updateConfiguration(config, resources.displayMetrics)

                        // Guardar el idioma seleccionat en les SharedPreferences
                        val prefs = this.getSharedPreferences("Idioma", Context.MODE_PRIVATE)
                        prefs.edit().putString("idioma", locale.language).apply()


                        // Recrea l'activitat perquè els canvis d'idioma tinguin efecte
                        this.recreate()
                        true
                    }
                    R.id.menuCastellano -> {
                        // Canviar la configuració regional a castellà
                        val locale = Locale("es")
                        Locale.setDefault(locale)
                        val config = Configuration()
                        config.locale = locale
                        resources.updateConfiguration(config, resources.displayMetrics)

                        binding.idiom.setImageResource(R.drawable.castellaflag)

                        // Guardar el idioma seleccionat en les SharedPreferences
                        val prefs = this.getSharedPreferences("Idioma", Context.MODE_PRIVATE)
                        prefs.edit().putString("idioma", locale.language).apply()

                        // Recrea l'activitat perquè els canvis d'idioma tinguin efecte
                        this.recreate()
                        true
                    }
                    R.id.menuIngles -> {
                        // Canviar la configuració regional a anglès
                        val locale = Locale("en")
                        Locale.setDefault(locale)
                        val config = Configuration()
                        config.locale = locale
                        resources.updateConfiguration(config, resources.displayMetrics)

                        // Guardar el idioma seleccionat en les SharedPreferences
                        val prefs = this.getSharedPreferences("Idioma", Context.MODE_PRIVATE)
                        prefs.edit().putString("idioma", locale.language).apply()

                        binding.idiom.setImageResource(R.drawable.regneflag)

                        // Recrea l'activitat perquè els canvis d'idioma tinguin efecte
                        this.recreate()

                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }

        val currentLocale = resources.configuration.locale
        when (currentLocale) {
            Locale("ca") -> binding.idiom.setImageResource(R.drawable.catalaflag)
            Locale("es") -> binding.idiom.setImageResource(R.drawable.castellaflag)
            else -> binding.idiom.setImageResource(R.drawable.regneflag)
        }


    }

    // Comprova si l'usuari ja ha iniciat sessió anteriorment
    private fun checkIfUserIsLogged() {
        if (auth.currentUser != null) {
            // Si l'usuari ja ha iniciat sessió, inicia l'activitat ContenidorFragments
            val currentUser = auth.currentUser
            val intent = Intent(this, ContenidorFragments::class.java)
            intent.putExtra("user", currentUser?.email)
            startActivity(intent)
            finish()
        }
    }

    // Inicia sessió amb les credencials proporcionades per l'usuari
    private fun loginAc(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Si l'autenticació ha estat correcta, inicia l'activitat ContenidorFragments
                    val currentUser = auth.currentUser
                    val intent = Intent(this, ContenidorFragments::class.java)
                    intent.putExtra("user", currentUser?.email)
                    startActivity(intent)
                    finish()
                } else {
                    Utils.mostrarAlerta(this, "Error", "S'ha produït un error en el login")
                }
            }
    }
}


/*     // Crear un canal de notificacions
     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         val name = getString(R.string.Likes)
         val descriptionText = getString(R.string.DescripcioNoti)
         val importance = NotificationManager.IMPORTANCE_DEFAULT
         val channel = NotificationChannel("1", name, importance).apply {
             description = descriptionText
         }
         // Registrar el canal de notificacions en el sistema
         val notificationManager: NotificationManager =
             getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
         notificationManager.createNotificationChannel(channel)
     }

     // Crear notificació
     val builder = NotificationCompat.Builder(this, "1")
         .setSmallIcon(R.drawable.notificacio)
         .setContentTitle("Tens un like!")
         .setContentText("Un usuari ha donat like a una publicacio teva!")
         .setPriority(NotificationCompat.PRIORITY_DEFAULT)

     // Mostrar la notificació
     with(NotificationManagerCompat.from(this)) {
         notify(1, builder.build())
     }


*/
