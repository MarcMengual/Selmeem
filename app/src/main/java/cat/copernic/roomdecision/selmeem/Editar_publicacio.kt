package cat.copernic.roomdecision.selmeem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cat.copernic.roomdecision.selmeem.databinding.FragmentEditarPublicacioBinding
import com.google.firebase.firestore.FirebaseFirestore


class Editar_publicacio(private val publicacioId: String) : Fragment() {

    private lateinit var binding: FragmentEditarPublicacioBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout del fragment
        binding = FragmentEditarPublicacioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()

        // Obtenir la referència de la publicació
        val publicacioRef = db.collection("publicacions").document(publicacioId)

        // Obtenir el valor actual del camp "titol" i establir-lo en el EditText "edNom"
        publicacioRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val titol = document.getString("titol")
                binding.edNom.setText(titol)
            }
        }

        // Configurar el botó d'eliminar
        binding.btnCancelar.setOnClickListener {
            // Eliminar la publicació
            publicacioRef.delete().addOnSuccessListener {
                val fragment = Pantalla_inicial()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.contenidorFragments1, fragment)
                transaction.commit()
            }
        }

        binding.btnCancelar2.setOnClickListener {
            val nouTitol = binding.edNom.text.toString().trim()
            if (nouTitol.isEmpty()) {
                // Mostrar missatge d'error si hi ha un error en actualitzar el camp "titol"
                Utils.mostrarError(requireContext(), "Introdueix un titol")
                return@setOnClickListener
            }
            publicacioRef.update("titol", nouTitol)
                .addOnSuccessListener {
                    val fragment = Pantalla_inicial()
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.contenidorFragments1, fragment)
                    transaction.commit()
                }
                .addOnFailureListener { e ->
                    // Mostrar missatge d'error si hi ha un error en actualitzar el camp "titol"
                    Utils.mostrarError(requireContext(), "Error al actualitzar el títol")
                }
        }

    }
}
