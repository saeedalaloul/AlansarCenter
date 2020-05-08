package com.alansar.center.Mohafez.Fragment;


import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Common.Common;
import com.alansar.center.Mohafez.Adapter.OrdersExamsAdapter;
import com.alansar.center.R;
import com.alansar.center.SweetAlertDialog_;
import com.alansar.center.supervisor_exams.Model.Exam;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 */
public class Orders_Exams_Fragment extends Fragment {
    private FirebaseFirestore db;
    private OrdersExamsAdapter adapter;
    private ArrayList<Exam> exams;
    private SweetAlertDialog_ sweetAlertDialog;
    private RecyclerView recyclerView;
    private View view;
    private ListenerRegistration registration;


    public Orders_Exams_Fragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_mohafez_orders__exams, container, false);
        recyclerView = view.findViewById(R.id.exams_orders_rv);
        db = FirebaseFirestore.getInstance();
        sweetAlertDialog = new SweetAlertDialog_(getContext());
        exams = new ArrayList<>();
        adapter = new OrdersExamsAdapter(exams);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadData() {
        if (Common.currentPerson != null) {
            List<Integer> list = new ArrayList<>();
            list.add(0);
            list.add(1);
            list.add(2);
            list.add(-1);
            list.add(-2);
            list.add(-3);
            registration = db.collection("Exam")
                    .whereEqualTo("idMohafez", Common.currentPerson.getId())
                    .whereIn("statusAcceptance", list)
                    .orderBy("year", Query.Direction.DESCENDING)
                    .orderBy("month", Query.Direction.DESCENDING)
                    .orderBy("day", Query.Direction.DESCENDING)
                    .addSnapshotListener((queryDocumentSnapshots, e) -> {
                        if (e != null) {
                            Log.w("sss", "listen:error" + e.getLocalizedMessage());
                            return;
                        }
                        if (queryDocumentSnapshots != null) {
                            SimpleDateFormat dateformat = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                dateformat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa", Locale.getDefault());
                            }
                            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                                switch (dc.getType()) {
                                    case ADDED:
                                        Log.d("sss", "New exam: " + dc.getDocument().getData());
                                        exams.clear();
                                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                            if (doc.exists()) {
                                                Exam exam = doc.toObject(Exam.class);
                                                if (dateformat != null) {
                                                    String date = dateformat.format(Timestamp.now().toDate().getTime());
                                                    try {
                                                        if (exam.getDateRejection() != null && !exam.getDateRejection().trim().isEmpty()) {
                                                            getDifferenceDate(dateformat.parse(exam.getDateRejection()), dateformat.parse(date), doc);
                                                        }
                                                        if (exam.getDate() != null && !exam.getDate().trim().isEmpty()) {
                                                            getDifferenceDate(dateformat.parse(exam.getDate()), dateformat.parse(date), doc);
                                                        }
                                                    } catch (ParseException ex) {
                                                        ex.printStackTrace();
                                                    }
                                                }
                                                if (!doc.getBoolean("isSeenExam.isSeenMohafez")) {
                                                    doc.getReference().update("isSeenExam.isSeenMohafez", true);
                                                }
                                                exams.add(exam);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                        break;
                                    case MODIFIED:
                                        Log.d("sss", "Modified exam: " + dc.getDocument().getData());
                                        exams.clear();
                                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                            if (doc.exists()) {
                                                Exam exam = doc.toObject(Exam.class);
                                                if (dateformat != null) {
                                                    String date = dateformat.format(Timestamp.now().toDate().getTime());
                                                    try {
                                                        if (exam.getDateRejection() != null && !exam.getDateRejection().trim().isEmpty()) {
                                                            getDifferenceDate(dateformat.parse(exam.getDateRejection()), dateformat.parse(date), doc);
                                                        }
                                                        if (exam.getDate() != null && !exam.getDate().trim().isEmpty()) {
                                                            getDifferenceDate(dateformat.parse(exam.getDate()), dateformat.parse(date), doc);
                                                        }
                                                    } catch (ParseException ex) {
                                                        ex.printStackTrace();
                                                    }
                                                }
                                                if (!doc.getBoolean("isSeenExam.isSeenMohafez")) {
                                                    doc.getReference().update("isSeenExam.isSeenMohafez", true);
                                                }
                                                exams.add(exam);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                        break;
                                    case REMOVED:
                                        Log.d("sss", "Removed exam: " + dc.getDocument().getData());
                                        exams.remove(dc.getOldIndex());
                                        adapter.notifyDataSetChanged();
                                        break;
                                }
                            }
                            if (exams.isEmpty()) {
                                view.findViewById(R.id.tv_check_exams).setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                view.findViewById(R.id.tv_check_exams).setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        } else {
            Common.currentPerson = Paper.book().read(Common.PERSON);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.DELETE_AN_ORDER)) {
            showDialogDeleteOrder(item.getOrder());
        }
        return super.onContextItemSelected(item);
    }

    private void showDialogDeleteOrder(int order) {
        final SweetAlertDialog BtnDialog = new SweetAlertDialog(Objects.requireNonNull(getActivity()), SweetAlertDialog.NORMAL_TYPE)
                .setContentText("هل أنت متأكد من ذلك ؟")
                .setConfirmText("تأكيد")
                .setCancelText("إلغاء")
                .setTitleText("حذف الطلب");

        BtnDialog.setOnShowListener(dialog -> {
            BtnDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setOnClickListener(view -> {

                if (exams.get(order).getStatusAcceptance() == 0 ||
                        exams.get(order).getStatusAcceptance() == -1 ||
                        exams.get(order).getStatusAcceptance() == -2 ||
                        exams.get(order).getStatusAcceptance() == -3) {
                    db.collection("Exam").document(exams.get(order).getId()).delete();
                    sweetAlertDialog.showDialogSuccess("OK", "تم حذف طلب الإختبار بنجاح !")
                            .setConfirmButton("OK", SweetAlertDialog::dismissWithAnimation);
                } else {
                    sweetAlertDialog.showDialogError("لم تكتمل العملية المطلوبة بنجاح بسبب إجراءات قبول الطلب !");
                }

                dialog.dismiss();
            });

            BtnDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setOnClickListener(view -> dialog.dismiss());
        });
        BtnDialog.show();
    }

    private void getDifferenceDate(Date startDate, Date endDate, QueryDocumentSnapshot doc) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        new Thread(() -> {
            if (Common.hasActiveInternetConnection(getContext())) {
                Log.d("sss", "لا يوجد اتصال بالإنترنت ..");
            } else {
                Log.d("sss", "متصل بالإنترنت ..");

                int status = Integer.parseInt(Objects.requireNonNull(doc.get("statusAcceptance")).toString());
                if (status == 2) {
                    Log.d("sss", "" + status);
                    if (doc.getBoolean("isSeenExam.isSeenMohafez")) {
                        if ((int) elapsedDays >= 1) {
                            doc.getReference().update("statusAcceptance", -3);
                        }
                    } else {
                        if ((int) elapsedDays >= 1) {
                            doc.getReference().update("statusAcceptance", -3);
                        }
                    }
                } else if (status == -1) {
                    Log.d("sss", "" + status);
                    if (doc.getBoolean("isSeenExam.isSeenMohafez")) {
                        if ((int) elapsedHours >= 10) {
                            doc.getReference().delete();
                        }
                    } else {
                        if ((int) elapsedDays >= 1) {
                            doc.getReference().delete();
                        }
                    }
                } else if (status == -2) {
                    Log.d("sss", "" + status);
                    if (doc.getBoolean("isSeenExam.isSeenMohafez")) {
                        if ((int) elapsedHours >= 10) {
                            doc.getReference().delete();
                        }
                    } else {
                        if ((int) elapsedDays >= 1) {
                            doc.getReference().delete();
                        }
                    }
                } else if (status == -3) {
                    Log.d("sss", "" + status);
                    if (doc.getBoolean("isSeenExam.isSeenMohafez")) {
                        if ((int) elapsedHours >= 10) {
                            doc.getReference().delete();
                        }
                    } else {

                        if ((int) elapsedDays >= 1) {
                            doc.getReference().delete();
                        }
                    }
                }
            }
        }).start();

        Log.d("sss", elapsedDays + " " + elapsedHours + " " + elapsedMinutes + " " + elapsedSeconds);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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
}