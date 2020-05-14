package com.alansar.center.Edare.Fragment;


import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Common.Common;
import com.alansar.center.Edare.Adapter.HalakaAdapter;
import com.alansar.center.Models.Group;
import com.alansar.center.Mohafez.Model.Mohafez;
import com.alansar.center.R;
import com.alansar.center.SweetAlertDialog_;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A simple {@link Fragment} subclass.
 */
public class HalakatFragment extends Fragment {
    private FirebaseFirestore db;
    private ArrayList<Group> groups;
    private RecyclerView rv;
    private HalakaAdapter halakaAdapter;
    private ListenerRegistration registration;
    private Spinner spinner_halaka_stage, spinner_halaka_mohafez;
    private TextInputEditText halaka_name;
    private FloatingActionButton creat_fabtn;
    private List<Mohafez> mohafezs;
    private ArrayAdapter<Mohafez> adapter;
    private String mohafezId;
    private SweetAlertDialog_ sweetAlertDialog;
    private AlertDialog alertDialog;
    private List<String> stage_list;
    private String halakaId;
    private String Action = "";
    private Button btn_add;
    private String retMohafezId;
    private ArrayAdapter<String> stagesAdapter;

    public HalakatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edare_halakat, container, false);
        initialized(view);
        creat_fabtn.setOnClickListener(view1 -> {
            db.collection("PermissionsUsers")
                    .document("permissionsUsers")
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.get("permissionsEdare.addHalaka", Boolean.TYPE)) {
                        Action = "Add";
                        showDialogAddHalaka();
                    } else {
                        new SweetAlertDialog_(getContext()).showDialogError("لم يتم منحك صلاحية إضافة حلقة يرجى مراجعة مسؤول المركز");
                    }
                }
            });

        });
        halakaAdapter = new HalakaAdapter(groups);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setHasFixedSize(true);
        rv.setAdapter(halakaAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Common.currentSTAGE != null) {
            LoadData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (registration != null) {
            registration.remove();
        }
    }

    private void initialized(View view) {
        if (getActivity() != null && Common.currentSTAGE != null) {
            db = FirebaseFirestore.getInstance();
            setHasOptionsMenu(true);
            groups = new ArrayList<>();
            mohafezs = new ArrayList<>();
            rv = view.findViewById(R.id.halakat_rv);
            stage_list = new ArrayList<>();
            stage_list.add("اختر المرحلة");
            stage_list.add(Common.currentSTAGE);
            stagesAdapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_spinner_item, stage_list);
            stagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sweetAlertDialog = new SweetAlertDialog_(getContext());
            creat_fabtn = view.findViewById(R.id.creat_halaka__fabtn);
        }
    }

    private void LoadData() {
        registration = db.collection("Group").whereEqualTo("stage", Common.currentSTAGE).limit(10)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.w("sss", "listen:error" + e.getLocalizedMessage());
                        return;
                    }
                    assert queryDocumentSnapshots != null;
                    if (!queryDocumentSnapshots.isEmpty()) {
                        groups.clear();
                        for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots) {
                            groups.add(snapshots.toObject(Group.class));
                            halakaAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void showDialogAddHalaka() {
        if (getActivity() != null) {
            LayoutInflater factory = LayoutInflater.from(getActivity());
            @SuppressLint("InflateParams") final View addDialogView = factory.inflate(R.layout.create_halaka_moshref, null);
            alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setView(addDialogView);
            alertDialog.show();
            halaka_name = alertDialog.findViewById(R.id.creat_halaka_et_name);
            spinner_halaka_stage = alertDialog.findViewById(R.id.creat_halaka_spinner_stage);
            spinner_halaka_mohafez = alertDialog.findViewById(R.id.creat_halaka_spinner_mohafez);
            if (spinner_halaka_mohafez != null) {
                spinner_halaka_mohafez.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (!mohafezs.isEmpty()) {
                            mohafezId = mohafezs.get(i).getId();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            spinner_halaka_stage.setAdapter(stagesAdapter);
            spinner_halaka_stage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> spinner, View v,
                                           int i, long arg3) {
                    spinner_halaka_mohafez.setSelection(0);
                    getMohafezsfromDatebase(stage_list.get(i));
                    mohafezId = "";
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }

            });

            btn_add = alertDialog.findViewById(R.id.creat_halaka_add_btn_save);
            if (btn_add != null) {
                btn_add.setOnClickListener(view -> {
                    if (Action.equals("Add")) {
                        addHalakaToDatabase();
                    } else {
                        updateHalaka();
                    }
                });
            }
            ImageButton imbtn_close = alertDialog.findViewById(R.id.creat_halaka_imgbtn_close);
            if (imbtn_close != null) {
                imbtn_close.setOnClickListener(view121 -> alertDialog.dismiss());
            }

        }
    }


    private void getMohafezsfromDatebase(String selectedVal) {
        db.collection("Mohafez")
                .whereEqualTo("stage", selectedVal)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mohafezs.clear();
                    mohafezs.add(new Mohafez("", "", "", "اختر المحفظ"));
                    for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                        mohafezs.add(queryDocumentSnapshots.toObjects(Mohafez.class).get(i));
                        adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                                android.R.layout.simple_spinner_item, mohafezs);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_halaka_mohafez.setAdapter(adapter);
                    }
                    for (int i = 0; i < mohafezs.size(); i++) {
                        if (mohafezs.get(i).getId().equals(retMohafezId)) {
                            spinner_halaka_mohafez.setSelection(i);
                        }
                    }
                });
    }

    private void addHalakaToDatabase() {
        if (validateInputs() && !mohafezId.isEmpty()) {
            db.collection("Mohafez").document(mohafezId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    if (Objects.requireNonNull(documentSnapshot.toObject(Mohafez.class)).getGroupId().trim().isEmpty()) {
                        String PushId = db.collection("Group").document().getId();
                        db.collection("Group")
                                .document(PushId).set(new Group(Objects.requireNonNull(halaka_name.getText()).toString().trim(),
                                mohafezId, spinner_halaka_stage.getSelectedItem().toString(), PushId));
                        db.collection("Mohafez").document(mohafezId).update("groupId", PushId);
                        alertDialog.dismiss();
                        sweetAlertDialog.showDialogSuccess("OK", "تم إضافة بيانات الحلقة بنجاح !").setConfirmButton("OK", sweetAlertDialog1 -> {
                            clearInputs();
                            sweetAlertDialog1.dismissWithAnimation();
                        });
                    } else {
                        sweetAlertDialog.showDialogError("المحفظ الذي قمت بإختياره لديه حلقة مسبقا ..");
                    }
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void showDialogUpdateHalaka(int position) {
        showDialogAddHalaka();
        btn_add.setText("update");
        halaka_name.setText(groups.get(position).getName());
        spinner_halaka_stage.setSelection(stage_list.indexOf(groups.get(position).getStage()));
        halakaId = groups.get(position).getGroupId();
        retMohafezId = groups.get(position).getMohafezId();
    }

    private void updateHalaka() {
        if (!halakaId.isEmpty()) {
            if (validateInputs()) {
                if (retMohafezId.equals(mohafezId)) {
                    db.collection("Group").document(halakaId).set(new Group(Objects.requireNonNull(halaka_name.getText()).toString().trim()
                            , retMohafezId, spinner_halaka_stage.getSelectedItem().toString(), halakaId));
                    db.collection("Mohafez").document(retMohafezId).update("groupId", halakaId);
                    alertDialog.dismiss();
                    sweetAlertDialog.showDialogSuccess("OK", "تم تحديث بيانات الحلقة بنجاح !").setConfirmButton("OK", sweetAlertDialog1 -> {
                        clearInputs();
                        sweetAlertDialog1.dismissWithAnimation();
                    });
                } else {
                    db.collection("Mohafez").document(mohafezId).get().addOnSuccessListener(documentSnapshot -> {
                        if (Objects.requireNonNull(documentSnapshot.get("groupId")).toString().trim().isEmpty()) {
                            db.collection("Group").document(halakaId).set(new Group(Objects.requireNonNull(halaka_name.getText()).toString().trim()
                                    , mohafezId, spinner_halaka_stage.getSelectedItem().toString(), halakaId));
                            db.collection("Mohafez").document(mohafezId).update("groupId", halakaId);
                            db.collection("Mohafez").document(retMohafezId).update("groupId", "");
                            sweetAlertDialog.showDialogSuccess("OK", "تم تحديث بيانات الحلقة بنجاح !").setConfirmButton("OK", sweetAlertDialog1 -> {
                                clearInputs();
                                sweetAlertDialog1.dismissWithAnimation();
                            });
                            alertDialog.dismiss();
                        } else {
                            sweetAlertDialog.showDialogError("المحفظ الذي قمت بإختياره لديه حلقة مسبقا ..");
                        }
                    });
                }

            }
        }
    }

    private boolean validateInputs() {
        if (!Objects.requireNonNull(halaka_name.getText()).toString().trim().isEmpty() &&
                spinner_halaka_stage.getSelectedItemPosition() != 0 &&
                spinner_halaka_mohafez.getSelectedItemPosition() != 0 &&
                checkGroupName(halaka_name.getText().toString().trim())
        ) {
            return true;
        } else {
            if (halaka_name.getText().toString().trim().isEmpty()) {
                sweetAlertDialog.showDialogError("حقل إسم الحلقة فارغ !");
                halaka_name.setFocusable(true);
            } else if (spinner_halaka_stage.getSelectedItemPosition() == 0) {
                sweetAlertDialog.showDialogError("يجب إختيار مرحلة ..");
            } else if (spinner_halaka_mohafez.getSelectedItemPosition() == 0) {
                sweetAlertDialog.showDialogError("يجب إختيار محفظ ..");
            } else if (!checkGroupName(halaka_name.getText().toString().trim())) {
                sweetAlertDialog.showDialogError("اسم الحلقة موجود بالفعل ..");
            }
        }
        return false;
    }

    private boolean checkGroupName(String groupName) {
        if (halakaId != null) {
            for (int i = 0; i < groups.size(); i++) {
                if (!halakaId.equals(groups.get(i).getGroupId())) {
                    if (groupName.equals(groups.get(i).getName())) {
                        return false;
                    }
                }
            }
        } else {
            for (int i = 0; i < groups.size(); i++) {
                if (groupName.equals(groups.get(i).getName())) {
                    return false;
                }
            }
        }
        return true;
    }

    private void SearchData(String newText) {
        AtomicBoolean isFound = new AtomicBoolean(false);
        CollectionReference Ref = db.collection("Group");
        Query query = Ref.whereEqualTo("stage", Common.currentSTAGE);
        if (newText.isEmpty()) {
            groups.clear();
            halakaAdapter.notifyDataSetChanged();
            LoadData();
        } else {
            groups.clear();
            query = query.orderBy("name").startAt(newText).endAt(newText + "\uf8ff");
            query.addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (queryDocumentSnapshots != null) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        isFound.set(true);
                        groups.clear();
                        for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots) {
                            groups.add(snapshots.toObject(Group.class));
                            halakaAdapter.notifyDataSetChanged();
                        }
                    } else {
                        groups.clear();
                        halakaAdapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "لم يتم العثور على طلبك !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    halakaAdapter.notifyDataSetChanged();
                    if (!isFound.get()) {
                        Toast.makeText(getContext(), "لم يتم العثور على طلبك !", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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
                // halakaAdapter.getFilter().filter(query);
                SearchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //halakaAdapter.getFilter().filter(newText);
                SearchData(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)) {
            db.collection("PermissionsUsers")
                    .document("permissionsUsers")
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.get("permissionsEdare.updateHalaka", Boolean.TYPE)) {
                        showDialogUpdateHalaka(item.getOrder());
                    } else {
                        new SweetAlertDialog_(getContext()).showDialogError("لم يتم منحك صلاحية تحديث حلقة يرجى مراجعة مسؤول المركز");
                    }
                }
            });
        }
        return super.onContextItemSelected(item);

    }

    private void clearInputs() {
        halaka_name.setText("");
        spinner_halaka_stage.setSelection(0);
        spinner_halaka_mohafez.setSelection(0);
        halakaId = "";
        mohafezId = "";
        Action = "";
    }
}