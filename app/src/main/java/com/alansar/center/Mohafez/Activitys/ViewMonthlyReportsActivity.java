package com.alansar.center.Mohafez.Activitys;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Common.Common;
import com.alansar.center.Mohafez.Adapter.ViewMonthlyReportsAdapter;
import com.alansar.center.Mohafez.Model.DailyReport;
import com.alansar.center.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ViewMonthlyReportsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String UID;
    private ArrayList<DailyReport> dailyReports;
    private ViewMonthlyReportsAdapter adapter;
    private Calendar calendar;
    private int year, month;
    private ListenerRegistration registration;

    public ViewMonthlyReportsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mohafez_view_monthly_reports);
        initial();

        if (getIntent() != null && getIntent().getStringExtra("UID") != null) {
            UID = getIntent().getStringExtra("UID");
        } else {
            Toast.makeText(this, "لا توجد تقارير شهرية !", Toast.LENGTH_SHORT).show();
            finish();
        }

        setSupportActionBar(findViewById(R.id.toolBar));
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (registration != null) {
            registration.remove();
        }
    }

    private void LoadData() {
        year = calendar.get(Calendar.YEAR);
        registration = db.collection("DailyReport")
                .whereEqualTo("year", year)
                .whereEqualTo("uid", UID)
                .orderBy("month", Query.Direction.DESCENDING)
                .limit(1)
                .addSnapshotListener(this, (queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.w("sss", "listen:error" + e.getLocalizedMessage());
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            month = Integer.parseInt(Objects.requireNonNull(queryDocumentSnapshots
                                    .getDocuments().get(0)
                                    .get("month")).toString());
                            registration = db.collection("DailyReport")
                                    .whereEqualTo("uid", UID)
                                    .whereEqualTo("year", year)
                                    .whereEqualTo("month", queryDocumentSnapshots.getDocuments().get(0).get("month"))
                                    .orderBy("day", Query.Direction.DESCENDING)
                                    .addSnapshotListener((queryDocumentSnapshots1, e1) -> {
                                        if (e1 != null) {
                                            Log.w("sss", "listen:error 1" + e1.getLocalizedMessage());
                                            return;
                                        }
                                        if (queryDocumentSnapshots1 != null) {
                                            dailyReports.clear();
                                            for (DocumentChange dc : queryDocumentSnapshots1.getDocumentChanges()) {
                                                dailyReports.add(dc.getDocument().toObject(DailyReport.class));
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE_DAILY_RECITATIONS)) {
            startActivity(new Intent(this, UpdateDailyMemorizationActivity.class)
                    .putExtra("UID", dailyReports.get(item.getOrder()).getUID())
                    .putExtra("id_Report", dailyReports.get(item.getOrder()).getId()));
        } else if (item.getTitle().equals(Common.DELETE_DAILY_RECITATIONS)) {
            deleteReportOfDB(dailyReports.get(item.getOrder()).getId());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteReportOfDB(String id) {
        final SweetAlertDialog BtnDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setContentText("هل أنت متأكد من ذلك ؟")
                .setConfirmText("تأكيد")
                .setCancelText("إلغاء")
                .setTitleText("حذف التقرير");

        BtnDialog.setOnShowListener(dialog -> {
            BtnDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setOnClickListener(view -> {
                db.collection("DailyReport").document(id).delete();
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setContentText("تمت");
                sweetAlertDialog.setTitleText("تم حذف التقرير بنجاح !");
                dialog.dismiss();
                sweetAlertDialog.show();
            });

            BtnDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setOnClickListener(view -> dialog.dismiss());
        });
        BtnDialog.show();
    }

    private void initial() {
        RecyclerView recyclerView = findViewById(R.id.rv_show_monthly_reports);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        dailyReports = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        adapter = new ViewMonthlyReportsAdapter(dailyReports);
        recyclerView.setAdapter(adapter);
        calendar = Calendar.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fg_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //adapter.getFilter().filter(query)SearchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // adapter.getFilter().filter(newText);
                SearchData(newText);
                return false;
            }
        });
        return true;
    }

    private void SearchData(String newText) {
        AtomicBoolean isFound = new AtomicBoolean(false);
        if (newText.isEmpty()) {
            dailyReports.clear();
            LoadData();
            adapter.notifyDataSetChanged();
        } else {
            dailyReports.clear();
            db.collection("DailyReport").whereEqualTo("uid", UID).
                    whereEqualTo("year", year)
                    .whereEqualTo("month", month)
                    .orderBy("dayOfWeek").startAt(newText).endAt(newText + "\uf8ff")
                    .addSnapshotListener((queryDocumentSnapshots, e) -> {
                        if (e != null) {
                            Log.w("sss", "listen:error" + e.getLocalizedMessage());
                            return;
                        }
                        if (queryDocumentSnapshots != null) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                isFound.set(true);
                                dailyReports.clear();
                                for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots) {
                                    dailyReports.add(snapshots.toObject(DailyReport.class));
                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(this, "لم يتم العثور على طلبك !", Toast.LENGTH_SHORT).show();
                                dailyReports.clear();
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            adapter.notifyDataSetChanged();
                            if (!isFound.get()) {
                                Toast.makeText(this, "لم يتم العثور على طلبك !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}