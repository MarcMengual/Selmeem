package cat.copernic.roomdecision.selmeem

import MyAdapter
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.roomdecision.selmeem.RVPublicacionsInici.Publicacio
import cat.copernic.roomdecision.selmeem.databinding.FragmentPerfilBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class Perfil : Fragment() {

    // Declaració de variables de classe
    private lateinit var email: String
    private lateinit var imageView: ImageView
    private lateinit var btnIniciarSessio3: Button
    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var publicacions: List<Publicacio>
    private val db = FirebaseFirestore.getInstance()


    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Infla el disseny del fragment
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)

        recyclerView = binding.recyclerViewPerfil

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)

        // Obtenir les publicacions de Firestore

        email = FirebaseAuth.getInstance().currentUser?.email.toString()


        db.collection("publicacions")
            .whereArrayContains("llistaFavorits", email)
            .get()
            .addOnSuccessListener { result ->
                // Convertir documents en un objecte Publicacions i introduirlo a la llista
                publicacions = result.documents.mapNotNull { it.toObject(Publicacio::class.java) }
                val activity = getActivity()
                if (activity is ContenidorFragments) {
                    val sortedPublicacions = publicacions.sortedByDescending { it.dataPujada }
                    val contenidor = requireActivity() as ContenidorFragments
                    recyclerView.adapter = MyAdapter(sortedPublicacions, contenidor)
                }
            }


        // Inicialitza les variables de classe
        imageView = view.findViewById<ImageView>(R.id.imageViewPerf)
        btnIniciarSessio3 = view.findViewById<Button>(R.id.btnIniciarSessio3)

        // Obté la referència a la col·lecció "usuarios"
        val db = Firebase.firestore
        val usersRef = db.collection("usuarios")

        // Obté les dades de l'usuari actual i actualitza la vista
        FirebaseAuth.getInstance().currentUser?.let { user ->
            GlobalScope.launch(Dispatchers.IO) {
                val documentSnapshot = usersRef.document(user.email!!).get().await()
                if (documentSnapshot.exists()) {
                    val name = documentSnapshot.getString("nom")
                    val image = documentSnapshot.getString("imatge")

                    // Actualitza el TextView amb el nom de l'usuari
                    withContext(Dispatchers.Main) {
                        val nameTextView = view.findViewById<TextView>(R.id.nomText)
                        nameTextView.text = name
                    }

                    // Carrega la imatge des de Firebase Storage i actualitza el ImageView
                    withContext(Dispatchers.IO) {
                        val storageRef = Firebase.storage.reference
                        val imageRef = storageRef.child(image!!)
                        val bytes = imageRef.getBytes(Long.MAX_VALUE).await()
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        withContext(Dispatchers.Main) {
                            imageView.setImageBitmap(bitmap)
                        }
                    }
                }
            }
        }

        // Afegir un listener de clic al botó de perfil
        btnIniciarSessio3.setOnClickListener { view ->
            val popupMenu = PopupMenu(requireContext(), view)
            popupMenu.menuInflater.inflate(R.menu.perfil_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menuCambiarImagen -> {
                        // Obre la galeria
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
                        true
                    }
                    R.id.menuEditarPerfil -> {
                        // Canvia el fragment a editarPerfil
                        val EditarPerfilFragment = EditarPerfil()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.contenidorFragments1, EditarPerfilFragment)
                            .addToBackStack(null)
                            .commit()
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()

        }
    }

    // Métode que s'executa quan l'usuari ha seleccionat una imatge de la galería
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data

            // Pujar la imatge a Firebase Storage
            val storageRef = Firebase.storage.reference
            val imageName = "${email}_${System.currentTimeMillis()}"
            val imageRef = storageRef.child(imageName)

            // Afegir un listener d'èxit i fracàs a la pujada de la imatge
            imageRef.putFile(imageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    // Obtenir el nom de la imatge i actualitzar-lo a la base de dades
                    val db = Firebase.firestore
                    db.collection("usuarios").document(email).update("imatge", imageName)
                        .addOnSuccessListener {
                            // Actualitzar la imatge de perfil a la vista
                            val imageView = view?.findViewById<ImageView>(R.id.imageViewPerf)
                            imageView?.let {
                                Glide.with(requireContext())
                                    .load(imageUri)
                                    .into(it)
                            }
                        }
                        .addOnFailureListener { e ->
                            // Mostrar missatge d'error si hi ha un error en actualitzar l'imatge
                            Utils.mostrarError(requireContext(), "Error actualitzant l'imatge")
                        }
                }
                .addOnFailureListener { e ->
                    // Mostrar missatge d'error si hi ha un error al pujar l'imatge
                    Utils.mostrarError(requireContext(), "Error al pujar imatge")
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Desvincular el binding para evitar fuites de memòria
        _binding = null
    }
}