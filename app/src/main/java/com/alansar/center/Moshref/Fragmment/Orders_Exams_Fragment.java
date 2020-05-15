package com.alansar.center.Moshref.Fragmment;

import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Common.Common;
import com.alansar.center.Moshref.Adapter.OrdersExamsAdapter;
import com.alansar.center.Notifications.APIService;
import com.alansar.center.Notifications.Client;
import com.alansar.center.Notifications.Data;
import com.alansar.center.Notifications.Response;
import com.alansar.center.Notifications.Sender;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;

public class Orders_Exams_Fragment extends Fragment {
    private FirebaseFirestore db;
    private OrdersExamsAdapter adapter;
    private ArrayList<Exam> exams;
    private String Notes = "";
    private RecyclerView recyclerView;
    private View view;
    private ListenerRegistration registration;
    private APIService apiService;
    private boolean notify = false;
    private String IdMoshrefExams;

    public Orders_Exams_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_moshref_orders__exams_, container, false);
        recyclerView = view.findViewById(R.id.exams_orders_rv);
        db = FirebaseFirestore.getInstance();
        exams = new ArrayList<>();
        adapter = new OrdersExamsAdapter(exams);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        setHasOptionsMenu(true);
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);
        getIdMoshrefExams();
        return view;
    }

    private void getIdMoshrefExams() {
        db.collection("SuperVisorExams")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                IdMoshrefExams = queryDocumentSnapshots.getDocuments().get(0).getId();
            }
        });
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


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadData() {
        if (Common.currentSTAGE != null) {
            List<Integer> list = new ArrayList<>();
            list.add(0);
            list.add(1);
            list.add(2);
            list.add(-1);
            list.add(-2);
            list.add(-3);
            registration = db.collection("Exam")
                    .whereIn("statusAcceptance", list)
                    .whereEqualTo("stage", Common.currentSTAGE)
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
                                                if (!doc.getBoolean("isSeenExam.isSeenMoshref")) {
                                                    doc.getReference().update("isSeenExam.isSeenMoshref", true);
                                                }
                                                Exam exam = doc.toObject(Exam.class);
                                                if (dateformat != null) {
                                                    String date = dateformat.format(Timestamp.now().toDate().getTime());
                                                    GregorianCalendar gregorianCalendar = new GregorianCalendar();
                                                    try {
                                                        gregorianCalendar.set(exam.getYear(), exam.getMonth(), exam.getDay(), 6, 0, 0);
                                                        String dateRet = dateformat.format(gregorianCalendar.getTime());
                                                        getDifferenceDate(dateformat.parse(dateRet), dateformat.parse(date), doc);
                                                    } catch (ParseException ex) {
                                                        ex.printStackTrace();
                                                    }
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
                                                    GregorianCalendar gregorianCalendar = new GregorianCalendar();
                                                    try {
                                                        gregorianCalendar.set(exam.getYear(), exam.getMonth(), exam.getDay(), 6, 0, 0);
                                                        String dateRet = dateformat.format(gregorianCalendar.getTime());
                                                        getDifferenceDate(dateformat.parse(dateRet), dateformat.parse(date), doc);
                                                    } catch (ParseException ex) {
                                                        ex.printStackTrace();
                                                    }
                                                }
                                                if (!doc.getBoolean("isSeenExam.isSeenMoshref")) {
                                                    doc.getReference().update("isSeenExam.isSeenMoshref", true);
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
                                if (exams.isEmpty()) {
                                    view.findViewById(R.id.tv_check_exams).setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                } else {
                                    view.findViewById(R.id.tv_check_exams).setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.PLACE_AN_ORDER)) {
            showDialogPalceOrder(item.getOrder());
        }
        return super.onContextItemSelected(item);
    }

    private void showDialogPalceOrder(int order) {
        final SweetAlertDialog BtnDialog = new SweetAlertDialog(Objects.requireNonNull(getActivity()), SweetAlertDialog.NORMAL_TYPE)
                .setContentText("هل أنت متأكد من ذلك ؟")
                .setConfirmText("قبول الطلب")
                .setCancelText("رفض الطلب")
                .setTitleText("إجراء الطلب");


        BtnDialog.setOnShowListener(dialog -> {
            BtnDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setOnClickListener(view -> {
                if (exams.get(order).getStatusAcceptance() >= 2
                        || exams.get(order).getStatusAcceptance() == -2
                        || exams.get(order).getStatusAcceptance() == -3) {
                    new SweetAlertDialog_(getContext()).showDialogError("لم تكتمل العملية المطلوبة بنجاح بسبب إجراءات قبول أو رفض الطلب !");
                } else {
                    notify = true;
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("statusAcceptance", 1);
                    map.put("isSeenExam.isSeenMohafez", false);
                    map.put("isSeenExam.isSeenEdare", false);
                    map.put("notes", "");
                    db.collection("Exam").document(exams.get(order).getId()).update(map);
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(Objects.requireNonNull(getContext()), SweetAlertDialog.SUCCESS_TYPE)
                            .setContentText("تمت");
                    dialog.dismiss();
                    sweetAlertDialog.show();
                    if (notify) {
                        sendNotifications(exams.get(order).getIdMohafez(), exams.get(order).getIdStudent(), 1,
                                exams.get(order).getExamPart());
                        notify = false;
                    }
                }
            });

            BtnDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setOnClickListener(view -> {
                if (exams.get(order).getStatusAcceptance() >= 2
                        || exams.get(order).getStatusAcceptance() == -2
                        || exams.get(order).getStatusAcceptance() == -3) {
                    new SweetAlertDialog_(getContext()).showDialogError("لم تكتمل العملية المطلوبة بنجاح بسبب إجراءات قبول أو رفض الطلب !");
                } else {
                    showDialogRequestNotes(order, BtnDialog);
                }
            });
        });

        BtnDialog.show();
    }

    private void sendNotifications(String idMohafez, String idStudent, int status, String examPart) {
        db.collection("Student").document(idStudent)
                .get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String NameStudent = documentSnapshot.getString("name");
                db.collection("Token").document(idMohafez)
                        .get().addOnSuccessListener(documentSnapshot1 -> {
                    if (documentSnapshot1.exists()) {
                        Data data = null;
                        if (NameStudent != null) {
                            if (status == 1) {
                                data = new Data(Common.currentPerson.getId(), "لقد قام مشرف المرحلة بقبول طلب إختبار "
                                        + examPart +
                                        " للطالب : " + NameStudent, "إجراء طلب إختبار", "" + idMohafez, "MohafezActivity", "Orders_Exams_Fragment");
                            } else if (status == -1) {
                                data = new Data(Common.currentPerson.getId(), "لقد قام مشرف المرحلة برفض طلب إختبار "
                                        + examPart +
                                        " للطالب : " + NameStudent, "إجراء طلب إختبار", "" + idMohafez, "MohafezActivity", "Orders_Exams_Fragment");
                            }
                        }

                        if (data != null) {
                            Sender sender = new Sender(data, documentSnapshot1.getString("token"));
                            apiService.sendNotification(sender)
                                    .enqueue(new Callback<Response>() {
                                        @Override
                                        public void onResponse(@NonNull Call<Response> call, @NonNull retrofit2.Response<Response> response) {
                                            if (response.code() == 200) {
                                                assert response.body() != null;
                                                if (response.body().success != 1) {
                                                    new SweetAlertDialog_(getContext())
                                                            .showDialogError("حدث خطا ما في إرسال الإشعار!");
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<Response> call, @NonNull Throwable t) {
                                            Log.d("sss", "" + t.getLocalizedMessage());
                                        }
                                    });
                        }
                    }
                });

                if (documentSnapshot.getString("name") != null && IdMoshrefExams != null && !IdMoshrefExams.isEmpty()) {
                    if (status == 1) {
                        db.collection("Token").document(IdMoshrefExams)
                                .get().addOnSuccessListener(documentSnapshot1 -> {
                            if (documentSnapshot1.exists()) {
                                Data data1 = new Data(Common.currentPerson.getId(), "لقد قام مشرف المرحلة بقبول طلب إختبار "
                                        + examPart +
                                        " للطالب : " + documentSnapshot.getString("name")
                                        + " يرجى مراجعة الطلب ...", "إجراء طلب إختبار", "" + IdMoshrefExams, "SuperVisorExamsActivity", "Orders_Exams_Fragment");

                                Sender sender1 = new Sender(data1, documentSnapshot1.getString("token"));
                                apiService.sendNotification(sender1)
                                        .enqueue(new Callback<Response>() {
                                            @Override
                                            public void onResponse(@NonNull Call<Response> call, @NonNull retrofit2.Response<Response> response) {
                                                if (response.code() == 200) {
                                                    assert response.body() != null;
                                                    if (response.body().success != 1) {
                                                        new SweetAlertDialog_(getContext())
                                                                .showDialogError("حدث خطا ما في إرسال الإشعار!");
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<Response> call, @NonNull Throwable t) {
                                                Log.d("sss", "" + t.getLocalizedMessage());
                                            }
                                        });
                            }
                        });
                    }
                } else {
                    Log.d("sss", "Name Student Not Found");
                }
            }
        });

    }

    private void showDialogRequestNotes(int order, SweetAlertDialog btnDialog) {
        Calendar calendar = Calendar.getInstance();
        if (getContext() != null) {
            notify = true;
            SweetAlertDialog dialog = new SweetAlertDialog(getContext())
                    .setTitleText("أدخل الملاحظات ...");
            final EditText input = new EditText(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            dialog.setCustomView(input);
            dialog.setConfirmButton("تمت", sweetAlertDialog -> {
                Notes = input.getText().toString().trim();
                sweetAlertDialog.dismissWithAnimation();
            });
            if (Notes != null && Notes.trim().isEmpty()) {
                dialog.show();
            } else if (Notes != null && !Notes.trim().isEmpty()) {
                SimpleDateFormat dateformat = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    dateformat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa", Locale.getDefault());
                }
                if (dateformat != null) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("statusAcceptance", -1);
                    map.put("notes", Notes);
                    map.put("day", calendar.get(Calendar.DAY_OF_MONTH));
                    map.put("month", calendar.get(Calendar.MONTH) + 1);
                    map.put("year", calendar.get(Calendar.YEAR));
                    map.put("isSeenExam.isSeenMohafez", false);
                    map.put("isSeenExam.isSeenEdare", false);
                    db.collection("Exam").document(exams.get(order).getId()).update(map);
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(Objects.requireNonNull(getContext()), SweetAlertDialog.SUCCESS_TYPE)
                            .setContentText("تمت");
                    dialog.dismiss();
                    sweetAlertDialog.show();
                    Notes = "";
                    btnDialog.dismissWithAnimation();

                    if (notify) {
                        sendNotifications(exams.get(order).getIdMohafez(),
                                exams.get(order).getIdStudent(), -1,
                                exams.get(order).getExamPart());
                        notify = false;
                    }
                }
            }
        }
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
        int status = Integer.parseInt(Objects.requireNonNull(doc.get("statusAcceptance")).toString());

        new Thread(() -> {
            if (Common.hasActiveInternetConnection(getContext())) {
                Log.d("sss", "لا يوجد اتصال بالإنترنت ..");
            } else {
                Log.d("sss", "متصل بالإنترنت ..");

                if (status == 2) {
                    Log.d("sss", "" + status);
                    if (doc.getBoolean("isSeenExam.isSeenMohafez")) {
                        if ((int) elapsedDays >= 2) {
                            doc.getReference().update("statusAcceptance", -3);
                        }
                    } else {
                        if ((int) elapsedDays >= 2) {
                            doc.getReference().update("statusAcceptance", -3);
                        }
                    }
                } else if (status == -1) {
                    Log.d("sss", "" + status);
                    if (doc.getBoolean("isSeenExam.isSeenMohafez")) {
                        if ((int) elapsedHours >= 2) {
                            doc.getReference().delete();
                        }
                    } else {
                        if ((int) elapsedDays >= 2) {
                            doc.getReference().delete();
                        }
                    }
                } else if (status == -2) {
                    Log.d("sss", "" + status);
                    if (doc.getBoolean("isSeenExam.isSeenMohafez")) {
                        if ((int) elapsedHours >= 2) {
                            doc.getReference().delete();
                        }
                    } else {
                        if ((int) elapsedDays >= 2) {
                            doc.getReference().delete();
                        }
                    }
                } else if (status == -3) {
                    Log.d("sss", "" + status);
                    if (doc.getBoolean("isSeenExam.isSeenMohafez")) {
                        if ((int) elapsedHours >= 2) {
                            doc.getReference().delete();
                        }
                    } else {
                        if ((int) elapsedDays >= 2) {
                            doc.getReference().delete();
                        }
                    }
                }
            }
        }).start();
        Log.d("sss", elapsedDays + " " + elapsedHours + " " + elapsedMinutes + " " + elapsedSeconds);
    }

}