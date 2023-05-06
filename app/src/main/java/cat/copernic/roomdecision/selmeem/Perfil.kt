package cat.copernic.roomdecision.selmeem

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class Perfil : Fragment() {

    private lateinit var email: String
    private lateinit var imageView: ImageView

    companion object {
        private const val REQUEST_CODE_GALLERY = 2
        const val REQUEST_CODE_PICK_IMAGE = 1

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        email = arguments?.getString("email").toString()
        imageView = view.findViewById<ImageView>(R.id.imageViewPerf)

        // Obtén la referencia a la colección "usuarios"
        val db = Firebase.firestore
        val usersRef = db.collection("usuarios")

        // Obtiene los datos del usuario actual y actualiza la vista
        FirebaseAuth.getInstance().currentUser?.let { user ->
            usersRef.document(user.email!!).get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val name = documentSnapshot.getString("nom")
                    val image = documentSnapshot.getString("imatge")

                    // Actualiza el TextView con el nombre del usuario
                    val nameTextView = view.findViewById<TextView>(R.id.nomText)
                    nameTextView.text = name

                    // Carga la imagen desde Firebase Storage y actualiza el ImageView
                    val storageRef = Firebase.storage.reference
                    val imageRef = storageRef.child(image!!)
                    imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        imageView.setImageBitmap(bitmap)
                    }.addOnFailureListener { e ->
                        Log.e(TAG, "Error al cargar la imagen", e)
                    }
                }
            }
        }

        // Agrega un listener de clic en la imagen del perfil
        imageView.setOnClickListener {
            // Crea un intent para abrir la galería
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_GALLERY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data

            // Sube la imagen a Firebase Storage
            val storageRef = Firebase.storage.reference
            val imageRef = storageRef.child("${email}_${System.currentTimeMillis()}")
            imageRef.putFile(imageUri!!)
                .addOnSuccessListener {
                    // Actualiza el campo "imatge" del documento en la base de datos
                    val db = Firebase.firestore
                    db.collection("usuarios").document(email).update("imatge", imageRef.name)
                        .addOnSuccessListener {
                            // Actualiza la imagen de perfil en la vista
                            val imageView = view?.findViewById<ImageView>(R.id.imageViewPerf)
                            imageView?.let {
                                Glide.with(requireContext())
                                    .load(imageUri)
                                    .into(it)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error al actualizar el campo imatge", e)
                        }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error al subir la imagen a Firebase Storage", e)
                }
        }
    }
}
