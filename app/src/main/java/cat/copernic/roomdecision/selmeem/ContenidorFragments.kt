package cat.copernic.roomdecision.selmeem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
}