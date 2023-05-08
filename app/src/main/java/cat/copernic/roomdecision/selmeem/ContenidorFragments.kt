package cat.copernic.roomdecision.selmeem

import Nova_Publicacio
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class ContenidorFragments : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contenidor_fragments)

        auth = FirebaseAuth.getInstance()

        val email = intent.getStringExtra("email")


        // Setegem la toolbar
        setSupportActionBar(findViewById(R.id.toolbar))

        // Habilitar botó de retrocés en la toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Configuració del Navigation Drawer
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.navigationView)
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
                            putString("email", email)
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