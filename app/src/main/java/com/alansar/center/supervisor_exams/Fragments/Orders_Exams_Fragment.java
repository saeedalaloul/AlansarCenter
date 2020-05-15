package com.alansar.center.supervisor_exams.Fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Common.Common;
import com.alansar.center.Notifications.APIService;
import com.alansar.center.Notifications.Client;
import com.alansar.center.Notifications.Data;
import com.alansar.center.Notifications.Response;
import com.alansar.center.Notifications.Sender;
import com.alansar.center.R;
import com.alansar.center.SweetAlertDialog_;
import com.alansar.center.supervisor_exams.Adapter.OrdersExamsAdapter;
import com.alansar.center.supervisor_exams.Model.Exam;
import com.alansar.center.testers.Model.Tester;
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
    private AlertDialog dialogRequrstTest;
    private Spinner sp_testers;
    private ArrayList<Tester> testers;
    private ArrayAdapter<Tester> adapterGroups;
    private DatePickerDialog.OnDateSetListener setListener;
    private int year;
    private int month;
    private int day;
    private SweetAlertDialog_ sweetAlertDialog;
    private View view;
    private RecyclerView recyclerView;
    private ListenerRegistration registration;
    private APIService apiService;
    private boolean notify = false;

    public Orders_Exams_Fragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_supervisor_exams_orders__exams, container, false);
        recyclerView = view.findViewById(R.id.exams_orders_rv);
        db = FirebaseFirestore.getInstance();
        sweetAlertDialog = new SweetAlertDialog_(getContext());
        exams = new ArrayList<>();
        testers = new ArrayList<>();
        adapter = new OrdersExamsAdapter(exams);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        setHasOptionsMenu(true);
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);
        return view;
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
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(-2);
        list.add(-3);
        registration = db.collection("Exam")
                .whereIn("statusAcceptance", list)
                .orderBy("year", Query.Direction.DESCENDING)
                .orderBy("month", Query.Direction.DESCENDING)
                .orderBy("day", Query.Direction.DESCENDING)
                .limit(20)
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
                                                GregorianCalendar gregorianCalendar = new GregorianCalendar();
                                                try {
                                                    gregorianCalendar.set(exam.getYear(), exam.getMonth(), exam.getDay(), 6, 0, 0);
                                                    String dateRet = dateformat.format(gregorianCalendar.getTime());
                                                    getDifferenceDate(dateformat.parse(dateRet), dateformat.parse(date), doc);
                                                } catch (ParseException ex) {
                                                    ex.printStackTrace();
                                                }
                                            }
                                            if (!doc.getBoolean("isSeenExam.isSeenMoshrefExam")) {
                                                doc.getReference().update("isSeenExam.isSeenMoshrefExam", true);
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
                                            if (!doc.getBoolean("isSeenExam.isSeenMoshrefExam")) {
                                                doc.getReference().update("isSeenExam.isSeenMoshrefExam", true);
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
                    }
                    if (exams.isEmpty()) {
                        view.findViewById(R.id.tv_check_exams).setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        view.findViewById(R.id.tv_check_exams).setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.PLACE_AN_ORDER)) {
            showDialogPalceOrder(item.getOrder());
        }
        return super.onContextItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    private void showDialogPalceOrder(int order) {
        final SweetAlertDialog BtnDialog = new SweetAlertDialog(Objects.requireNonNull(getActivity()), SweetAlertDialog.NORMAL_TYPE)
                .setContentText("هل أنت متأكد من ذلك ؟")
                .setConfirmText("قبول الطلب")
                .setCancelText("رفض الطلب")
                .setTitleText("إجراء الطلب");


        BtnDialog.setOnShowListener(dialog -> {
            BtnDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setOnClickListener(view -> {
                if (exams.get(order).getStatusAcceptance() == -3) {
                    new SweetAlertDialog_(getContext()).showDialogError("لم تكتمل العملية المطلوبة بنجاح لأن الطالب لم يجري الإختبار !");
                } else {

                    if (getContext() != null) {
                        LayoutInflater factory = LayoutInflater.from(getActivity());
                        @SuppressLint("InflateParams") final View assignTesterDialogView
                                = factory.inflate(R.layout.assign_tester_for_student_dialog, null);
                        dialogRequrstTest = new AlertDialog.Builder(getContext())
                                .setView(assignTesterDialogView).create();
                        dialogRequrstTest.show();

                        sp_testers = dialogRequrstTest.findViewById(R.id.assign_tester_spinner_testers);
                        EditText et_name = dialogRequrstTest.findViewById(R.id.assign_tester_et_name);
                        EditText et_date = dialogRequrstTest.findViewById(R.id.assign_tester_et_date);
                        Button btn_add = dialogRequrstTest.findViewById(R.id.assign_tester_add_btn_save);
                        assert btn_add != null;
                        btn_add.setOnClickListener(view2 -> {
                            if (validateInputs(et_date)) {
                                addTesterFromDB(order, dialog);
                            }
                        });
                        ImageButton imbtn_close = dialogRequrstTest.findViewById(R.id.assign_tester_imgbtn_close);
                        assert imbtn_close != null;
                        imbtn_close.setOnClickListener(view121 -> dialogRequrstTest.dismiss());
                        getNameStudentFromDB(order, et_name);
                        getTestersfromDatebase(order);
                        if (et_date != null) {
                            showDialogPickerDate(et_date);
                        }
                        if (et_date != null) {
                            Exam exam = exams.get(order);
                            if (exam.getMonth() != 0 && exam.getDay() != 0 && exam.getYear() != 0) {
                                day = exam.getDay();
                                month = exam.getMonth();
                                year = exam.getYear();
                                et_date.setText(exam.getDay() + "/" +
                                        exam.getMonth() + "/" +
                                        exam.getYear());
                            }
                        }
                    }
                }
            });

            BtnDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setOnClickListener(view -> {
                if (exams.get(order).getStatusAcceptance() == -3) {
                    new SweetAlertDialog_(getContext()).showDialogError("لم تكتمل العملية المطلوبة بنجاح لأن الطالب لم يجري الإختبار !");
                } else {
                    showDialogRequestNotes(order, BtnDialog);
                }
            });
        });

        BtnDialog.show();
    }

    @SuppressLint("NewApi")
    private void addTesterFromDB(int order, DialogInterface dialog) {
        if (testers != null && !testers.isEmpty() && sp_testers.getSelectedItemPosition() > 0) {
            SimpleDateFormat dateformat = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dateformat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa", Locale.getDefault());
            }
            if (dateformat != null) {
                notify = true;
                HashMap<String, Object> map = new HashMap<>();
                map.put("statusAcceptance", 2);
                map.put("notes", "");
                map.put("day", day);
                map.put("month", month);
                map.put("year", year);
                map.put("isSeenExam.isSeenMohafez", false);
                map.put("isSeenExam.isSeenMoshref", false);
                map.put("isSeenExam.isSeenTester", false);
                map.put("isSeenExam.isSeenEdare", false);
                map.put("idTester", testers.get(sp_testers.getSelectedItemPosition()).getUID());
                db.collection("Exam").document(exams.get(order).getId()).update(map);
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(Objects.requireNonNull(getContext()), SweetAlertDialog.SUCCESS_TYPE)
                        .setContentText("تمت");
                dialog.dismiss();
                sweetAlertDialog.show();
                dialogRequrstTest.dismiss();
                if (notify) {
                    sendNotifications(exams.get(order).getIdMohafez(),
                            exams.get(order).getIdStudent(),
                            testers.get(sp_testers.getSelectedItemPosition()).getUID(), 2,
                            exams.get(order).getExamPart());
                    notify = false;
                }
            }
        } else {
            sweetAlertDialog.showDialogError("يجب إضافة مختبرين !");
        }
    }

    private boolean validateInputs(EditText ed_Date) {
        if (day != 0 && month != 0 && year != 0
                && ed_Date != null && !ed_Date.getText().toString().trim().isEmpty()
                && sp_testers.getSelectedItemPosition() != 0) {
            return true;
        } else {
            if (day == 0 || month == 0 || year == 0 || ed_Date == null || ed_Date.getText().toString().trim().isEmpty()) {
                sweetAlertDialog.showDialogError("يجب اختيار تاريخ الإختبار !");
            } else if (sp_testers.getSelectedItemPosition() == 0) {
                sweetAlertDialog.showDialogError("يجب تعيين مختبر للإختبار !");
            }
        }
        return false;
    }

    private void getNameStudentFromDB(int order, EditText et_name) {
        if (exams.get(order) != null && exams.get(order).getIdStudent() != null) {
            db.collection("Student").document(exams.get(order).getIdStudent())
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    if (et_name != null) {
                        et_name.setText(documentSnapshot.getString("name"));
                    }
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void showDialogPickerDate(EditText ed_Date) {
        Calendar calendar = Calendar.getInstance();
        int retyear = calendar.get(Calendar.YEAR);
        int retmonth = calendar.get(Calendar.MONTH);
        int retday = calendar.get(Calendar.DAY_OF_MONTH);
        ed_Date.setOnClickListener(view1 -> {
            DatePickerDialog pickerDialog = new DatePickerDialog(Objects.requireNonNull(getActivity()),
                    setListener, retyear, retmonth, retday);
            pickerDialog.show();
        });

        setListener = (datePicker, year1, month1, day1) -> {
            month1 = month1 + 1;
            month = month1;
            day = day1;
            year = year1;
            String date = day + "/" + month + "/" + year;
            ed_Date.setText(date);
        };
    }

    private void showDialogRequestNotes(int order, SweetAlertDialog btnDialog) {
        if (getContext() != null) {
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
                    notify = true;
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("statusAcceptance", -2);
                    map.put("notes", Notes);
                    map.put("idTester", "");
                    map.put("day", day);
                    map.put("month", month);
                    map.put("year", year);
                    map.put("isSeenExam.isSeenMohafez", false);
                    map.put("isSeenExam.isSeenMoshref", false);
                    map.put("isSeenExam.isSeenTester", false);
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
                                exams.get(order).getIdStudent(),
                                exams.get(order).getIdTester(), -2,
                                exams.get(order).getExamPart());
                        notify = false;
                    }
                }
            }
        }
    }

    private void getTestersfromDatebase(int order) {
        db.collection("Tester").get().addOnSuccessListener(queryDocumentSnapshots -> {
            testers.clear();
            testers.add(new Tester("", "اختر المختبر", ""));
            for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                testers.add(queryDocumentSnapshots.toObjects(Tester.class).get(i));
                adapterGroups = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                        android.R.layout.simple_spinner_item, testers);
                adapterGroups.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_testers.setAdapter(adapterGroups);
            }
            for (int i = 0; i < testers.size(); i++) {
                if (testers.get(i).getUID().equals(exams.get(order).getIdTester())) {
                    sp_testers.setSelection(i);
                }
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
                //adapter.getFilter().filter(query);
                // SearchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // adapter.getFilter().filter(newText);
                //SearchData(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);

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
                        if ((int) elapsedDays >= 2) {
                            doc.getReference().update("statusAcceptance", -3);
                        }
                    }
                } else if (status == -3) {
                    Log.d("sss", "" + status);
                    if (doc.getBoolean("isSeenExam.isSeenMohafez")) {
                        if ((int) elapsedHours >= 10) {
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

    private void sendNotifications(String idMohafez, String idStudent, String idTester, int status, String examPart) {
        db.collection("Student").document(idStudent)
                .get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String NameStudent = documentSnapshot.getString("name");
                db.collection("Token").document(idMohafez)
                        .get().addOnSuccessListener(documentSnapshot1 -> {
                    if (documentSnapshot1.exists()) {
                        Data data = null;
                        if (NameStudent != null) {
                            if (status == 2) {
                                data = new Data(Common.currentPerson.getId(), "لقد قام مشرف الإختبارات بحجز طلب إختبار "
                                        + examPart +
                                        " للطالب : " + NameStudent + " يرجى مراجعة الموعد المحدد ...", "إجراء طلب إختبار",
                                        "" + idMohafez, "MohafezActivity", "Orders_Exams_Fragment");
                            } else if (status == -2) {
                                data = new Data(Common.currentPerson.getId(), "لقد قام مشرف الإختبارات برفض حجز طلب إختبار "
                                        + examPart +
                                        " للطالب : " + NameStudent, "إجراء طلب إختبار",
                                        "" + idMohafez, "MohafezActivity", "Orders_Exams_Fragment");
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

                if (documentSnapshot.getString("name") != null && idTester != null && !idTester.isEmpty()) {
                    if (status == 2) {
                        db.collection("Token").document(idTester)
                                .get().addOnSuccessListener(documentSnapshot1 -> {
                            if (documentSnapshot1.exists()) {
                                Data data1 = new Data(Common.currentPerson.getId(), "لقد قام مشرف الإختبارات بتعيينك مختبر طلب إختبار "
                                        + examPart +
                                        " للطالب : " + documentSnapshot.getString("name")
                                        + " يرجى مراجعة الموعد المحدد للطلب ...", "إجراء طلب إختبار", "" +
                                        idTester, "TesterActivity", "Orders_Exams_Fragment");

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
                    Toast.makeText(getContext(), "Name is not Found", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
