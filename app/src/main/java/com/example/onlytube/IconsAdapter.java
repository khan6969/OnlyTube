package com.example.onlytube;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class IconsAdapter extends RecyclerView.Adapter<IconsAdapter.ViewHolder> {
    public IconsAdapter() {
    }

    Context context;
    private List<IconList> Products;

    public IconsAdapter(Context context, List<IconList> products) {
        this.context = context;
        Products = products;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.listicons,parent,false);
        return new ViewHolder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final IconList L = Products.get(position);
        ImageView imageView = holder.ProductImaeView;
      //  TextView name = holder.TvProduct;
        // Picasso.get().load(L.getCategory_image()).into(imageView);
        holder.TvProduct.setText(L.getIcon_name());
     //   holder.ProductImaeView.setVisibility(View.VISIBLE);

        holder.ProductImaeView.setImageResource(L.getCategory_image());
        // Glide.with(context).load(json+L.getCategory_image()).into(holder.ProductImaeView);

        //   name.setText(L.getCategory_name());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(position==0){
                    Intent Y = new Intent(context,YouTubeActivity.class);
                   // o.putExtra("category_name",L.getCategory_name());
                    Y.putExtra("category_image",L.getCategory_image());
                    Toast.makeText(context, "Next Youtube Activity"+position, Toast.LENGTH_SHORT).show();

                    context.startActivity(Y);
                }

                if(position==1){
                    Intent F = new Intent(context,FacebookActivity.class);
                    // o.putExtra("category_name",L.getCategory_name());
                    F.putExtra("category_image",L.getCategory_image());
                    Toast.makeText(context, "Next Facebook Activity"+position, Toast.LENGTH_SHORT).show();

                    context.startActivity(F);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return Products == null? 0:Products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ProductImaeView;
        TextView TvProduct;

        CardView cview;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ProductImaeView =itemView.findViewById(R.id.ProductImageView);
            TvProduct=itemView.findViewById(R.id.TvProduct);

            cview=itemView.findViewById(R.id.cview);
        }
    }
}
