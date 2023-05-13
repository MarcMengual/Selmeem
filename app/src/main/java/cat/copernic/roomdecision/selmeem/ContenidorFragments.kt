package cat.copernic.roomdecision.selmeem

import Nova_Publicacio
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import androidx.appcompat.app.AlertDialog


class ContenidorFragments : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contenidor_fragments)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val emailDb = FirebaseAuth.getInstance().currentUser?.email

        // Setegem la toolbar
        setSupportActionBar(findViewById(R.id.toolbar))

        // Habilitar botó de retrocés en la toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Configuració del Navigation Drawer
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.navigationView)

        // Obteneim la referencia al header del NavigationView
        val headerView = navView.getHeaderView(0)
        val profileImage = headerView.findViewById<ImageView>(R.id.profile_image)
        val profileName = headerView.findViewById<TextView>(R.id.profile_name)
        val profilEmail = headerView.findViewById<TextView>(R.id.profile_email)
        val imatgeAdmin = headerView.findViewById<ImageView>(R.id.adminMode)


        // Obtenim el nom de l'arxiu de la imatge de Firestore
        val userDocRef = db.collection("usuarios").document(emailDb!!)
        userDocRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val imageName = document.getString("imatge")
                if (imageName != null) {
                    val storageRef = Firebase.storage.reference.child("$imageName")
                    storageRef.getBytes(1024 * 1024).addOnSuccessListener { bytes ->
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        profileImage.setImageBitmap(bitmap)
                    }.addOnFailureListener { exception ->
                        Log.e(TAG, "Error al descarregar l'imatge del Storage: $exception")
                    }
                }
            } else {
                Log.d(TAG, "El document no existeix o no s'ha trobat")
            }
        }

        val user = FirebaseAuth.getInstance().currentUser

        //Si comptes com administrador (que es ficar el correo) et sortira l'imatge

        if (user?.email == "mengualmarcgesti@gmail.com") {
            imatgeAdmin.visibility = View.VISIBLE
        } else {
            imatgeAdmin.visibility = View.GONE
        }

        imatgeAdmin.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Aquest icone significa que ets administrador de l'aplicació")
                .setCancelable(true)
                .setPositiveButton("Aceptar", null)
            val dialog = builder.create()
            dialog.show()
        }






        // Obtenim el nom de l'usuari desde Firestore
        userDocRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val name = document.getString("nom")
                if (name != null) {
                    // Establecer el nombre del usuario en el TextView correspondiente
                    profileName.text = name
                }
            } else {
                Log.d(TAG, "El document no existeix")
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Error al obtenir el document de l'usuari: $exception")
        }

        // Obtenim el gmail de l'usuari desde Firestore
        userDocRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val name = document.getString("email")
                if (name != null) {
                    // Fiquem l'email de l'usuari en el TextView corresponent.
                    profilEmail.text = name
                }
            } else {
                Log.d(TAG, "El document no existeix")
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Error al obtener el document de l'usuari: $exception")
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            val id = menuItem.itemId
            when (id) {
                R.id.toHome -> {
                    val fragment = Pantalla_inicial()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.contenidorFragments1, fragment).commit()
                    drawerLayout.closeDrawers()
                    return@setNavigationItemSelectedListener true
                }
                R.id.action_pantalla_inicial_to_perfil -> {
                    val fragment = Perfil().apply {
                        arguments = Bundle().apply {
                            putString("email", emailDb)
                        }
                    }
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.contenidorFragments1, fragment).commit()
                    drawerLayout.closeDrawers()
                    return@setNavigationItemSelectedListener true
                }

                R.id.action_pantalla_inicial_to_nova_Publicacio -> {
                    val fragment = Nova_Publicacio()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.contenidorFragments1, fragment).commit()
                    drawerLayout.closeDrawers()
                    return@setNavigationItemSelectedListener true
                }
                R.id.logOut -> {
                    auth.signOut()

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
            false
        }
        // Establecer ic_launcher como icono del botón de navegación
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.open_drawer, R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_launchermenu)
    }

    // Configuració de la toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    // Acció en prémer el botó de retrocés de la toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}