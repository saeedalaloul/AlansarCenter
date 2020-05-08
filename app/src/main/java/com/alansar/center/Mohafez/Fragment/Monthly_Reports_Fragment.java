package com.alansar.center.Mohafez.Fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Common.Common;
import com.alansar.center.Mohafez.Adapter.MonthlyReportsAdapter;
import com.alansar.center.Mohafez.Model.Report;
import com.alansar.center.R;
import com.alansar.center.SweetAlertDialog_;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class Monthly_Reports_Fragment extends Fragment {
    private FirebaseFirestore db;
    private MonthlyReportsAdapter adapter;
    private ArrayList<Report> reportArrayList;
    private ListenerRegistration registration;
    private ImageButton btnSearch;
    private AlertDialog alertDialog;
    private Spinner sp_year, sp_month;
    private ArrayList<Integer> monthsArrayList;

    public Monthly_Reports_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mohafez_monthly__reports, container, false);
        if (getContext() != null)
            initial(view);
        btnSearch.setOnClickListener(view1 -> showDialogSearch());
        return view;
    }

    private void showDialogSearch() {
        if (getContext() != null && getActivity() != null) {
            List<String> enters_month = new ArrayList<>();
            List<String> enters_year = new ArrayList<>();
            enters_month.add("اختر الشهر");
            enters_year.add("اختر السنة");
            LayoutInflater factory = LayoutInflater.from(getActivity());
            @SuppressLint("InflateParams") final View addExamDialogView = factory.inflate(R.layout.dialog_date, null);
            alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setView(addExamDialogView);
            alertDialog.show();
            sp_month = alertDialog.findViewById(R.id.dialog_date_sp_month);
            sp_year = alertDialog.findViewById(R.id.dialog_date_sp_year);
            ImageButton imbtn_close = alertDialog.findViewById(R.id.dialog_date_imgbtn_close);
            assert imbtn_close != null;
            imbtn_close.setOnClickListener(view121 -> alertDialog.dismiss());
            Button btn_search = alertDialog.findViewById(R.id.dialog_date_btn_search);
            assert btn_search != null;
            btn_search.setOnClickListener(view -> {
                if (validateSpinner()) {
                    if (sp_month.getSelectedItemPosition() != 0) {
                        searchFromDBWithMonth();
                    } else {
                        searchFromDB();
                    }
                }
            });
            for (int i = 1; i <= 12; i++) {
                enters_month.add("" + i);
            }
            for (int i = 2020; i <= 2030; i++) {
                enters_year.add("" + i);
            }
            ArrayAdapter<String> adapter_month = new ArrayAdapter<>(
                    getContext(), android.R.layout.simple_spinner_item, enters_month);
            adapter_month.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_month.setAdapter(adapter_month);

            ArrayAdapter<String> adapter_year = new ArrayAdapter<>(
                    getContext(), android.R.layout.simple_spinner_item, enters_year);
            adapter_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_year.setAdapter(adapter_year);
        }
    }

    private void searchFromDBWithMonth() {
        if (Common.currentPerson != null && !Common.currentPerson.getId().isEmpty()
                && Common.currentGroupId != null) {
            registration = db.collection("DailyReport")
                    .whereEqualTo("year", Integer.parseInt(sp_year.getSelectedItem().toString()))
                    .whereEqualTo("idGroup", Common.currentGroupId)
                    .whereEqualTo("month", Integer.parseInt(sp_month.getSelectedItem().toString()))
                    .limit(1)
                    .addSnapshotListener((queryDocumentSnapshots, e) -> {
                        if (e != null) {
                            Log.d("sss", "listen:error" + e.getLocalizedMessage());
                            return;
                        }

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            reportArrayList.clear();
                            Report report = new Report(queryDocumentSnapshots.getDocuments().get(0).get("month", Integer.TYPE),
                                    queryDocumentSnapshots.getDocuments().get(0).get("year", Integer.TYPE));
                            reportArrayList.add(report);
                            Collections.reverse(reportArrayList);
                            adapter.notifyDataSetChanged();
                            new SweetAlertDialog_(getActivity())
                                    .showDialogSuccess("OK", "بحثك جاهز");
                            alertDialog.dismiss();
                        } else {
                            new SweetAlertDialog_(getActivity())
                                    .showDialogError("عذرا بحثك غير متوفر !");
                        }
                    });
        }
    }

    private void searchFromDB() {
        if (Common.currentPerson != null && !Common.currentPerson.getId().isEmpty()
                && Common.currentGroupId != null) {
            reportArrayList.clear();
            for (int i = 1; i <= monthsArrayList.size(); i++) {
                int finalI = i;
                registration = db.collection("DailyReport")
                        .whereEqualTo("year", Integer.parseInt(sp_year.getSelectedItem().toString()))
                        .whereEqualTo("idGroup", Common.currentGroupId)
                        .whereEqualTo("month", i)
                        .limit(1)
                        .addSnapshotListener((queryDocumentSnapshots, e) -> {
                            if (e != null) {
                                Log.d("sss", "listen:error" + e.getLocalizedMessage());
                                return;
                            }

                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                Report report = new Report(queryDocumentSnapshots.getDocuments().get(0).get("month", Integer.TYPE),
                                        queryDocumentSnapshots.getDocuments().get(0).get("year", Integer.TYPE));
                                reportArrayList.add(report);
                                Collections.reverse(reportArrayList);
                                adapter.notifyDataSetChanged();
//                                if (finalI == 11)
//                                {
//                                    SweetAlertDialog_ sweetAlertDialog_ = new SweetAlertDialog_(getActivity());
//                                    sweetAlertDialog_.showDialogSuccess("OK", "بحثك جاهز");
//                                }
                                alertDialog.dismiss();
                            }
//                            else {
//                                if (finalI ==11)
//                                  new SweetAlertDialog_(getActivity())
//                                        .showDialogError("عذرا بحثك غير متوفر !");
//                            }
                        });
            }
        }
    }

    private boolean validateSpinner() {
        if (sp_year.getSelectedItemPosition() != 0) {
            return true;
        } else {
            new SweetAlertDialog_(getActivity())
                    .showDialogError("يجب اختيار السنة ..");
            return false;
        }
    }

    private void initial(View view) {
        btnSearch = view.findViewById(R.id.btn_search_report);
        reportArrayList = new ArrayList<>();
        monthsArrayList = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            monthsArrayList.add(i);
        }

        db = FirebaseFirestore.getInstance();

        RecyclerView recyclerView = view.findViewById(R.id.rv_reports);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        adapter = new MonthlyReportsAdapter(reportArrayList, getContext());
        recyclerView.setAdapter(adapter);
    }

    private void LoadData() {
        Calendar calendar = Calendar.getInstance();
        if (Common.currentPerson != null && !Common.currentPerson.getId().isEmpty()
                && Common.currentGroupId != null) {
            for (int i = 1; i <= monthsArrayList.size(); i++) {
                registration = db.collection("DailyReport")
                        .whereEqualTo("year", calendar.get(Calendar.YEAR))
                        .whereEqualTo("idGroup", Common.currentGroupId)
                        .whereEqualTo("month", i)
                        .limit(1)
                        .addSnapshotListener((queryDocumentSnapshots, e) -> {
                            if (e != null) {
                                Log.d("sss", "listen:error" + e.getLocalizedMessage());
                                return;
                            }
                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                Report report = new Report(queryDocumentSnapshots.getDocuments().get(0).get("month", Integer.TYPE),
                                        queryDocumentSnapshots.getDocuments().get(0).get("year", Integer.TYPE));
                                reportArrayList.add(report);
                                Collections.reverse(reportArrayList);
                                adapter.notifyDataSetChanged();
                            }
                        });
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (reportArrayList != null) {
            reportArrayList.clear();
        }
        RequestPermissions();
    }

    private void RequestPermissions() {
        Dexter.withContext(getContext())
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        LoadData();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        if (permissionDeniedResponse.isPermanentlyDenied() && getContext() != null) {
                            // navigate user to app settings
                            Toast.makeText(getContext(), "بجب منح التطبيق صلاحية التخزين حتى تتمكن من استخدام التقارير الشهرية ..", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.fromParts("package", getContext().getPackageName(), null));
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).withErrorListener(dexterError -> {
            if (dexterError != null) {
                Toast.makeText(getContext(), "Error :" + dexterError.toString(), Toast.LENGTH_SHORT).show();
            }
        }).check();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (registration != null) {
            registration.remove();
        }
    }
}