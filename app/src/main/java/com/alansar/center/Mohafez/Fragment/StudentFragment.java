package com.alansar.center.Mohafez.Fragment;


import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Activitys.HostingActivity;
import com.alansar.center.Common.Common;
import com.alansar.center.Mohafez.Activitys.AddDailyMemorizationActivity;
import com.alansar.center.Mohafez.Activitys.ViewMonthlyReportsActivity;
import com.alansar.center.Mohafez.Adapter.StudentAdapter;
import com.alansar.center.Notifications.APIService;
import com.alansar.center.Notifications.Client;
import com.alansar.center.Notifications.Data;
import com.alansar.center.Notifications.Response;
import com.alansar.center.Notifications.Sender;
import com.alansar.center.R;
import com.alansar.center.SweetAlertDialog_;
import com.alansar.center.students.Model.Student;
import com.alansar.center.supervisor_exams.Model.Exam;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;


public class StudentFragment extends Fragment {
    private FirebaseFirestore db;
    private List<Student> students;
    private StudentAdapter adapter;
    private String groupId;
    private AlertDialog alertDialog;
    private Spinner sp_Parts;
    private View view;
    private RecyclerView rv;
    private ListenerRegistration registration;
    private APIService apiService;
    private String idMoshref;
    private String[] Values;
    private TextView tv_name_student, tv_part_exam,
            tv_date_exam, tv_name_mohafez,
            tv_mark_exam, tv_notes_exam,
            tv_name_tester;
    private LinearLayout li_question_exam_ed;

