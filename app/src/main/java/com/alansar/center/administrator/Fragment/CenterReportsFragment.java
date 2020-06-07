package com.alansar.center.administrator.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.alansar.center.Models.DownloadDataFromDB;
import com.alansar.center.Mohafez.Model.Mohafez;
import com.alansar.center.R;
import com.alansar.center.Services.DownloadReportService;
import com.alansar.center.SweetAlertDialog_;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CenterReportsFragment extends Fragment {
    private Spinner sp_mohafzeen, sp_year, sp_month, sp_stages, sp_mohafzeen_custom;
    private ImageButton btn_downLoad_report_monthly, btn_downLoad_report_all, btn_downLoad_report_custom;
    private FirebaseFirestore db;
    private ArrayAdapter<Mohafez> adapter;
    private ArrayAdapter<Integer> years_adapter;
    private ArrayAdapter<Integer> month_adapter;
    private List<Mohafez> mohafezs;
    private SweetAlertDialog_ sweetAlertDialog;

    public CenterReportsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view1 = inflater.inflate(R.layout.fragment_admin_center_reports, container, false);
        initial(view1);
        getMohafzeen();
        sp_mohafzeen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sp_year.setSelection(0);
                sp_month.setSelection(0);
                if (i != 0) {
                    getYearsOfReports(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sp_month.setSelection(0);
                if (i != 0) {
                    getMonthsOfReports(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_downLoad_report_monthly.setOnClickListener(view -> {
            if (isValidateSpinnerReportsMonthly()) {
                downLoadReportMonthly();
            }
        });

        sp_stages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    getMohafzeenCustom();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_downLoad_report_all.setOnClickListener(view -> showDialogSelectColumns(0));
        btn_downLoad_report_custom.setOnClickListener(view -> {
            if (isValidateSpinnerReportsDBCustom()) {
                if (sp_mohafzeen_custom.getSelectedItemPosition() != 0) {
                    showDialogSelectColumns(2);
                } else {
                    showDialogSelectColumns(1);
                }
            }
        });
        return view1;
    }

    private boolean isValidateSpinnerReportsDBCustom() {
        if (sp_stages.getSelectedItemPosition() != 0) {
            return true;
        } else {
            sweetAlertDialog.showDialogError("عذرا يجب اختيار مرحلة !");
        }
        return false;
    }

    private void getMohafzeenCustom() {
        if (getActivity() != null && sp_stages.getSelectedItem() != null) {
            mohafezs.clear();
            mohafezs.add(new Mohafez("", "", "", "اختر المحفظ"));
            db.collection("Mohafez")
                    .whereEqualTo("stage", "" + sp_stages.getSelectedItem().toString())
                    .get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        mohafezs.add(snapshot.toObject(Mohafez.class));
                    }
                    adapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_spinner_item, mohafezs);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_mohafzeen_custom.setAdapter(adapter);
                }
            }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
        }
    }

    private void showDialogSelectColumns(int type) {
        if (getContext() != null) {
            // Set up the alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("حدد الأعمدة التي تريدها أو حدد الكل");

            List<String> ColumnsList = new ArrayList<>();

// Add a checkbox list
            String[] Columns = {"اسم الطالب رباعي", "الصف", "الحفظ الكلي", "المرحلة", "جوال ولي الأمر", "سنة الميلاد", "تاريخ الميلاد", "رقم هوية الطالب", "اسم المحفظ", "العمر"};
            boolean[] checkedItems = {false, false, false, false, false, false, false, false, false, false};

            builder.setMultiChoiceItems(Columns, checkedItems, (dialog, which, isChecked) -> {
                // The user checked or unchecked a box
                if (isChecked) {
                    // If the user checked the item, add it to the selected items
                    ColumnsList.add(Columns[which]);
                } else {
                    // Else, if the item is already in the array, remove it
                    ColumnsList.remove(Columns[which]);
                }

            });

// Add OK and Cancel buttons
            builder.setPositiveButton("تحميل التقرير", (dialog, which) -> {
                // The user clicked OK
                if (!ColumnsList.isEmpty()) {
                    if (type == 0) {
                        new DownloadDataFromDB(getContext(), ColumnsList, FirebaseFirestore.getInstance(), null, null);
                    } else if (type == 1) {
                        if (sp_stages.getSelectedItem() != null)
                            new DownloadDataFromDB(getContext(), ColumnsList, FirebaseFirestore.getInstance(), null, sp_stages.getSelectedItem().toString());
                    } else if (type == 2) {
                        if (sp_mohafzeen_custom.getSelectedItem() != null && sp_stages.getSelectedItem() != null) {
                            new DownloadDataFromDB(getContext(), ColumnsList,
                                    FirebaseFirestore.getInstance(),
                                    mohafezs.get(sp_mohafzeen_custom.getSelectedItemPosition()).getGroupId(),
                                    sp_stages.getSelectedItem().toString());
                        }
                    }
                } else {
                    sweetAlertDialog.showDialogError("عذرا يجب تحديد عمود ..");
                }
            });

            builder.setNeutralButton("حدد الكل", (dialogInterface, i) -> {
                Arrays.fill(checkedItems, true);
                ColumnsList.clear();
                Collections.addAll(ColumnsList, Columns);
                AlertDialog dialog = builder.create();
                dialog.show();
            });

            builder.setNegativeButton("إلغاء", null);

// Create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void downLoadReportMonthly() {
        if (getContext() != null) {
            if (DownloadReportService.isRunning) {
                Toast.makeText(getContext(), "عذرا يتم تحميل التقرير الشهري في الخلفية ..", Toast.LENGTH_SHORT).show();
            } else {
                getContext().startService(new Intent(getContext(), DownloadReportService.class)
                        .putExtra("month", Integer.parseInt(sp_month.getSelectedItem().toString()))
                        .putExtra("year", Integer.parseInt(sp_year.getSelectedItem().toString()))
                        .putExtra("groupId", mohafezs.get(sp_mohafzeen.getSelectedItemPosition()).getGroupId())
                        .putExtra("mohafezName", mohafezs.get(sp_mohafzeen.getSelectedItemPosition()).getName()));
            }
        }
    }

    private boolean isValidateSpinnerReportsMonthly() {
        if (sp_mohafzeen.getSelectedItemPosition() != 0
                && sp_year.getSelectedItemPosition() != 0
                && sp_month.getSelectedItemPosition() != 0) {
            return true;
        } else if (sp_mohafzeen.getSelectedItemPosition() == 0) {
            sweetAlertDialog.showDialogError("يجب اختيار محفظ");
        } else if (sp_year.getSelectedItemPosition() == 0) {
            sweetAlertDialog.showDialogError("يجب اختيار السنة");

        } else if (sp_month.getSelectedItemPosition() == 0) {
            sweetAlertDialog.showDialogError("يجب اختيار الشهر");
        }
        return false;
    }

    private void getYearsOfReports(int i) {
        if (getActivity() != null) {
            List<Integer> years = new ArrayList<>();
            years.add(0);
            for (int j = 2020; j <= 2030; j++) {
                int finalJ = j;
                db.collection("DailyReport")
                        .whereEqualTo("idGroup", mohafezs.get(i).getGroupId())
                        .whereEqualTo("year", j)
                        .limit(1)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                years.add(finalJ);
                                years_adapter = new ArrayAdapter<>(getActivity(),
                                        android.R.layout.simple_spinner_item, years);
                                years_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_year.setAdapter(years_adapter);
                            }
                        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
            }
        }
    }

    private void getMonthsOfReports(int i) {
        if (getActivity() != null) {
            List<Integer> months = new ArrayList<>();
            months.add(0);
            for (int j = 1; j <= 12; j++) {
                int finalJ = j;
                db.collection("DailyReport")
                        .whereEqualTo("idGroup", mohafezs.get(i).getGroupId())
                        .whereEqualTo("year", Integer.parseInt(sp_year.getSelectedItem().toString()))
                        .whereEqualTo("month", j)
                        .limit(1)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                months.add(finalJ);
                                month_adapter = new ArrayAdapter<>(getActivity(),
                                        android.R.layout.simple_spinner_item, months);
                                month_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_month.setAdapter(month_adapter);
                            }
                        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
            }
        }
    }

    private void getMohafzeen() {
        if (getActivity() != null) {
            mohafezs.add(new Mohafez("", "", "", "اختر المحفظ"));
            db.collection("Mohafez")
                    .get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        mohafezs.add(snapshot.toObject(Mohafez.class));
                    }
                    adapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_spinner_item, mohafezs);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_mohafzeen.setAdapter(adapter);
                }
            }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
        }
    }

    private void initial(View view) {
        sp_mohafzeen = view.findViewById(R.id.sp_center_reports_mohafzeen);
        sp_year = view.findViewById(R.id.sp_center_reports_years);
        sp_month = view.findViewById(R.id.sp_center_reports_month);
        sp_stages = view.findViewById(R.id.sp_center_reports_stages);
        sp_mohafzeen_custom = view.findViewById(R.id.sp_center_reports_mohafzeen_custom);
        btn_downLoad_report_custom = view.findViewById(R.id.imgbtn_center_reports_download_report_custom);
        btn_downLoad_report_all = view.findViewById(R.id.imgbtn_center_reports_download_report_all);
        btn_downLoad_report_monthly = view.findViewById(R.id.imgbtn_center_reports_download_report);
        mohafezs = new ArrayList<>();
        sweetAlertDialog = new SweetAlertDialog_(getContext());
        db = FirebaseFirestore.getInstance();
    }
}
