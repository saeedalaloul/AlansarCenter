package com.alansar.center.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Common.Common;
import com.alansar.center.Edare.Activitys.EdareActivity;
import com.alansar.center.Models.AccountItem;
import com.alansar.center.Mohafez.Activitys.MohafezActivity;
import com.alansar.center.Moshref.Activity.MoshrefActivity;
import com.alansar.center.R;
import com.alansar.center.administrator.Activitys.AdminActivity;
import com.alansar.center.supervisor_exams.Activitys.SuperVisorExamsActivity;
import com.alansar.center.testers.Activitys.TesterActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class Multiple_accounts_Adapter extends RecyclerView.Adapter<Multiple_accounts_Adapter.ViewHolder> {

    private ArrayList<AccountItem> accountItems;
    private FragmentActivity activity;
    private FirebaseFirestore db;

    public Multiple_accounts_Adapter(ArrayList<AccountItem> accountItems, FragmentActivity activity) {
        this.accountItems = accountItems;
        this.activity = activity;
        Paper.init(activity);
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_login, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AccountItem accountItem = accountItems.get(position);
        holder.tv_name.setText(accountItem.getName());
        holder.tv_Permission.setText(Common.ConvertPermissionToNameArabic(accountItem.getPermission()));
        if (accountItem.getImage() != null && accountItem.getImage().isEmpty()) {
            holder.img_profile.setImageResource(R.drawable.profile_image);
        } else {
            Picasso.get().load(accountItem.getImage()).into(holder.img_profile);
        }
        holder.itemView.setOnClickListener(view -> {
            switch (accountItem.getPermission()) {
                case Common.PERMISSIONS_EDARE:
                    Paper.book().write(Common.PERMISSION, accountItem.getPermission());
                    Common.currentPermission = accountItem.getPermission();
                    view.getContext().startActivity(new Intent(view.getContext(), EdareActivity.class)
                            .putExtra("Permission", accountItem.getPermission())
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    getStageOfPersonFromDB(accountItem.getPermission(), Common.currentPerson.getId());
                    break;
                case Common.PERMISSIONS_SUPER_VISOR:
                    Paper.book().write(Common.PERMISSION, accountItem.getPermission());
                    Common.currentPermission = accountItem.getPermission();
                    view.getContext().startActivity(new Intent(view.getContext(), MoshrefActivity.class)
                            .putExtra("Permission", accountItem.getPermission())
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    getStageOfPersonFromDB(accountItem.getPermission(), Common.currentPerson.getId());
                    break;
                case Common.PERMISSIONS_ADMIN:
                    Paper.book().write(Common.PERMISSION, accountItem.getPermission());
                    Common.currentPermission = accountItem.getPermission();
                    view.getContext().startActivity(new Intent(view.getContext(), AdminActivity.class)
                            .putExtra("Permission", accountItem.getPermission())
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    activity.finish();
                    break;
                case Common.PERMISSIONS_MOHAFEZ:
                    Paper.book().write(Common.PERMISSION, accountItem.getPermission());
                    Common.currentPermission = accountItem.getPermission();
                    view.getContext().startActivity(new Intent(view.getContext(), MohafezActivity.class)
                            .putExtra("Permission", accountItem.getPermission())
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    getStageOfPersonFromDB(accountItem.getPermission(), Common.currentPerson.getId());
                    break;
                case Common.PERMISSIONS_SUPER_VISOR_EXAMS:
                    Paper.book().write(Common.PERMISSION, accountItem.getPermission());
                    Common.currentPermission = accountItem.getPermission();
                    view.getContext().startActivity(new Intent(view.getContext(), SuperVisorExamsActivity.class)
                            .putExtra("Permission", accountItem.getPermission())
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    activity.finish();
                    break;
                case Common.PERMISSIONS_TESTER:
                    Paper.book().write(Common.PERMISSION, accountItem.getPermission());
                    Common.currentPermission = accountItem.getPermission();
                    view.getContext().startActivity(new Intent(view.getContext(), TesterActivity.class)
                            .putExtra("Permission", accountItem.getPermission())
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    getStageOfPersonFromDB(accountItem.getPermission(), Common.currentPerson.getId());
                    break;
            }
        });
    }

    @Override
    public int getItemCount() {
        return accountItems.size();
    }

    private void getStageOfPersonFromDB(String Permission, String UID) {
        if (Permission != null && UID != null) {
            switch (Permission) {
                case Common.PERMISSIONS_MOHAFEZ:
                    db.collection("Mohafez").document(UID).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    Common.currentSTAGE = documentSnapshot.getString("stage");
                                    Common.currentGroupId = documentSnapshot.getString("groupId");
                                    Paper.book().write(Common.STAGE, Common.currentSTAGE);
                                    Paper.book().write(Common.GROUPID, Common.currentGroupId);
                                    activity.finish();
                                }
                            });
                    break;
                case Common.PERMISSIONS_SUPER_VISOR:
                    db.collection("Moshref").document(UID).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    Common.currentSTAGE = documentSnapshot.getString("stage");
                                    Paper.book().write(Common.STAGE, Common.currentSTAGE);
                                    activity.finish();
                                }
                            });
                    break;
                case Common.PERMISSIONS_TESTER:
                    db.collection("Tester").document(UID).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    Common.currentSTAGE = documentSnapshot.getString("stage");
                                    Paper.book().write(Common.STAGE, Common.currentSTAGE);
                                    activity.finish();
                                }
                            });
                    break;
                case Common.PERMISSIONS_EDARE:
                    db.collection("Edare").document(UID).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    Common.currentSTAGE = documentSnapshot.getString("stage");
                                    Paper.book().write(Common.STAGE, Common.currentSTAGE);
                                    activity.finish();
                                }
                            });
                    break;
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_Permission;
        CircleImageView img_profile;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.item_account_login_name_tv);
            tv_Permission = itemView.findViewById(R.id.item_account_login_permission_tv);
            img_profile = itemView.findViewById(R.id.item_account_login_profile_img);
        }
    }


}