    public StudentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_mohafez_student, container, false);
        if (getActivity() != null && getContext() != null) {
            db = FirebaseFirestore.getInstance();

            rv = view.findViewById(R.id.student_rv);
            students = new ArrayList<>();
            FloatingActionButton btnaddMohafez = view.findViewById(R.id.btn_add_student);
            btnaddMohafez.setOnClickListener(view1 -> addStudent());
            adapter = new StudentAdapter(students);
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            rv.setHasFixedSize(true);
            setHasOptionsMenu(true);
            rv.setAdapter(adapter);
            getIdMoshref();
            Values = getResources().getStringArray(
                    R.array.parts_quran);
            apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);
        }
        return view;
    }

    private void LoadData() {
        if (Common.currentPerson != null && Common.currentSTAGE != null && Common.currentGroupId != null) {
            registration = db.collection("Student")
                    .orderBy("name", Query.Direction.ASCENDING)
                    .whereEqualTo("stage", Common.currentSTAGE)
                    .whereEqualTo("groupId", Common.currentGroupId)
                    .limit(20)
                    .addSnapshotListener((queryDocumentSnapshots, e) -> {
                        if (e != null) {
                            Log.w("sss", "listen:error" + e.getLocalizedMessage());
                            return;
                        }
                        if (queryDocumentSnapshots != null) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                students.clear();
                                for (int j = 0; j < queryDocumentSnapshots.size(); j++) {
                                    students.add(queryDocumentSnapshots.getDocuments().get(j).toObject(Student.class));
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            if (students.isEmpty()) {
                                view.findViewById(R.id.tv_check_students).setVisibility(View.VISIBLE);
                                rv.setVisibility(View.GONE);
                            } else {
                                view.findViewById(R.id.tv_check_students).setVisibility(View.GONE);
                                rv.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.ADD_DAILY_RECITATIONS)) {
            startActivity(new Intent(getContext(), AddDailyMemorizationActivity.class)
                    .putExtra("UID", StudentAdapter.students.get(item.getOrder()).getId()));
        } else if (item.getTitle().equals(Common.VIEW_THE_LATEST_MONTHLY_REPORT)) {
            startActivity(new Intent(getContext(), ViewMonthlyReportsActivity.class)
                    .putExtra("UID", StudentAdapter.students.get(item.getOrder()).getId()));
        } else if (item.getTitle().equals(Common.VIEW_THE_LATEST_EXAM)) {
            ViewLatestExamOfStudent(item.getOrder());
        } else if (item.getTitle().equals(Common.REQUEST_A_TEST)) {
            queryLastExamFromDB(item.getOrder());
        } else if (item.getTitle().equals(Common.ISDISABLEACCOUNT)) {
            updateIsEnabledAccount(students.get(item.getOrder()).getId(), false);
        } else if (item.getTitle().equals(Common.ISENABLEACCOUNT)) {
            updateIsEnabledAccount(students.get(item.getOrder()).getId(), true);
        }
        return super.onContextItemSelected(item);
    }

    private void ViewLatestExamOfStudent(int order) {
        Calendar calendar = Calendar.getInstance();
        db.collection("Exam")
                .whereEqualTo("idStudent", students.get(order).getId())
                .whereEqualTo("idMohafez", Common.currentPerson.getId())
                .whereEqualTo("statusAcceptance", 3)
                .whereEqualTo("year", calendar.get(Calendar.YEAR))
                .orderBy("month", Query.Direction.DESCENDING)
                .orderBy("day", Query.Direction.DESCENDING)
                .limit(1)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                showDialogMoreDetails(queryDocumentSnapshots.getDocuments().get(0).toObject(Exam.class));
            } else {
                new SweetAlertDialog_(getContext()).showDialogError("عذرا لم يتم العثور على أية إختبارات لهذا الطالب");
            }
        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
    }


    @SuppressLint("SetTextI18n")
    private void showDialogMoreDetails(Exam exam) {
        if (getContext() != null && exam != null) {
            LayoutInflater factory = LayoutInflater.from(getActivity());
            @SuppressLint("InflateParams") final View moreDetailsDialogView
                    = factory.inflate(R.layout.more_details_exam_dialog_custom_super_visor, null);
            AlertDialog dialogMoreDetails = new AlertDialog.Builder(getContext())
                    .setView(moreDetailsDialogView).create();
            dialogMoreDetails.show();
            Objects.requireNonNull(dialogMoreDetails.getWindow()).setBackgroundDrawableResource(R.color.fbutton_color_transparent);
            InitializationDialog(dialogMoreDetails);

            tv_name_student.setText(exam.getNotes());
            tv_part_exam.setText(exam.getExamPart());
            getNameTesterFromDB(exam.getIdTester(), tv_name_tester);
            tv_date_exam.setText(exam.getDay() + "/" + exam.getMonth() + "/" + exam.getYear());
            getNameStudentFromDB(exam.getIdStudent(), tv_name_student);
            getNameMohafezFromDB(exam.getIdMohafez(), tv_name_mohafez);
            tv_notes_exam.setText(exam.getNotes());
            calcMarksExam(exam.getMarksExamQuestions(), tv_mark_exam);
            if (exam.getMarksExamQuestions() != null) {
                for (int i = 1; i <= exam.getMarksExamQuestions().size(); i++) {
                    MaterialEditText ed_questions = new MaterialEditText(getContext());
                    LinearLayout.LayoutParams lp_ed = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);

                    ed_questions.setLayoutParams(lp_ed);
                    ed_questions.setMetTextColor(Color.BLACK);
                    ed_questions.setTextSize(20);
                    ed_questions.setTextColor(Color.BLACK);
                    ed_questions.setFocusable(false);
                    ed_questions.setHelperText("س :" + i);
                    ed_questions.setHelperTextAlwaysShown(true);
                    ed_questions.setHelperTextColor(Color.BLACK);
                    ed_questions.setText("" + exam.getMarksExamQuestions().get("" + i));
                    li_question_exam_ed.addView(ed_questions);
                }
            }
        }
    }

    private void InitializationDialog(AlertDialog dialogMoreDetails) {
        tv_notes_exam = dialogMoreDetails.findViewById(R.id.tv_notes_more_details_exam);
        tv_name_mohafez = dialogMoreDetails.findViewById(R.id.tv_name_mohafez);
        tv_name_student = dialogMoreDetails.findViewById(R.id.tv_name_student);
        tv_part_exam = dialogMoreDetails.findViewById(R.id.tv_part_exam);
        tv_date_exam = dialogMoreDetails.findViewById(R.id.tv_date_exam);
        tv_mark_exam = dialogMoreDetails.findViewById(R.id.tv_mark_exam);
        tv_name_tester = dialogMoreDetails.findViewById(R.id.tv_name_tester);
        li_question_exam_ed = dialogMoreDetails.findViewById(R.id.linear_layout_questions_exam_ed);
    }

    private void getNameTesterFromDB(String idTester, TextView tv_name_tester) {
        if (idTester != null) {
            db.collection("Tester").document(idTester)
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    if (tv_name_tester != null) {
                        tv_name_tester.setText(documentSnapshot.getString("name"));
                    }
                }
            }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
        }
    }

    private void getNameStudentFromDB(String id, TextView et_name) {
        if (id != null && !id.isEmpty()) {
            db.collection("Student").document(id)
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    if (et_name != null) {
                        et_name.setText(documentSnapshot.getString("name"));
                    }
                }
            }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
        }
    }

    private void getNameMohafezFromDB(String id, TextView et_name) {
        if (id != null && !id.isEmpty()) {
            db.collection("Mohafez").document(id)
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    if (et_name != null) {
                        et_name.setText(documentSnapshot.getString("name"));
                    }
                }
            }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void queryLastExamFromDB(int order) {
        Calendar calendar = Calendar.getInstance();
        db.collection("Exam")
                .whereEqualTo("idStudent", students.get(order).getId())
                .whereEqualTo("idMohafez", Common.currentPerson.getId())
                .whereEqualTo("year", calendar.get(Calendar.YEAR))
                .orderBy("month", Query.Direction.ASCENDING)
                .orderBy("day", Query.Direction.ASCENDING)
                .limitToLast(1)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null) {
                SimpleDateFormat dateformat = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    dateformat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa", Locale.getDefault());
                }
                if (!queryDocumentSnapshots.isEmpty()) {
                    Exam exam = queryDocumentSnapshots.getDocuments().get(0).toObject(Exam.class);
                    if (exam != null) {
                        double result = calcMarksExam(exam.getMarksExamQuestions());
                        if (result != 0) {
                            if (result < 80.00) {
                                if (dateformat != null) {
                                    String date = dateformat.format(Timestamp.now().toDate().getTime());
                                    GregorianCalendar gregorianCalendar = new GregorianCalendar();
                                    gregorianCalendar.set(exam.getYear(), exam.getMonth(), exam.getDay(), 6, 0, 0);
                                    String dateRet = dateformat.format(gregorianCalendar.getTime());
                                    try {
                                        getDifferenceDate(dateformat.parse(dateRet), dateformat.parse(date), order);
                                    } catch (ParseException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            } else {
                                showDialogRequestAtest(order);
                            }
                        } else {
                            showDialogRequestAtest(order);
                        }
                    }
                } else {
                    showDialogRequestAtest(order);
                }
            }
        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));

    }

    private void checkLastExamIsNotOrdering(int order) {
        Calendar calendar = Calendar.getInstance();
        db.collection("Exam")
                .whereEqualTo("idStudent", students.get(order).getId())
                .whereEqualTo("idMohafez", Common.currentPerson.getId())
                .whereEqualTo("year", calendar.get(Calendar.YEAR))
                .orderBy("month", Query.Direction.ASCENDING)
                .orderBy("day", Query.Direction.ASCENDING)
                .limitToLast(1)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                int statusAcceptance = queryDocumentSnapshots.getDocuments().get(0)
                        .get("statusAcceptance", Integer.TYPE);
                String partExam = queryDocumentSnapshots.getDocuments().get(0).getString("examPart");
                if (statusAcceptance >= 0 && statusAcceptance < 3) {
                    if (sp_Parts != null && sp_Parts.getSelectedItem().toString().equals(partExam)) {
                        new SweetAlertDialog_(getContext())
                                .showDialogError("عذرا لا يمكن تقديم الطلب أكثر من مرة");
                    } else {
                        addReqestTest(order);
                    }
                } else {
                    addReqestTest(order);
                }
            } else {
                addReqestTest(order);
            }
        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
    }

    @SuppressLint("SetTextI18n")
    private void calcMarksExam(HashMap<String, Double> map, TextView tv_mark_exam) {
        if (map != null) {
            double mark = 0;
            if (!map.isEmpty()) {
                for (int i = 1; i <= map.size(); i++) {
                    if (map.get("" + i) != null) {
                        mark += map.get("" + i);
                    }
                }
            }
            double result = round(mark / map.size());
            if (result >= 80.00) {
                tv_mark_exam.setText("" + result);
                tv_mark_exam.setTextColor(Color.WHITE);
                tv_mark_exam.setBackgroundColor(Color.GREEN);

            } else if (result < 80.00) {
                tv_mark_exam.setText("" + result);
                tv_mark_exam.setTextColor(Color.WHITE);
                tv_mark_exam.setBackgroundColor(Color.RED);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private double calcMarksExam(HashMap<String, Double> map) {
        if (map != null) {
            double mark = 0;
            for (int i = 1; i <= map.size(); i++) {
                if (map.get("" + i) != null) {
                    mark += map.get("" + i);
                }
            }
            return round(mark / map.size());
        } else {
            return 0;
        }
    }

    private double round(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private void showDialogRequestAtest(int order) {
        if (getContext() != null && getActivity() != null) {
            List<String> enters = new ArrayList<>();
            enters.add("اختر الجزء");
            Calendar calendar = Calendar.getInstance();
            LayoutInflater factory = LayoutInflater.from(getActivity());
            @SuppressLint("InflateParams") final View addExamDialogView = factory.inflate(R.layout.add_exam_dialog, null);
            alertDialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity())).create();
            alertDialog.setView(addExamDialogView);
            alertDialog.show();
            sp_Parts = alertDialog.findViewById(R.id.add_exam_spinner_parts);
            EditText et_name = alertDialog.findViewById(R.id.add_exam_et_name);
            Button btn_add = alertDialog.findViewById(R.id.add_exam_add_btn_save);
            assert btn_add != null;
            btn_add.setOnClickListener(view -> {
                if (validateInputsRequrstAtest()) {
                    checkLastExamIsNotOrdering(order);
                    alertDialog.dismiss();
                }
            });
            db.collection("Exam")
                    .whereEqualTo("idStudent", students.get(order).getId())
                    .whereEqualTo("idMohafez", Common.currentPerson.getId())
                    .whereEqualTo("statusAcceptance", 3)
                    .whereEqualTo("year", calendar.get(Calendar.YEAR))
                    .orderBy("month", Query.Direction.DESCENDING)
                    .orderBy("day", Query.Direction.DESCENDING)
                    .limit(1)
                    .get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    double result = calcMarksExam(Objects.requireNonNull(queryDocumentSnapshots.getDocuments()
                            .get(0).toObject(Exam.class)).getMarksExamQuestions());
                    String examPart = Objects.requireNonNull(queryDocumentSnapshots.getDocuments().get(0).toObject(Exam.class)).getExamPart();
                    if (result >= 80.00) {
                        for (int j = 0; j < Values.length; j++) {
                            if (examPart.equals(Values[j])) {
                                enters.add(Values[j - 1]);
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                getContext(), android.R.layout.simple_spinner_item, enters);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp_Parts.setAdapter(adapter);

                    } else if (result < 80.00) {
                        for (String value : Values) {
                            if (examPart.equals(value)) {
                                enters.add(value);
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                getContext(), android.R.layout.simple_spinner_item, enters);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp_Parts.setAdapter(adapter);
                    } else {
                        Toast.makeText(getContext(), "this", Toast.LENGTH_SHORT).show();
                    }
                }

            }).addOnFailureListener(e -> Log.d("sss", "listen:error" + e.getLocalizedMessage()));
            ImageButton imbtn_close = alertDialog.findViewById(R.id.add_exam_imgbtn_close);
            assert imbtn_close != null;
            imbtn_close.setOnClickListener(view121 -> alertDialog.dismiss());
            if (et_name != null) {
                et_name.setText(students.get(order).getName());
            }
        }
    }

    private void addReqestTest(int order) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        boolean notify = true;
        String PushId = db.collection("Exam").document().getId();
        db.collection("Exam").document(PushId).set(new Exam(PushId, students.get(order).getId()
                , Common.currentPerson.getId(), sp_Parts.getSelectedItem().toString(), 0, "" + Common.currentSTAGE, day, month + 1, year));

        new SweetAlertDialog_(getContext()).showDialogSuccess("OK", "تم طلب الإختبار بنجاح !")
                .setConfirmButton("OK", SweetAlertDialog::dismissWithAnimation);
        if (notify) {
            sendNotifications(sp_Parts.getSelectedItem().toString(), students.get(order).getName());
            notify = false;
        }
    }

    private boolean validateInputsRequrstAtest() {
        if (sp_Parts.getSelectedItemPosition() != 0) {
            return true;
        } else {
            new SweetAlertDialog_(getContext()).showDialogError("يجب اختيار الجزء !");
            return false;
        }
    }

    private void sendNotifications(String examPart, String NameStudent) {
        String NameMohafez = Common.currentPerson.getFname() + " " + Common.currentPerson.getMname() + " " + Common.currentPerson.getLname();
        if (idMoshref != null && !idMoshref.isEmpty()) {
            db.collection("Token").document(idMoshref)
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Data data = new Data(Common.currentPerson.getId(), "لقد قام المحفظ "
                            + NameMohafez +
                            " بطلب إختبار : " + examPart + " للطالب : " + NameStudent, "إجراء طلب إختبار", "" + idMoshref,
                            "MoshrefActivity", "Orders_Exams_Fragment");
                    Sender sender = new Sender(data, documentSnapshot.getString("token"));
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
            }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
        }
    }

    private void getIdMoshref() {
        db.collection("Moshref")
                .whereEqualTo("stage", Common.currentSTAGE)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                idMoshref = queryDocumentSnapshots.getDocuments().get(0).getId();
            }
        });
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
                //adapter.getFilter().filter(query);
                SearchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // adapter.getFilter().filter(newText);
                SearchData(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);

    }

    private void SearchData(String newText) {
        AtomicBoolean isFound = new AtomicBoolean(false);
        CollectionReference Ref = db.collection("Student");
        if (groupId != null && Common.currentSTAGE != null) {
            Query query = Ref.whereEqualTo("stage", Common.currentSTAGE).whereEqualTo("groupId", groupId);
            if (newText.isEmpty()) {
                students.clear();
                LoadData();
                adapter.notifyDataSetChanged();
            } else {
                students.clear();
                query = query.orderBy("name").startAt(newText).endAt(newText + "\uf8ff");
                query.addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.w("sss", "listen:error" + e.getLocalizedMessage());
                        return;
                    }
                    if (queryDocumentSnapshots != null) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            isFound.set(true);
                            students.clear();
                            for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots) {
                                students.add(snapshots.toObject(Student.class));
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getContext(), "لم يتم العثور على طلبك !", Toast.LENGTH_SHORT).show();
                            students.clear();
                            adapter.notifyDataSetChanged();
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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Common.currentPerson != null) {
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

    private void addStudent() {
        startActivity(new Intent(getActivity(), HostingActivity.class)
                .putExtra("fragmentType", "Personal_Information__Fragment")
                .putExtra("permissions", Common.PERMISSIONS_STUDENTN));
    }

    private void getDifferenceDate(Date startDate, Date endDate, int order) {
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

        db.collection("ExamsSettings")
                .get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.isEmpty()) {
                int numberOrdersExamDay = Integer.parseInt(Objects.requireNonNull(documentSnapshot.getDocuments()
                        .get(0).get("numberOrdersExamDay")).toString());
                if (elapsedDays <= numberOrdersExamDay) {
                    int differentDays = (int) (numberOrdersExamDay - elapsedDays);
                    String message;
                    if (differentDays == 1 || differentDays == 0) {
                        message = "عذرا متبقي يوم واحد لإنتهاء المدة المحددة حتى تتمكن من حجز الطلب";
                    } else if (differentDays == 2) {
                        message = "عذرا متبقي يومان لإنتهاء المدة المحددة حتى تتمكن من حجز الطلب";
                    } else {
                        message = "عذرا متبقي " + differentDays + " يوم لإنتهاء المدة المحددة حتى تتمكن من حجز الطلب";
                    }
                    new SweetAlertDialog_(getContext())
                            .showDialogError(message);
                } else {
                    showDialogRequestAtest(order);
                }
            }

        });

        Log.d("sss", elapsedDays + " " + elapsedHours + " " + elapsedMinutes + " " + elapsedSeconds);
    }
}
