package cat.copernic.roomdecision.selmeem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import cat.copernic.roomdecision.selmeem.databinding.ActivityContenidorFragmentsBinding

class ContenidorFragments : AppCompatActivity() {
    private lateinit var binding: ActivityContenidorFragmentsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContenidorFragmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.contenidorFragments, Pantalla_inicial())
                .commit()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

/*    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_pantalla_inicial_to_perfil ->
        }
        return super.onOptionsItemSelected(item)
    }*/
}