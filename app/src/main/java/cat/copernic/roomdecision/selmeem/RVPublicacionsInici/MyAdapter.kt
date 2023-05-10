import android.content.ContentValues.TAG
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.roomdecision.selmeem.R
import cat.copernic.roomdecision.selmeem.RVPublicacionsInici.Publicacio
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

class MyAdapter(private val publicacions: List<Publicacio>) : RecyclerView.Adapter<MyAdapter.PublicacionViewHolder>() {
    private val storageReference = Firebase.storage.reference
    private val mAuth = FirebaseAuth.getInstance()

    class PublicacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titolTextView: TextView = itemView.findViewById(R.id.titol)
        val personaPropTextView: TextView = itemView.findViewById(R.id.personaProp)
        val likeTextView: TextView = itemView.findViewById(R.id.like)
        val imatgeImageView: ImageView = itemView.findViewById(R.id.imatgePublicacio)
        val likeImage: ImageView = itemView.findViewById(R.id.likeImage)
        val favImage: ImageView = itemView.findViewById(R.id.fav)
        val imatgePerfil: ImageView = itemView.findViewById(R.id.imatgePerfil)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicacionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_publicacio, parent, false)
        return PublicacionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PublicacionViewHolder, position: Int) {
        val publicacion = publicacions[position]
        val storageRef = storageReference.child("images/${publicacion.imatge}")

        val ONE_MEGABYTE: Long = 1024 * 1024
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            holder.imatgeImageView.setImageBitmap(bitmap)
        }.addOnFailureListener {
        }

        holder.titolTextView.text = publicacion.titol
        holder.personaPropTextView.text = publicacion.nomCreador
        holder.likeTextView.text = publicacion.like.toString()

        val llistaLike = publicacion.llistaLike.toMutableList()
        val llistaFavorits = publicacion.llistaFavorits.toMutableList()

        // Obtenim l'email de l'usuari actual
        val db = FirebaseFirestore.getInstance()
        val userEmail = mAuth.currentUser?.email
        val publicacionsRef = db.collection("publicacions")

        // Verifiquem si l'email de l'usuari actual está en la lista de likes
        if (userEmail in llistaLike) {
            // Si está, cambiem l'imatge del botó like a "like_marcat"
            holder.likeImage.setImageResource(R.drawable.like_marcat)

            // Agregem un listener per quan es presioni el botó "like" es tregui el like
            holder.likeImage.setOnClickListener {
                llistaLike.remove(userEmail)
                publicacion.like -= 1
                holder.likeTextView.text = publicacion.like.toString()
                publicacionsRef.document(publicacion.id)
                    .update("like", publicacion.like, "llistaLike", llistaLike)
                    .addOnSuccessListener {
                        // La actualizació es realitza correctament
                    }
                    .addOnFailureListener { e ->
                        // La actualizació falla
                    }
                holder.likeImage.setImageResource(R.drawable.like_desmarcat)

                // Agregem un listener per quan es presioni el botó "like" de nou, es sumi el like
                holder.likeImage.setOnClickListener {
                    llistaLike.add(userEmail!!)
                    publicacion.like += 1
                    holder.likeTextView.text = publicacion.like.toString()
                    publicacionsRef.document(publicacion.id)
                        .update("like", publicacion.like, "llistaLike", llistaLike)
                        .addOnSuccessListener {
                            // La actualizació es realitza correctament
                        }
                        .addOnFailureListener { e ->
                            // L'actualizació falla
                        }
                    holder.likeImage.setImageResource(R.drawable.like_marcat)

                    // Desactivem el listener del botó like
                    holder.likeImage.setOnClickListener(null)
                }
            }
        } else {
            // Si no está, mantenim l'imatge normal del botó "like" a "like_desmarcat"
            holder.likeImage.setImageResource(R.drawable.like_desmarcat)

            // Agregem un listener per quan es presioie el botó "like", es sumi el like
            holder.likeImage.setOnClickListener {
                llistaLike.add(userEmail!!)
                publicacion.like += 1
                holder.likeTextView.text = publicacion.like.toString()
                publicacionsRef.document(publicacion.id)
                    .update("like", publicacion.like, "llistaLike", llistaLike)
                    .addOnSuccessListener {
                        // La actualizació s'ha realitzat correctament
                    }
                    .addOnFailureListener { e ->
                        // La actualizació falla
                    }
                holder.likeImage.setImageResource(R.drawable.like_marcat)
                holder.likeImage.setOnClickListener {
                    llistaLike.remove(userEmail)
                    publicacion.like -= 1
                    holder.likeTextView.text = publicacion.like.toString()
                    publicacionsRef.document(publicacion.id)
                        .update("like", publicacion.like, "llistaLike", llistaLike)
                        .addOnSuccessListener {
                            // La actualizació s'ha realitzat correctament
                        }
                        .addOnFailureListener { e ->
                            // La actualizació falla
                        }
                    holder.likeImage.setImageResource(R.drawable.like_desmarcat)
                    holder.likeImage.setOnClickListener {
                        llistaLike.add(userEmail!!)
                        publicacion.like += 1
                        holder.likeTextView.text = publicacion.like.toString()
                        publicacionsRef.document(publicacion.id)
                            .update("like", publicacion.like, "llistaLike", llistaLike)
                            .addOnSuccessListener {
                                // La actualizació s'ha realitzat correctament
                            }
                            .addOnFailureListener { e ->
                                // La actualizació falla
                            }
                        holder.likeImage.setImageResource(R.drawable.like_marcat)
                        holder.likeImage.setOnClickListener(null)
                    }
                }
            }
        }
        // Verifiquem si l'email de l'usuari actual está en la lista de favorits
        if (userEmail in llistaFavorits) {
            // Si está, cambiem l'imatge del botó fav a "like_marcat"
            holder.favImage.setImageResource(R.drawable.save_marcat)

            // Agregem un listener per quan es presioni el botó "fav" es tregui el fav
            holder.favImage.setOnClickListener {
                llistaFavorits.remove(userEmail)
                publicacionsRef.document(publicacion.id)
                    .update("llistaFavorits", llistaFavorits)
                    .addOnSuccessListener {
                        // La actualizació es realitza correctament
                    }
                    .addOnFailureListener { e ->
                        // La actualizació falla
                    }
                holder.favImage.setImageResource(R.drawable.save_desmarcat)

                // Agregem un listener per quan es presioni el botó "fav" de nou
                holder.favImage.setOnClickListener {
                    llistaFavorits.add(userEmail!!)
                    publicacionsRef.document(publicacion.id)
                        .update( "llistaFavorits", llistaFavorits)
                        .addOnSuccessListener {
                            // La actualizació es realitza correctament
                        }
                        .addOnFailureListener { e ->
                            // L'actualizació falla
                        }
                    holder.favImage.setImageResource(R.drawable.save_marcat)

                    // Desactivem el listener del botó fav
                    holder.favImage.setOnClickListener(null)
                }
            }
        } else {
            // Si no está, mantenim l'imatge normal del botó "fav" a "fav_desmarcat"
            holder.favImage.setImageResource(R.drawable.save_desmarcat)

            // Agregem un listener per quan es presioie el botó "fav"
            holder.favImage.setOnClickListener {
                llistaFavorits.add(userEmail!!)
                publicacionsRef.document(publicacion.id)
                    .update( "llistaFavorits", llistaFavorits)
                    .addOnSuccessListener {
                        // La actualizació s'ha realitzat correctament
                    }
                    .addOnFailureListener { e ->
                        // La actualizació falla
                    }
                holder.favImage.setImageResource(R.drawable.save_marcat)
                holder.favImage.setOnClickListener {
                    llistaFavorits.remove(userEmail)
                    publicacionsRef.document(publicacion.id)
                        .update("llistaFavorits", llistaFavorits)
                        .addOnSuccessListener {
                            // La actualizació s'ha realitzat correctament
                        }
                        .addOnFailureListener { e ->
                            // La actualizació falla
                        }
                    holder.favImage.setImageResource(R.drawable.save_desmarcat)
                    holder.favImage.setOnClickListener {
                        llistaFavorits.add(userEmail!!)
                        publicacionsRef.document(publicacion.id)
                            .update("llistaFavorits", llistaFavorits)
                            .addOnSuccessListener {
                                // La actualizació s'ha realitzat correctament
                            }
                            .addOnFailureListener { e ->
                                // La actualizació falla
                            }
                        holder.favImage.setImageResource(R.drawable.save_marcat)
                        holder.favImage.setOnClickListener(null)
                    }
                }
            }
        }
        // Obtener la imagen de perfil del creador de la publicación
        val usersRef = db.collection("usuarios")
        val query = usersRef.whereEqualTo("nom", publicacion.nomCreador)

        query.get().addOnSuccessListener { result ->
            if (!result.isEmpty) {
                val documentSnapshot = result.documents[0]
                val imatgePerfil = documentSnapshot.getString("imatge")
                Log.d(TAG, "imagen de perfil: $imatgePerfil")
                val storageRefPerfil = Firebase.storage.reference.child("$imatgePerfil")

                val ONE_MEGABYTE: Long = 1024 * 1024
                storageRefPerfil.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    holder.imatgePerfil.setImageBitmap(bitmap)

                }.addOnFailureListener { }
            }
        }

    }



    override fun getItemCount() = publicacions.size
}
