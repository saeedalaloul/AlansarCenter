package com.alansar.center.Edare.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Common.Common;
import com.alansar.center.Interface.ItemClickListener;
import com.alansar.center.Mohafez.Model.Mohafez;
import com.alansar.center.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MohafezViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
    public TextView tv_mohafez_name, tv_mohafez_stage;
    public ImageButton imgbtn_more;
    private ItemClickListener itemClickListener;
    private List<Mohafez> mohafezs;
    private FirebaseFirestore db;

    public MohafezViewHolder(@NonNull View itemView, List<Mohafez> mohafezs) {
        super(itemView);
        tv_mohafez_name = itemView.findViewById(R.id.tv_mohafez_name);
        tv_mohafez_stage = itemView.findViewById(R.id.tv_mohafez_stage);
        imgbtn_more = itemView.findViewById(R.id.imgbtn_more_mohafez);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
        this.mohafezs = mohafezs;
        db = FirebaseFirestore.getInstance();
    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }


    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select the Action");
        contextMenu.add(0, 0, getAdapterPosition(), Common.UPDATE);
        db.collection("Person").document(mohafezs.get(getAdapterPosition()).getId()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.getBoolean("enableAccount")) {
                    contextMenu.add(0, 1, getAdapterPosition(), Common.ISDISABLEACCOUNT);
                } else {
                    contextMenu.add(0, 1, getAdapterPosition(), Common.ISENABLEACCOUNT);
                }
            }
        });
    }
}
