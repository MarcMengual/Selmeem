import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import cat.copernic.roomdecision.selmeem.Pantalla_inicial
import cat.copernic.roomdecision.selmeem.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import java.util.*
import android.util.Log
import cat.copernic.roomdecision.selmeem.Utils
import cat.copernic.roomdecision.selmeem.model.publicacions


class Nova_Publicacio : Fragment() {

    val PICK_IMAGE_REQUEST = 1
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private lateinit var titol: EditText
    private lateinit var imatge: ImageView

    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_nova__publicacio, container, false)

        titol = view.findViewById(R.id.edNom)
        imatge = view.findViewById(R.id.imageView)

        view.findViewById<View>(R.id.btnCancelar).setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.contenidorFragments1, Pantalla_inicial())
            transaction.commit()
        }

        view.findViewById<View>(R.id.btnCancelar2).setOnClickListener {
            if (titol.text.isNotEmpty() && selectedImageUri != null) {
                uploadImage()
            } else {
                Log.w(TAG, "El titol i la imatge son obligatoris.")
            }
        }

        view.findViewById<View>(R.id.btnCancelar3).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        return view
    }

    private fun uploadImage() {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email

        if (email != null) {
            val storageRef = storage.reference.child("images/${UUID.randomUUID()}")
            val uploadTask = storageRef.putFile(selectedImageUri!!)
            uploadTask.addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imatgeNom =
                        storageRef.name // Obtener el nom de la imatge del StorageReference
                    createPublicacio(imatgeNom, email)
                }
            }.addOnFailureListener { e ->
                // Mostrar missatge d'error al pujar imatge al Storage
                Utils.mostrarError(requireContext(), "Error en pujar la imatge")
            }
        } else {
            // Mostrar missatge d'error amb el correo
            Utils.mostrarError(requireContext(), "Error amb el correo")

        }
    }


    // Funció per crear una nova publicació a la base de dades
    private fun createPublicacio(imatgeNom: String, email: String) {
        // Obtenir el nom del creador a través d'un callback
        getNomCreador(object : NomCreadorCallback {
            override fun onCallback(nomCreador: String) {
                // Crear una nova publicació amb les dades proporcionades
                val publicacio = publicacions(
                    titol = titol.text.toString(),
                    imatge = imatgeNom,
                    nomCreador = nomCreador,
                    dataPujada = Calendar.getInstance().time,
                    like = 0,
                    llistaLike = mutableListOf<String>(),
                    llistaFavorits = mutableListOf<String>(),
                    id = "", // inicialitzar el valor del camp id com una cadena buida
                )

                // Afegir la nova publicació a la col·lecció "publicacions" a la base de dades
                db.collection("publicacions")
                    .add(publicacio)
                    .addOnSuccessListener { documentReference ->
                        // Obtenir l'ID de la nova publicació
                        val documentId = documentReference.id
                        // Actualitzar el valor del camp "id" amb el nom del document
                        db.collection("publicacions")
                            .document(documentId)
                            .update("id", documentId)
                            .addOnSuccessListener {
                                // Si s'ha afegit correctament la nova publicació, actualitzar la vista amb les publicacions actuals
                                Log.d(TAG, "DocumentSnapshot added with ID: $documentId")
                                val transaction = parentFragmentManager.beginTransaction()
                                transaction.replace(R.id.contenidorFragments1, Pantalla_inicial())
                                transaction.commit()
                            }
                            .addOnFailureListener { e ->
                                // Mostrar missatge d'error al actualitzar publicacions
                                Utils.mostrarError(
                                    requireContext(),
                                    "Error al actualitzar publicacions"
                                )
                            }
                    }
                    .addOnFailureListener { e ->
                        // Mostrar missatge d'error al crear publicacio
                        Utils.mostrarError(requireContext(), "Error al crear publicacio")
                    }
            }
        })
    }

    // Funció per obtenir el nom del creador a partir del correu electrònic
    private fun getNomCreador(callback: NomCreadorCallback) {
        // Obtenció de l'autenticació i de l'usuari actual
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val db = FirebaseFirestore.getInstance()

        // Si l'usuari està autenticat, obtenir el seu correu electrònic
        currentUser?.email?.let { email ->
            // Obtenció del document de l'usuari a partir del seu correu electrònic
            db.collection("usuarios")
                .document(email)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    // Obtenir el nom del creador a partir del document
                    val nomCreador = documentSnapshot.getString("nom") ?: ""
                    callback.onCallback(nomCreador)
                }
                .addOnFailureListener { exception ->
                    // Si hi ha hagut un error al obtenir el document de l'usuari, mostrar un missatge d'error
                    Utils.mostrarError(requireContext(), "Error al actualitzar publicacions")
                }
        }

    }

    interface NomCreadorCallback {
        fun onCallback(nomCreador: String)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(
                    requireActivity().contentResolver,
                    selectedImageUri
                )
                imatge.setImageBitmap(bitmap)
            } catch (e: IOException) {
                // Mostrar missatge d'error al obtenir la imatge
                Utils.mostrarError(requireContext(), "Error al obtenir l'imatge")
            }
        }
    }

}
