package cat.copernic.roomdecision.selmeem

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import cat.copernic.roomdecision.selmeem.databinding.FragmentEditarPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditarPerfil : Fragment() {

    private lateinit var edPassw: EditText
    private lateinit var binding: FragmentEditarPerfilBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragment
        binding = FragmentEditarPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Botó per cancel·lar l'acció
        val btnCancelar = binding.btnCancelar
        btnCancelar.setOnClickListener {
            val PerfilFragment = Perfil()
            parentFragmentManager.beginTransaction()
                .replace(R.id.contenidorFragments1, PerfilFragment)
                .addToBackStack(null)
                .commit()
        }

        // Botó per actualitzar el nom d'usuari
        val btnCancelar2 = binding.btnCancelar2
        edPassw = binding.edPassw
        btnCancelar2.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser
            val email = user?.email

            // Comprova que el camp no estigui buit
            if (edPassw.text.toString().isEmpty()) {
                // Mostrar missatge d'error si hi ha un error en actualitzar el nom
                Utils.mostrarError(requireContext(), "El camp no pot estar buit!")
            } else {
                // Actualitza el nom d'usuari a Firebase
                db.collection("usuarios").document(email!!).update("nom", edPassw.text.toString())
                    .addOnSuccessListener {
                        // Torna a la pantalla de perfil
                        val PerfilFragment = Perfil()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.contenidorFragments1, PerfilFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                    .addOnFailureListener {
                        // Mostrar missatge d'error si hi ha un error en actualitzar el nom
                        Utils.mostrarError(requireContext(), "Error actualitzant el nom")
                    }
            }
        }
    }
}