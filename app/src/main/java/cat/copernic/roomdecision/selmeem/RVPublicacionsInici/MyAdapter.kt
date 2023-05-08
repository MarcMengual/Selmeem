import android.graphics.BitmapFactory
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
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MyAdapter(private val publicacions: List<Publicacio>) : RecyclerView.Adapter<MyAdapter.PublicacionViewHolder>() {
    private val storageReference = Firebase.storage.reference
    private val mAuth = FirebaseAuth.getInstance()

    class PublicacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titolTextView: TextView = itemView.findViewById(R.id.titol)
        val personaPropTextView: TextView = itemView.findViewById(R.id.personaProp)
        val likeTextView: TextView = itemView.findViewById(R.id.like)
        val imatgeImageView: ImageView = itemView.findViewById(R.id.imatgePublicacio)
        val likeImage: ImageView = itemView.findViewById(R.id.likeImage)
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

        // Obtenemos el email del usuario actual
        val userEmail = mAuth.currentUser?.email

        val db = FirebaseFirestore.getInstance()
        val publicacionsRef = db.collection("publicacions")

        // Verificamos si el email del usuario actual está en la lista de likes
        if (userEmail in llistaLike) {
            // Si está, cambiamos la imagen del botón like
            holder.likeImage.setImageResource(R.drawable.like_marcat)

            // Desactivamos el listener del botón like
            holder.likeImage.setOnClickListener(null)
        } else {
            // Si no está, mantenemos la imagen normal del botón like
            holder.likeImage.setImageResource(R.drawable.like_desmarcat)

            // Añadimos un listener para que cuando se presione se agregue el like
            holder.likeImage.setOnClickListener {
                llistaLike.add(userEmail!!)
                publicacion.like += 1
                holder.likeTextView.text = publicacion.like.toString()
                publicacionsRef.document(publicacion.id).update("like", publicacion.like, "llistaLike", llistaLike)
                    .addOnSuccessListener {
                        // La actualización se realizó correctamente
                    }
                    .addOnFailureListener { e ->
                        // La actualización falló
                    }
                holder.likeImage.setImageResource(R.drawable.like_marcat)
                holder.likeImage.setOnClickListener(null)
            }
        }
    }


    override fun getItemCount() = publicacions.size
}
