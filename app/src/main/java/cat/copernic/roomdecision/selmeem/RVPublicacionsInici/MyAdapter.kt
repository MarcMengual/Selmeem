import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.roomdecision.selmeem.R
import cat.copernic.roomdecision.selmeem.RVPublicacionsInici.Publicacio
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MyAdapter(private val publicacions: List<Publicacio>) : RecyclerView.Adapter<MyAdapter.PublicacionViewHolder>() {
    private val storageReference = Firebase.storage.reference

    class PublicacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titolTextView: TextView = itemView.findViewById(R.id.titol)
        val personaPropTextView: TextView = itemView.findViewById(R.id.personaProp)
        val likeTextView: TextView = itemView.findViewById(R.id.like)
        val imatgeImageView: ImageView = itemView.findViewById(R.id.imatgePublicacio)
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
    }


    override fun getItemCount() = publicacions.size
}
