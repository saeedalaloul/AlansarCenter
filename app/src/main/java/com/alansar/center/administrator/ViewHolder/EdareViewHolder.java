package com.alansar.center.administrator.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Common.Common;
import com.alansar.center.Edare.Model.Edare;
import com.alansar.center.Interface.ItemClickListener;
import com.alansar.center.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EdareViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
    public TextView tv_Edare_name, tv_Edare_stage;
    public ImageButton imgbtn_more;
    private ItemClickListener itemClickListener;
    private FirebaseFirestore db;
    private ArrayList<Edare> edares;


    public EdareViewHolder(@NonNull View itemView, ArrayList<Edare> edares) {
        super(itemView);
        tv_Edare_name = itemView.findViewById(R.id.tv_edare_name);
        tv_Edare_stage = itemView.findViewById(R.id.tv_edare_stage);
        imgbtn_more = itemView.findViewById(R.id.imgbtn_more_edare);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
        this.edares = edares;
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
        contextMenu.add(0, 1, getAdapterPosition(), Common.DELETE);
        db.collection("Person").document(edares.get(getAdapterPosition()).getId()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.getBoolean("enableAccount")) {
                contextMenu.add(0, 2, getAdapterPosition(), Common.ISDISABLEACCOUNT);
            } else {
                contextMenu.add(0, 2, getAdapterPosition(), Common.ISENABLEACCOUNT);
            }
        });
    }
}