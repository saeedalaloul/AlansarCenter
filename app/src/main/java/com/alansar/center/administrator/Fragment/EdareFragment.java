package com.alansar.center.administrator.Fragment;


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
import com.alansar.center.Edare.Model.Edare;
import com.alansar.center.R;
import com.alansar.center.administrator.Adapters.EdareAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class EdareFragment extends Fragment {
    private ArrayList<Edare> edares;
    private FirebaseFirestore db;
    private EdareAdapter adapter;
    private RecyclerView rv;
    private View view;
    private ListenerRegistration registration;

    public EdareFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_admin_edare, container, false);

        db = FirebaseFirestore.getInstance();

        rv = view.findViewById(R.id.edare_rv);
        edares = new ArrayList<>();
        FloatingActionButton btnaddEdare = view.findViewById(R.id.btn_add_edare);
        btnaddEdare.setOnClickListener(view1 -> addEdare());
        adapter = new EdareAdapter(edares);
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

    private void addEdare() {
        startActivity(new Intent(getActivity(), HostingActivity.class)
                .putExtra("fragmentType", "Personal_Information__Fragment")
                .putExtra("permissions", Common.PERMISSIONS_EDARE));
    }

    private void LoadData() {
        registration = db.collection("Edare")
                .orderBy("name", Query.Direction.ASCENDING).limit(10)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    assert queryDocumentSnapshots != null;
                    if (!queryDocumentSnapshots.isEmpty()) {
                        edares.clear();
                        for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots) {
                            edares.add(snapshots.toObject(Edare.class));
                            adapter.notifyDataSetChanged();
                        }
                        if (edares.isEmpty()) {
                            view.findViewById(R.id.tv_check_edare).setVisibility(View.VISIBLE);
                            rv.setVisibility(View.GONE);
                        } else {
                            view.findViewById(R.id.tv_check_edare).setVisibility(View.GONE);
                            rv.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void SearchData(String newText) {
        CollectionReference Ref = db.collection("Edare");
        if (newText.isEmpty()) {
            edares.clear();
            LoadData();
            adapter.notifyDataSetChanged();
        } else {
            Query query = Ref.orderBy("name").startAt(newText).endAt(newText + "\uf8ff");
            query.addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (e != null) {
                    Log.w("sss", "listen:error" + e.getLocalizedMessage());
                    return;
                }
                if (queryDocumentSnapshots != null) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        edares.clear();
                        for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots) {
                            edares.add(snapshots.toObject(Edare.class));
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        edares.clear();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "لم يتم العثور على طلبك !", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)) {
            startActivity(new Intent(getActivity(), HostingActivity.class)
                    .putExtra("fragmentType", "Update_Personal_Information__Fragment")
                    .putExtra("UID", EdareAdapter.edares.get(item.getOrder()).getId())
                    .putExtra("permissions", Common.PERMISSIONS_EDARE)
            );
        } else if (item.getTitle().equals(Common.DELETE)) {
            Toast.makeText(getContext(), "DELETE", Toast.LENGTH_SHORT).show();
        } else if (item.getTitle().equals(Common.ISDISABLEACCOUNT)) {
            updateIsEnabledAccount(EdareAdapter.edares.get(item.getOrder()).getId(), false);
        } else if (item.getTitle().equals(Common.ISENABLEACCOUNT)) {
            updateIsEnabledAccount(EdareAdapter.edares.get(item.getOrder()).getId(), true);
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
}
