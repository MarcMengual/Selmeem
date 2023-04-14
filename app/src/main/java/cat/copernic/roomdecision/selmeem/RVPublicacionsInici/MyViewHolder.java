package cat.copernic.roomdecision.selmeem.RVPublicacionsInici;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cat.copernic.roomdecision.selmeem.R;

public class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView imatgePublicacioView;
    TextView titolView, personaPropView, likeView;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        imatgePublicacioView = itemView.findViewById(R.id.imatgePublicacio);
        titolView = itemView.findViewById(R.id.titol);
        personaPropView = itemView.findViewById(R.id.personaProp);
        likeView = itemView.findViewById(R.id.like);
    }
}
