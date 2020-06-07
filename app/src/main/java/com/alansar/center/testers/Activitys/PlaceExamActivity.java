package com.alansar.center.testers.Activitys;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.alansar.center.Common.Common;
import com.alansar.center.FButton;
import com.alansar.center.Notifications.APIService;
import com.alansar.center.Notifications.Client;
import com.alansar.center.Notifications.Data;
import com.alansar.center.Notifications.Response;
import com.alansar.center.Notifications.Sender;
import com.alansar.center.R;
import com.alansar.center.SweetAlertDialog_;
import com.alansar.center.supervisor_exams.Model.Exam;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Callback;

public class PlaceExamActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private int questionsNumberExam;
    private String idExam, StudentName;
    private TextView tv_name_student, tv_part_exam, tv_date_exam, tv_name_mohafez, tv_mark_exam;
    private LinearLayout li_question_exam_ed, li_question_exam_tv;
    private FButton btn_discount_points_5, btn_discount_points_2, btn_points_clear;
    private View view1;
    private AlertDialog alertDialog;
    private HashMap<String, Double> marksExamQuestions;
    private HashMap<String, String> signsExamQuestions;
    private APIService apiService;
    private boolean notify = false;
    private String IdMoshrefExams, IdMohafez;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_exam);
        initialize();
        setSupportActionBar(findViewById(R.id.place_exam_toolbar));
        getIdMoshrefExams();
        if (getIntent() != null && getIntent().getStringExtra("questionsNumberExam") != null
                && getIntent().getStringExtra("idExam") != null
                && getIntent().getStringExtra("StudentName") != null) {
            questionsNumberExam = Integer.parseInt(0 + getIntent().getStringExtra("questionsNumberExam"));
            idExam = getIntent().getStringExtra("idExam");
            StudentName = getIntent().getStringExtra("StudentName");
        } else {
            finish();
        }

        for (int i = 1; i <= questionsNumberExam; i++) {
            MaterialEditText ed_questions = new MaterialEditText(this);
            MaterialEditText ed_mark_exam_question = new MaterialEditText(this);
            LinearLayout.LayoutParams lp_ed = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams lp_tv = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            ed_questions.setLayoutParams(lp_ed);
            ed_questions.setHint("س :" + i);
            ed_questions.setMetTextColor(Color.BLACK);
            ed_questions.setMetHintTextColor(Color.BLACK);
            ed_questions.setMaxCharacters(6);
            ed_questions.setTextSize(20);
            ed_questions.setTextColor(Color.BLACK);
            ed_questions.setTag("" + i);

            ed_mark_exam_question.setHint("س :" + i);
            ed_mark_exam_question.setTag("" + i);
            ed_mark_exam_question.setMetTextColor(Color.BLACK);
            ed_mark_exam_question.setMetHintTextColor(Color.BLACK);
            ed_mark_exam_question.setMaxCharacters(6);
            ed_mark_exam_question.setTextSize(20);
            ed_mark_exam_question.setLayoutParams(lp_tv);
            ed_mark_exam_question.setFocusable(false);
            ed_mark_exam_question.setTextColor(Color.BLACK);
            ed_mark_exam_question.setText("95");

            Common.disableSoftInputFromAppearing(ed_questions);

            li_question_exam_ed.addView(ed_questions);
            li_question_exam_tv.addView(ed_mark_exam_question);
            marksExamQuestions = new HashMap<>();
            signsExamQuestions = new HashMap<>();
            for (int j = 1; j <= questionsNumberExam; j++) {
                marksExamQuestions.put("" + j, 95.0);
                signsExamQuestions.put("" + j, "");
            }

            btn_discount_points_2.setOnClickListener(view -> {
                view1 = li_question_exam_ed.getFocusedChild();
                if (view1 instanceof MaterialEditText) {
                    MaterialEditText ed_question = (MaterialEditText) view1;
                    ed_question.append("-");
                    signsExamQuestions.replace("" + view1.getTag(), "" + Objects.requireNonNull(ed_question.getText()).toString());
                    MaterialEditText tv_mark = li_question_exam_tv.findViewWithTag(view1.getTag());
                    marksExamQuestions.replace("" + view1.getTag(), marksExamQuestions.get("" + view1.getTag()) - 2.5);
                    tv_mark.setText("" + marksExamQuestions.get("" + view1.getTag()));
                }
                calcMarkExam(marksExamQuestions);
            });
            btn_discount_points_5.setOnClickListener(view -> {
                view1 = li_question_exam_ed.getFocusedChild();
                if (view1 instanceof MaterialEditText) {
                    MaterialEditText ed_question = (MaterialEditText) view1;
                    ed_question.append("/");
                    signsExamQuestions.replace("" + view1.getTag(), "" + Objects.requireNonNull(ed_question.getText()).toString());
                    MaterialEditText tv_mark = li_question_exam_tv.findViewWithTag(view1.getTag());
                    marksExamQuestions.replace("" + view1.getTag(), marksExamQuestions.get("" + view1.getTag()) - 5);
                    tv_mark.setText("" + marksExamQuestions.get("" + view1.getTag()));
                }
                calcMarkExam(marksExamQuestions);
            });

            btn_points_clear.setOnClickListener(view2 -> {
                view1 = li_question_exam_ed.getFocusedChild();
                if (view1 instanceof MaterialEditText) {
                    MaterialEditText ed_question = (MaterialEditText) view1;
                    int length = Objects.requireNonNull(ed_question.getText()).length();
                    MaterialEditText tv_mark = li_question_exam_tv.findViewWithTag(view1.getTag());
                    if (length > 0) {
                        if (ed_question.getText().toString().charAt(length - 1) == '-') {
                            ed_question.getText().delete(length - 1, length);
                            Double m = 2.5 + marksExamQuestions.get("" + view1.getTag());
                            marksExamQuestions.replace("" + view1.getTag(), m);
                            signsExamQuestions.replace("" + view1.getTag(), "" + Objects.requireNonNull(ed_question.getText()).toString());
                            tv_mark.setText("" + marksExamQuestions.get("" + view1.getTag()));
                        } else if (ed_question.getText().toString().charAt(length - 1) == '/') {
                            ed_question.getText().delete(length - 1, length);
                            Double m = 5 + marksExamQuestions.get("" + view1.getTag());
                            signsExamQuestions.replace("" + view1.getTag(), "" + Objects.requireNonNull(ed_question.getText()).toString());
                            marksExamQuestions.replace("" + view1.getTag(), m);
                            tv_mark.setText("" + marksExamQuestions.get("" + view1.getTag()));
                        }
                    }
                    calcMarkExam(marksExamQuestions);
                }
            });
        }
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);
        getDeatailsExamFromDB(idExam);
    }

    private void initialize() {
        db = FirebaseFirestore.getInstance();
        tv_name_student = findViewById(R.id.tv_name_student);
        tv_date_exam = findViewById(R.id.tv_date_exam);
        tv_mark_exam = findViewById(R.id.tv_mark_exam);
        tv_name_mohafez = findViewById(R.id.tv_name_mohafez);
        tv_part_exam = findViewById(R.id.tv_part_exam);
        li_question_exam_ed = findViewById(R.id.linear_layout_questions_exam_ed);
        li_question_exam_tv = findViewById(R.id.linear_layout_questions_exam_tv);
        btn_discount_points_2 = findViewById(R.id.btn_points_discount_2);
        btn_discount_points_5 = findViewById(R.id.btn_points_discount_5);
        btn_points_clear = findViewById(R.id.btn_points_clear);
    }

    @SuppressLint("SetTextI18n")
    private void getDeatailsExamFromDB(String idExam) {
        db.collection("Exam")
                .document(idExam).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Log.d("sss", "" + documentSnapshot.getData());
                Exam exam = documentSnapshot.toObject(Exam.class);
                tv_name_student.setText(StudentName);
                assert exam != null;
                tv_part_exam.setText(exam.getExamPart());
                getNameOfMohafeaFromDB(exam.getIdMohafez());
                tv_date_exam.setText(exam.getDay() + "/" + exam.getMonth() + "/" + exam.getYear());
                IdMohafez = exam.getIdMohafez();
            }
        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
    }

    private void getNameOfMohafeaFromDB(String id) {
        db.collection("Mohafez").document(id)
                .get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                tv_name_mohafez.setText(documentSnapshot.getString("name"));
            }
        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
    }

    @SuppressLint({"SetTextI18n"})
    private void calcMarkExam(HashMap<String, Double> map) {
        Double d = 0.0;
        if (!map.isEmpty()) {
            for (int i1 = 0; i1 <= map.size(); i1++) {
                if (map.get("" + i1) != null) {
                    d += map.get("" + i1);
                }
            }
            tv_mark_exam.setText("" + round(d / map.size()));
        }
    }

    private double round(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private void getIdMoshrefExams() {
        db.collection("SuperVisorExams")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                IdMoshrefExams = queryDocumentSnapshots.getDocuments().get(0).getId();
            }
        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.place_exam_meanu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_place_exam) {
            if (isValidateInputs()) {
                showDialogPlaceExam();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    private void showDialogPlaceExam() {
        LayoutInflater factory = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View addTesterDialogView = factory.inflate(R.layout.place_exam_dialog, null);
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setView(addTesterDialogView);
        alertDialog.show();

        EditText ed_notes = alertDialog.findViewById(R.id.place_exam_et_notes);
        EditText ed_mark = alertDialog.findViewById(R.id.place_exam_et_mark);

        String statusMark;
        double mark = Double.parseDouble("" + tv_mark_exam.getText().toString());
        if ((int) mark >= 80) {
            statusMark = "اجتاز الطالب الإختبار بنجاح .";
        } else {
            statusMark = "لم يجتاز الطالب الإختبار بنجاح .";
            if (ed_mark != null) {
                ed_mark.setTextColor(Color.RED);
            }
        }
        if (ed_mark != null) {
            ed_mark.setText("درجة الطالب : (" + tv_mark_exam.getText().toString() + ") " + statusMark);
        }

        Button btn_add = alertDialog.findViewById(R.id.place_exam_add_btn_save);
        if (btn_add != null) {
            btn_add.setOnClickListener(view -> {
                if (ed_notes != null && marksExamQuestions != null) {
                    if (ed_notes.getText().toString().trim().isEmpty() && !marksExamQuestions.isEmpty()) {
                        PlaceExamToDB("لا توجد ملاحظات ..");
                    } else if (!ed_notes.getText().toString().trim().isEmpty() && !marksExamQuestions.isEmpty()) {
                        PlaceExamToDB(ed_notes.getText().toString());
                    }
                }
            });
        }

        ImageButton imbtn_close = alertDialog.findViewById(R.id.place_exam_imgbtn_close);
        assert imbtn_close != null;
        imbtn_close.setOnClickListener(view121 -> alertDialog.dismiss());
    }

    private void PlaceExamToDB(String notes) {
        notify = true;
        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("statusAcceptance", 3);
        map1.put("notes", notes);
        map1.put("marksExamQuestions", marksExamQuestions);
        map1.put("signsExamQuestions", signsExamQuestions);
        map1.put("isSeenExam.isSeenMohafez", false);
        map1.put("isSeenExam.isSeenMoshref", false);
        map1.put("isSeenExam.isSeenMoshrefExam", false);
        map1.put("isSeenExam.isSeenEdare", false);
        db.collection("Exam").document(idExam)
                .update(map1);

        if (notify) {
            if (IdMohafez != null && !IdMohafez.isEmpty()) {
                sendNotifications(IdMohafez);
                notify = false;
            }
        }
        new SweetAlertDialog_(this).showDialogSuccess("Good job!", "تمت عملية اعتماد درجة الإختبار بنجاح  !")
                .setConfirmButton("Ok", sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    alertDialog.dismiss();
                    finish();
                });
    }

    private boolean isValidateInputs() {
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        for (int j = 1; j <= questionsNumberExam; j++) {
            View view = li_question_exam_ed.findViewWithTag("" + j);
            if (view instanceof MaterialEditText) {
                MaterialEditText ed_question = (MaterialEditText) view;
                if (Objects.requireNonNull(ed_question.getText()).toString().isEmpty()) {
                    ed_question.setFocusable(true);
                    ed_question.setError("");
                    atomicBoolean.set(false);
                    break;
                } else {
                    atomicBoolean.set(true);
                }
            }
        }
        return atomicBoolean.get();
    }

    private void sendNotifications(String idMohafez) {
        db.collection("Token").document(idMohafez)
                .get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Data data = null;
                if (StudentName != null) {
                    data = new Data(Common.currentPerson.getId(), "لقد تم اعتماد درجة  "
                            + tv_mark_exam.getText().toString() + " لإختبار " + tv_part_exam.getText().toString() +
                            " للطالب : " + StudentName, "حالة إختبار الطالب", "" + idMohafez, "MohafezActivity", "ExamsFragment");
                }

                if (data != null) {
                    Sender sender = new Sender(data, documentSnapshot.getString("token"));
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(@NonNull Call<Response> call, @NonNull retrofit2.Response<Response> response) {
                                    if (response.code() == 200) {
                                        assert response.body() != null;
                                        if (response.body().success != 1) {
                                            new SweetAlertDialog_(getBaseContext())
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

        if (StudentName != null && IdMoshrefExams != null && !IdMoshrefExams.isEmpty()) {
            db.collection("Token").document(IdMoshrefExams)
                    .get().addOnSuccessListener(documentSnapshot1 -> {
                if (documentSnapshot1.exists()) {
                    Data data = new Data(Common.currentPerson.getId(), "لقد تم اعتماد درجة  "
                            + tv_mark_exam.getText().toString() + " لإختبار " + tv_part_exam.getText().toString() +
                            " للطالب : " + StudentName, "حالة إختبار الطالب", "" + IdMoshrefExams, "SuperVisorExamsActivity", "ExamsFragment");
                    Sender sender1 = new Sender(data, documentSnapshot1.getString("token"));
                    apiService.sendNotification(sender1)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(@NonNull Call<Response> call, @NonNull retrofit2.Response<Response> response) {
                                    if (response.code() == 200) {
                                        assert response.body() != null;
                                        if (response.body().success != 1) {
                                            new SweetAlertDialog_(getBaseContext())
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
    }

}