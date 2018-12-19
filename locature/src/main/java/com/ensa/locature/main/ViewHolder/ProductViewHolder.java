package com.ensa.locature.main.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ensa.locature.main.Interface.ItemClickListner;
import com.ensa.locature.main.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName, txtProductDesc, txtProductPrix;
    public ImageView imageView;

    public ItemClickListner listner ;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.carimage);
        txtProductName = (TextView) itemView.findViewById(R.id.carname);
        txtProductDesc = (TextView) itemView.findViewById(R.id.desc);
        txtProductPrix = (TextView) itemView.findViewById(R.id.prix);

    }

    public void setItemClickListner(ItemClickListner listner){
        this.listner = listner ;
    }

    @Override
    public void onClick(View v) {

        listner.onClick(v, getAdapterPosition(), false);

    }
}
