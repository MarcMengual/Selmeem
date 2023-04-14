package cat.copernic.roomdecision.selmeem.RVPublicacionsInici;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cat.copernic.roomdecision.selmeem.R;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {


    Context context;
    List<Item> items;

    public MyAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_publicacio,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.titolView.setText(items.get(position).getTitol());
        holder.personaPropView.setText(items.get(position).getPropietari());
        holder.likeView.setText(String.valueOf(items.get(position).getLikes()));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
