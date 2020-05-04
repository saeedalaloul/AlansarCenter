package com.alansar.center.supervisor_exams.Fragments;

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
import com.alansar.center.Mohafez.Model.Mohafez;
import com.alansar.center.R;
import com.alansar.center.SweetAlertDialog_;
import com.alansar.center.supervisor_exams.Adapter.TestersAdapter;
import com.alansar.center.testers.Model.Tester;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class TestersFragment extends Fragment {
    private FirebaseFirestore db;
    private AlertDialog alertDialog;
    private Spinner sp_mohafezeen, sp_stage;
    private int position;
    private ArrayList<Mohafez> mohafezs;
    private ArrayAdapter<Mohafez> adapter;
    private SweetAlertDialog_ sweetAlertDialog;
    private ArrayList<Tester> testers;
    private TestersAdapter testersAdapter;
    private View view;
    private RecyclerView recyclerView;
    private ListenerRegistration registration;

    public TestersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_supervisor_exams_testers, container, false);
        mohafezs = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        FloatingActionButton btnaddMohafez = view.findViewById(R.id.btn_add_tester);
        btnaddMohafez.setOnClickListener(view1 -> showDialogAddTester());
        sweetAlertDialog = new SweetAlertDialog_(getContext());
        testers = new ArrayList<>();
        recyclerView = view.findViewById(R.id.tester_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        testersAdapter = new TestersAdapter(testers);
        recyclerView.setAdapter(testersAdapter);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (registration != null) {
            registration.remove();
        }
    }

    private void loadData() {
        registration = db.collection("Tester").addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w("sss", "listen:error" + e.getLocalizedMessage());
                return;
            }
            if (queryDocumentSnapshots != null) {
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d("sss", "New tester: " + dc.getDocument().getData());
                            testers.clear();
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                if (doc.exists()) {
                                    testers.add(doc.toObject(Tester.class));
                                    testersAdapter.notifyDataSetChanged();
                                }
                            }
                            break;
                        case MODIFIED:
                            Log.d("sss", "Modified tester: " + dc.getDocument().getData());
                            testers.clear();
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                if (doc.exists()) {
                                    testers.add(doc.toObject(Tester.class));
                                    testersAdapter.notifyDataSetChanged();
                                }
                            }
                            break;
                        case REMOVED:
                            Log.d("sss", "Removed tester: " + dc.getDocument().getData());
                            testers.remove(dc.getOldIndex());
                            testersAdapter.notifyDataSetChanged();
                            break;
                    }
                    if (testers.isEmpty()) {
                        view.findViewById(R.id.tv_check_testers).setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        view.findViewById(R.id.tv_check_testers).setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void showDialogAddTester() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") final View addTesterDialogView = factory.inflate(R.layout.add_tester_dialog, null);
        alertDialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity())).create();
        alertDialog.setView(addTesterDialogView);
        alertDialog.show();
        sp_stage = alertDialog.findViewById(R.id.add_tester_spinner_stage);
        sp_mohafezeen = alertDialog.findViewById(R.id.add_tester_spinner_mohafezen);

        assert sp_mohafezeen != null;
        sp_mohafezeen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!mohafezs.isEmpty()) {
                    position = i;
                    Toast.makeText(getContext(), "" + i, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        sp_stage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> spinner, View v,
                                       int arg2, long arg3) {
                String selectedVal = getResources().getStringArray(R.array.stage_array)[spinner.getSelectedItemPosition()];
                sp_mohafezeen.setSelection(0);
                getMohafezsfromDatebase(selectedVal);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }

        });

        Button btn_add = alertDialog.findViewById(R.id.add_tester_btn_add);
        assert btn_add != null;
        btn_add.setOnClickListener(view -> {
            if (validateInputs()) {
                addTesterofDB();
            }
        });
        ImageButton imbtn_close = alertDialog.findViewById(R.id.add_tester_imgbtn_close);
        assert imbtn_close != null;
        imbtn_close.setOnClickListener(view121 -> alertDialog.dismiss());
    }

    private void addTesterofDB() {
        if (position > 0) {
            db.collection("Tester").document(mohafezs.get(position).getId()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            sweetAlertDialog.showDialogError("المختبر موجود بالفعل !");
                        } else {
                            db.collection("Tester")
                                    .document(mohafezs.get(position).getId())
                                    .set(new Tester(mohafezs.get(position).getId(),
                                            mohafezs.get(position).getName(), mohafezs.get(position).getStage()));
                            db.collection("Person")
                                    .document(mohafezs.get(position).getId())
                                    .update("permissions", FieldValue.arrayUnion(Common.PERMISSIONS_TESTER));

                            alertDialog.dismiss();
                            sweetAlertDialog.showDialogSuccess("OK", "تم اضافة المختبر بنجاح !");
                        }
                    });
        }
    }

    private boolean validateInputs() {
        if (sp_stage.getSelectedItemPosition() != 0
                && sp_mohafezeen.getSelectedItemPosition() != 0
        ) {
            return true;
        } else if (sp_stage.getSelectedItemPosition() == 0) {
            sweetAlertDialog.showDialogError("يجب تحديد المرحلة !");
        } else if (sp_mohafezeen.getSelectedItemPosition() == 0) {
            sweetAlertDialog.showDialogError("يجب تحديد المحفظ !");
        }

        return false;
    }

    private void getMohafezsfromDatebase(String selectedVal) {
        db.collection("Mohafez").whereEqualTo("stage", selectedVal).get().addOnSuccessListener(queryDocumentSnapshots -> {
            mohafezs.clear();
            mohafezs.add(new Mohafez("", "", "", "اختر المحفظ"));
            for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                mohafezs.add(queryDocumentSnapshots.toObjects(Mohafez.class).get(i));
                adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                        android.R.layout.simple_spinner_item, mohafezs);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_mohafezeen.setAdapter(adapter);
            }
        });
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
                SearchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SearchData(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void SearchData(String newText) {
        AtomicBoolean isFound = new AtomicBoolean(false);
        Query query = db.collection("Tester");
        if (newText.isEmpty()) {
            testers.clear();
            loadData();
            testersAdapter.notifyDataSetChanged();
        } else {
            testers.clear();
            query = query.orderBy("name").startAt(newText).endAt(newText + "\uf8ff");
            query.addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (queryDocumentSnapshots != null) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        isFound.set(true);
                        testers.clear();
                        for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots) {
                            testers.add(snapshots.toObject(Tester.class));
                            testersAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getContext(), "لم يتم العثور على طلبك !", Toast.LENGTH_SHORT).show();
                        testers.clear();
                        testersAdapter.notifyDataSetChanged();
                    }
                } else {
                    testersAdapter.notifyDataSetChanged();
                    if (!isFound.get()) {
                        Toast.makeText(getContext(), "لم يتم العثور على طلبك !", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.DELETE)) {
            deleteTesterOfDB(testers.get(item.getOrder()).getUID());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteTesterOfDB(String uid) {
        final SweetAlertDialog BtnDialog = new SweetAlertDialog(Objects.requireNonNull(getActivity()), SweetAlertDialog.WARNING_TYPE)
                .setContentText("هل أنت متأكد من ذلك ؟")
                .setConfirmText("تأكيد")
                .setCancelText("إلغاء")
                .setTitleText("حذف الحساب");

        BtnDialog.setOnShowListener(dialog -> {
            BtnDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setOnClickListener(view -> {
                db.collection("Tester").document(uid).delete();
                db.collection("Person").document(uid).update("permissions", FieldValue.arrayRemove(Common.PERMISSIONS_TESTER));
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(Objects.requireNonNull(getContext()), SweetAlertDialog.SUCCESS_TYPE)
                        .setContentText("تمت");
                sweetAlertDialog.setTitleText("تم حذف الحساب بنجاح !");
                dialog.dismiss();
                sweetAlertDialog.show();
            });

            BtnDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setOnClickListener(view -> dialog.dismiss());
        });
        BtnDialog.show();
    }
}