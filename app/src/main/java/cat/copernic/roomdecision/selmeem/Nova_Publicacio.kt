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
                    val imatgeNom = storageRef.name // Obtener el nombre de la imagen del StorageReference
                    createPublicacio(imatgeNom, email)
                }
            }.addOnFailureListener {e ->
                Log.e(TAG, "Error en pujar la imatge a Firebase Storage.", e)
            }
        } else {
            Log.w(TAG, "Error amb el correo.")

        }
    }


    private fun createPublicacio(imatgeNom: String, email: String) {
        getNomCreador(object : NomCreadorCallback {
            override fun onCallback(nomCreador: String) {
                val publicacio = publicacions(
                    titol = titol.text.toString(),
                    imatge = imatgeNom,
                    nomCreador = nomCreador,
                    dataPujada = Calendar.getInstance().time,
                    like = 0,
                    llistaLike = mutableListOf<String>(),
                    llistaFavorits = mutableListOf<String>(),
                    id = "", // inicializar el valor del campo id como una cadena vacía
                )

                db.collection("publicacions")
                    .add(publicacio)
                    .addOnSuccessListener { documentReference ->
                        val documentId = documentReference.id
                        db.collection("publicacions")
                            .document(documentId)
                            .update("id", documentId) // actualizar el valor del campo id con el nombre del documento
                            .addOnSuccessListener {
                                Log.d(TAG, "DocumentSnapshot added with ID: $documentId")
                                val transaction = parentFragmentManager.beginTransaction()
                                transaction.replace(R.id.contenidorFragments1, Pantalla_inicial())
                                transaction.commit()
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error updating document", e)
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error adding document", e)
                    }
            }
        })
    }





    private fun getNomCreador(callback: NomCreadorCallback) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val db = FirebaseFirestore.getInstance()

        currentUser?.email?.let { email ->
            db.collection("usuarios")
                .document(email)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val nomCreador = documentSnapshot.getString("nom") ?: ""
                    callback.onCallback(nomCreador)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error obteniendo nombre del creador: $exception")
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
                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, selectedImageUri)
                imatge.setImageBitmap(bitmap)
            } catch (e: IOException) {
                Log.e(TAG, "Error obteniendo la imagen seleccionada.", e)
            }
        }
    }

}
