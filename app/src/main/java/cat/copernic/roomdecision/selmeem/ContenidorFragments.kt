package cat.copernic.roomdecision.selmeem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import cat.copernic.roomdecision.selmeem.databinding.ActivityContenidorFragmentsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class ContenidorFragments : AppCompatActivity() {
    private lateinit var binding: ActivityContenidorFragmentsBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContenidorFragmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.contenidorFragments1, Pantalla_inicial())
                .commit()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toHome -> {
                // Replace the contents of the container with the new fragment
                supportFragmentManager.beginTransaction()
                    .replace(R.id.contenidorFragments1, Pantalla_inicial())
                    .addToBackStack(null)
                    .commit()
                true
            }
            R.id.action_pantalla_inicial_to_perfil -> {
                // Replace the contents of the container with the new fragment
                supportFragmentManager.beginTransaction()
                .replace(R.id.contenidorFragments1, Perfil())
                .addToBackStack(null)
                .commit()
                true
            }
            R.id.action_pantalla_inicial_to_nova_Publicacio -> {
                // Replace the contents of the container with the new fragment
                supportFragmentManager.beginTransaction()
                .replace(R.id.contenidorFragments1, Nova_Publicacio())
                .addToBackStack(null)
                .commit()
                true
            }
            R.id.logOut -> {
                auth.signOut()
                startActivity(Intent(this,MainActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}