package com.alansar.center.Edare.Fragment;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Activitys.HostingActivity;
import com.alansar.center.Common.Common;
import com.alansar.center.Edare.Adapter.MohafezAdapter;
import com.alansar.center.Mohafez.Model.Mohafez;
import com.alansar.center.R;
import com.alansar.center.SweetAlertDialog_;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class MohafezFragment extends Fragment {
    private ArrayList<Mohafez> mohafezeen;
    private FirebaseFirestore db;
    private MohafezAdapter adapter;
    private ListenerRegistration registration;

    public MohafezFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edare_mohafez, container, false);

        db = FirebaseFirestore.getInstance();

        RecyclerView rv = view.findViewById(R.id.mohafez_rv);
        mohafezeen = new ArrayList<>();
        FloatingActionButton btnaddMohafez = view.findViewById(R.id.btn_add_mohafez);
        btnaddMohafez.setOnClickListener(view1 -> addMohafez());
        adapter = new MohafezAdapter(mohafezeen);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setHasFixedSize(true);
        setHasOptionsMenu(true);
        rv.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadData();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (registration != null) {
            registration.remove();
        }
    }

    private void LoadData() {
        if (Common.currentSTAGE != null) {
            registration = db.collection("Mohafez")
                    .orderBy("name", Query.Direction.ASCENDING)
                    .whereEqualTo("stage", Common.currentSTAGE)
                    .limit(10)
                    .addSnapshotListener((queryDocumentSnapshots, e) -> {
                        if (e != null) {
                            Log.w("sss", "listen:error" + e.getLocalizedMessage());
                            return;
                        }
                        assert queryDocumentSnapshots != null;
                        if (!queryDocumentSnapshots.isEmpty()) {
                            mohafezeen.clear();
                            for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots) {
                                if (!snapshots.getId().equals(Common.currentPerson.getId()))
                                    mohafezeen.add(snapshots.toObject(Mohafez.class));
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        adapter.notifyDataSetChanged();
        if (item.getTitle().equals(Common.UPDATE)) {
            db.collection("PermissionsUsers")
                    .document("permissionsUsers")
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.get("permissionsEdare.updateMohafez", Boolean.TYPE)) {
                        startActivity(new Intent(getActivity(), HostingActivity.class)
                                .putExtra("fragmentType", "Update_Personal_Information__Fragment")
                                .putExtra("UID", MohafezAdapter.mohafezeen.get(item.getOrder()).getId())
                                .putExtra("permissions", Common.PERMISSIONS_MOHAFEZ)
                        );
                    } else {
                        new SweetAlertDialog_(getContext()).showDialogError("لم يتم منحك صلاحية تحديث محفظ يرجى مراجعة مسؤول المركز");
                    }
                }
            }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
        } else if (item.getTitle().equals(Common.ISDISABLEACCOUNT)) {
            db.collection("PermissionsUsers")
                    .document("permissionsUsers")
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.get("permissionsEdare.disableAccountMohafez", Boolean.TYPE)) {
                        updateIsEnabledAccount(MohafezAdapter.mohafezeen.get(item.getOrder()).getId(), false);
                    } else {
                        new SweetAlertDialog_(getContext()).showDialogError("لم يتم منحك صلاحية تعطيل حسابات المحفظين يرجى مراجعة مسؤول المركز");
                    }
                }
            }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
        } else if (item.getTitle().equals(Common.ISENABLEACCOUNT)) {
            db.collection("PermissionsUsers")
                    .document("permissionsUsers")
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.get("permissionsEdare.disableAccountMohafez", Boolean.TYPE)) {
                        updateIsEnabledAccount(MohafezAdapter.mohafezeen.get(item.getOrder()).getId(), true);
                    } else {
                        new SweetAlertDialog_(getContext()).showDialogError("لم يتم منحك صلاحية تمكين حسابات المحفظين يرجى مراجعة مسؤول المركز");
                    }
                }
            }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
        }
        return super.onContextItemSelected(item);

    }

    private void updateIsEnabledAccount(String id, boolean isEnable) {
        final SweetAlertDialog BtnDialog = new SweetAlertDialog(Objects.requireNonNull(getActivity()), SweetAlertDialog.NORMAL_TYPE)
                .setContentText("هل أنت متأكد من ذلك ؟")
                .setConfirmText("تأكيد")
                .setCancelText("إلغاء");

        if (isEnable)
            BtnDialog.setTitleText("تمكين الحساب");
        else
            BtnDialog.setTitleText("تعطيل الحساب");

        BtnDialog.setOnShowListener(dialog -> {
            BtnDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setOnClickListener(view -> {
                db.collection("Person").document(id).update("enableAccount", isEnable);
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(Objects.requireNonNull(getContext()), SweetAlertDialog.SUCCESS_TYPE)
                        .setContentText("تمت");
                if (isEnable)
                    sweetAlertDialog.setTitleText("تم تمكين الحساب بنجاح !");
                else
                    sweetAlertDialog.setTitleText("تم تعطيل الحساب بنجاح !");
                dialog.dismiss();
                sweetAlertDialog.show();
            });

            BtnDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setOnClickListener(view -> dialog.dismiss());
        });
        BtnDialog.show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fg_menu, menu);
        SearchManager searchManager = (SearchManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // adapter.getFilter().filter(query);
                SearchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //adapter.getFilter().filter(newText);
                SearchData(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);

    }

    private void SearchData(String newText) {
        AtomicBoolean isFound = new AtomicBoolean(false);
        CollectionReference Ref = db.collection("Mohafez");
        Query query = Ref.whereEqualTo("stage", Common.currentSTAGE);
        if (newText.isEmpty()) {
            LoadData();
            mohafezeen.clear();
            adapter.notifyDataSetChanged();
        } else {
            mohafezeen.clear();
            query = query.orderBy("name").startAt(newText).endAt(newText + "\uf8ff");
            query.addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (e != null) {
                    Log.w("sss", "listen:error" + e.getLocalizedMessage());
                    return;
                }
                if (queryDocumentSnapshots != null) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        isFound.set(true);
                        mohafezeen.clear();
                        for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots) {
                            mohafezeen.add(snapshots.toObject(Mohafez.class));
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        mohafezeen.clear();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "لم يتم العثور على طلبك !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    adapter.notifyDataSetChanged();
                    if (!isFound.get()) {
                        Toast.makeText(getContext(), "لم يتم العثور على طلبك !", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void addMohafez() {
        db.collection("PermissionsUsers")
                .document("permissionsUsers")
                .get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.get("permissionsEdare.addMohafez", Boolean.TYPE)) {
                    startActivity(new Intent(getActivity(), HostingActivity.class)
                            .putExtra("fragmentType", "Personal_Information__Fragment")
                            .putExtra("permissions", Common.PERMISSIONS_MOHAFEZ));
                } else {
                    new SweetAlertDialog_(getContext()).showDialogError("لم يتم منحك صلاحية إضافة محفظ يرجى مراجعة مسؤول المركز");
                }
            }
        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
    }

}
