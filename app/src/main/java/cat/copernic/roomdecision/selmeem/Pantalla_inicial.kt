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
import cat.copernic.roomdecision.selmeem.databinding.FragmentPantallaInicialBinding
import com.google.firebase.firestore.FirebaseFirestore

class Pantalla_inicial : Fragment() {

    private var _binding: FragmentPantallaInicialBinding? = null
    private val binding get() = _binding!!

    private lateinit var publicacions: List<Publicacio>
    private lateinit var recyclerView: RecyclerView

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el disseny del fragment a partir del binding generat a partir del disseny
        _binding = FragmentPantallaInicialBinding.inflate(inflater, container, false)
        recyclerView = binding.RVPublicacions
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Obtener las publicaciones de Firestore
        db.collection("publicacions")
            .get()
            .addOnSuccessListener { result ->
                // Convertir cada documento en un objeto Publicacion y añadirlo a la lista
                publicacions = result.documents.mapNotNull { it.toObject(Publicacio::class.java) }
                // Configurar el RecyclerView
                recyclerView.adapter = MyAdapter(publicacions)
            }

        binding.btnIniciarSessio3.setOnClickListener {
            val sortedPublicacions = publicacions.sortedByDescending { it.dataPujada }
            recyclerView.adapter = MyAdapter(sortedPublicacions)
        }

        binding.btnIniciarSessio4.setOnClickListener {
            val sortedPublicacions = publicacions.sortedByDescending { it.like }
            recyclerView.adapter = MyAdapter(sortedPublicacions)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        // Desvincular el binding para evitar fuites de memòria
        _binding = null
    }
}
