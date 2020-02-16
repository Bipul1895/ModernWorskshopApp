package com.example.modernworkshopapp.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.modernworkshopapp.Interface.ItemClickListner;
import com.example.modernworkshopapp.R;

public class AdminParticularOrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView quantity;
    public ImageView orderImage;
    public Button downloadFile;

    private ItemClickListner itemClickListner;

    public AdminParticularOrderViewHolder(@NonNull View itemView) {
        super(itemView);

        quantity=itemView.findViewById(R.id.admin_order_quantity);
        orderImage=itemView.findViewById(R.id.admin_file_img_view);
        downloadFile=itemView.findViewById(R.id.admin_view_particular_order_download_btn);

    }


    @Override
    public void onClick(View view) {

        itemClickListner.onClick(view, getAdapterPosition(), false);

    }

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }


}
