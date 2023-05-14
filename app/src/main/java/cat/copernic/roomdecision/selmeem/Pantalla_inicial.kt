package cat.copernic.roomdecision.selmeem

import MyAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.roomdecision.selmeem.RVPublicacionsInici.Publicacio
import cat.copernic.roomdecision.selmeem.Utils.Companion.mostrarAlerta
import cat.copernic.roomdecision.selmeem.databinding.FragmentPantallaInicialBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Pantalla_inicial
 *
 * @constructor Create empty Pantalla_inicial
 */
class Pantalla_inicial : Fragment() {

    private var _binding: FragmentPantallaInicialBinding? = null
    private val binding get() = _binding!!


    private lateinit var publicacions: List<Publicacio>
    private lateinit var recyclerView: RecyclerView

    private val db = FirebaseFirestore.getInstance()

    /**
     * On create view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el disseny del fragment a partir del binding generat a partir del disseny
        _binding = FragmentPantallaInicialBinding.inflate(inflater, container, false)
        recyclerView = binding.RVPublicacions

        return binding.root
    }


    /**
     * On view created
     *
     * @param view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Obtenir les publicacions de Firestore
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = db.collection("publicacions").get().await()
                // Convertir documents en un objecte Publicacions i introduirlo a la llista
                publicacions = result.documents.mapNotNull { it.toObject(Publicacio::class.java) }
                // Configurar el RecyclerView
                val activity = getActivity()
                if (activity is ContenidorFragments) {
                    val sortedPublicacions = publicacions.sortedByDescending { it.dataPujada }
                    val contenidor = requireActivity() as ContenidorFragments
                    withContext(Dispatchers.Main) {
                        recyclerView.adapter = MyAdapter(sortedPublicacions, contenidor)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mostrarAlerta(requireContext(),"Error","Error al accedir a la base de dades")
                }
            }
        }


        binding.btnIniciarSessio3.setOnClickListener {
            db.collection("publicacions")
                .get()
                .addOnSuccessListener { result ->
                    // Convertir documents en un objecte Publicacions i introduirlo a la llista
                    publicacions =
                        result.documents.mapNotNull { it.toObject(Publicacio::class.java) }

                    val activity = getActivity()
                    if (activity is ContenidorFragments) {
                        val sortedPublicacions = publicacions.sortedByDescending { it.dataPujada }
                        val contenidor = requireActivity() as ContenidorFragments
                        recyclerView.adapter = MyAdapter(sortedPublicacions, contenidor)
                    }
                }
        }

        binding.btnIniciarSessio4.setOnClickListener {
            db.collection("publicacions")
                .get()
                .addOnSuccessListener { result ->
                    // Convertir documents en un objecte Publicacions i introduirlo a la llista
                    publicacions =
                        result.documents.mapNotNull { it.toObject(Publicacio::class.java) }

                    val activity = getActivity()
                    if (activity is ContenidorFragments) {
                        val sortedPublicacions = publicacions.sortedByDescending { it.like }
                        val contenidor = requireActivity() as ContenidorFragments
                        recyclerView.adapter = MyAdapter(sortedPublicacions, contenidor)
                    }
                }
        }
    }


    /**
     * On destroy view
     *
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Desvincular el binding per evitar fuites de memòria
        _binding = null
    }
}
