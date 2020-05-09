package com.alansar.center.Mohafez.Activitys;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alansar.center.Common.Common;
import com.alansar.center.FButton;
import com.alansar.center.Mohafez.Model.DailyReport;
import com.alansar.center.R;
import com.alansar.center.SweetAlertDialog_;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateDailyMemorizationActivity extends AppCompatActivity {
    private SearchableSpinner sp_surah_start, sp_surah_end, sp_surah_start_Murajaea, sp_surah_end_Murajaea;
    private RadioGroup rg_status_hafez, rg_status_student, rg_evaluation_student;
    private MaterialEditText ed_Notes, ed_aya_start, ed_aya_end, edt_Date, edt_aya_start_Murajaea, edt_aya_end_Murajaea;
    private String UID;
    private TextView tv_evaluation_student, tv_Person_name;
    private FButton update_daily_memorization;
    private String status_student, status_present, evaluation_student_hefez, evaluation_student_mourahae, id_Report;
    private SweetAlertDialog_ sweetAlertDialog;
    private DatePickerDialog.OnDateSetListener setListener;
    private int year, month, day;
    private MaterialCheckBox checkbox_date_default;
    private String Notes_No_hafez, Notes_Absent, Notes_Authorized, dayOfWeek;
    private FirebaseFirestore db;
    private CircleImageView img_profile;
    private String[] Values;
    private DailyReport retdailyReport;
    private int[] arrayOfAyaNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mohafez_update_daily_memorization);
        InitializeFields();

        if (getIntent() != null && getIntent().getStringExtra("UID") != null
                && getIntent().getStringExtra("id_Report") != null) {
            UID = getIntent().getStringExtra("UID");
            id_Report = getIntent().getStringExtra("id_Report");
        }

        checkbox_date_default.setOnCheckedChangeListener((compoundButton, b) -> resetDefaultDate(b));
        update_daily_memorization.setOnClickListener(view -> checkStatusStudent());
        getPersonData();
        getReport();
        edt_Date.setOnClickListener(view -> listenerDateOnbtn());

        rg_evaluation_student.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.rb_Excellent:
                    evaluation_student_hefez = Common.EXCELLENT;
                    evaluation_student_mourahae = Common.EXCELLENT;
                    break;
                case R.id.rb_very_good:
                    evaluation_student_hefez = Common.VERY_GOOD;
                    evaluation_student_mourahae = Common.VERY_GOOD;
                    break;
                case R.id.rb_good:
                    evaluation_student_hefez = Common.GOOD;
                    evaluation_student_mourahae = Common.GOOD;
                    break;
                case R.id.rb_Weak:
                    evaluation_student_hefez = Common.WEEK;
                    evaluation_student_mourahae = Common.WEEK;
                    break;
            }
        });
    }

    private void getReport() {
        db.collection("DailyReport").document(id_Report)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (documentSnapshot != null) {
                        if (documentSnapshot.exists()) {
                            retdailyReport = documentSnapshot.toObject(DailyReport.class);
                            setDataFields(retdailyReport);
                        }
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void setDataFields(DailyReport dailyReport) {
        if (dailyReport != null) {
            switch (dailyReport.getStatusStudent()) {
                case Common.PRESENT:
                    rg_status_student.check(R.id.rb_Present);
                    checkStatusPresent(dailyReport);
                    findViewById(R.id.liner_status_hefez).setVisibility(View.VISIBLE);
                    status_student = Common.PRESENT;
                    break;
                case Common.ABSENT:
                    rg_status_student.check(R.id.rb_absent);
                    findViewById(R.id.liner_ed_notes).setVisibility(View.VISIBLE);
                    ed_Notes.setText(dailyReport.getNotes());
                    edt_Date.setText(dailyReport.getDay() + "/" + dailyReport.getMonth() + "/" + dailyReport.getYear());
                    status_student = Common.ABSENT;
                    dayOfWeek = dailyReport.getDayOfWeek();
                    day = dailyReport.getDay();
                    month = dailyReport.getMonth();
                    year = dailyReport.getYear();
                    break;
                case Common.AUTHORIZED:
                    rg_status_student.check(R.id.rb_authorized);
                    findViewById(R.id.liner_ed_notes).setVisibility(View.VISIBLE);
                    ed_Notes.setText(dailyReport.getNotes());
                    edt_Date.setText(dailyReport.getDay() + "/" + dailyReport.getMonth() + "/" + dailyReport.getYear());
                    status_student = Common.AUTHORIZED;
                    dayOfWeek = dailyReport.getDayOfWeek();
                    day = dailyReport.getDay();
                    month = dailyReport.getMonth();
                    year = dailyReport.getYear();
                    break;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void checkStatusPresent(DailyReport dailyReport) {
        if (dailyReport != null) {
            switch (dailyReport.getStatusHefeaz()) {
                case Common.HAFEZ:
                    rg_status_hafez.check(R.id.rb_hafiz);
                    findViewById(R.id.liner_status_hefez_report).setVisibility(View.VISIBLE);
                    findViewById(R.id.liner_evaluation_student).setVisibility(View.VISIBLE);

                    for (int i = 0; i < Values.length; i++) {
                        if (Values[i].equals(dailyReport.getSuratStart())) {
                            sp_surah_start.setSelection(i);
                        }
                        if (Values[i].equals(dailyReport.getSuratEnd())) {
                            sp_surah_end.setSelection(i);
                        }
                    }

                    ed_aya_start.setText("" + dailyReport.getAyaStart());
                    ed_aya_end.setText("" + dailyReport.getAyaEnd());
                    setDataEvaluationStudent(dailyReport.getEvaluationStudent());
                    edt_Date.setText(dailyReport.getDay() + "/" + dailyReport.getMonth() + "/" + dailyReport.getYear());
                    status_present = Common.HAFEZ;
                    dayOfWeek = dailyReport.getDayOfWeek();
                    day = dailyReport.getDay();
                    month = dailyReport.getMonth();
                    year = dailyReport.getYear();
                    break;
                case Common.MURAJAEA:
                    rg_status_hafez.check(R.id.rb_Murajaea);
                    findViewById(R.id.liner_status_Murajaea_report).setVisibility(View.VISIBLE);
                    findViewById(R.id.liner_evaluation_student).setVisibility(View.VISIBLE);

                    for (int i = 0; i < Values.length; i++) {
                        if (Values[i].equals(dailyReport.getSuratStart())) {
                            sp_surah_start_Murajaea.setSelection(i);
                        }
                        if (Values[i].equals(dailyReport.getSuratEnd())) {
                            sp_surah_end_Murajaea.setSelection(i);
                        }
                    }

                    edt_aya_start_Murajaea.setText("" + dailyReport.getAyaStart());
                    edt_aya_end_Murajaea.setText("" + dailyReport.getAyaEnd());
                    setDataEvaluationStudent(dailyReport.getEvaluationStudent());
                    edt_Date.setText(dailyReport.getDay() + "/" + dailyReport.getMonth() + "/" + dailyReport.getYear());
                    status_present = Common.MURAJAEA;
                    dayOfWeek = dailyReport.getDayOfWeek();
                    day = dailyReport.getDay();
                    month = dailyReport.getMonth();
                    year = dailyReport.getYear();
                    break;
                case Common.NO_HAFEZ:
                    rg_status_hafez.check(R.id.rb_not_hafiz);
                    findViewById(R.id.liner_ed_notes).setVisibility(View.VISIBLE);
                    ed_Notes.setText(dailyReport.getNotes());
                    edt_Date.setText(dailyReport.getDay() + "/" + dailyReport.getMonth() + "/" + dailyReport.getYear());
                    status_present = Common.NO_HAFEZ;
                    dayOfWeek = dailyReport.getDayOfWeek();
                    day = dailyReport.getDay();
                    month = dailyReport.getMonth();
                    year = dailyReport.getYear();
                    break;
            }
        }
    }

    private void setDataEvaluationStudent(String evaluationStudent) {
        if (evaluationStudent != null) {
            switch (evaluationStudent) {
                case Common.EXCELLENT:
                    rg_evaluation_student.check(R.id.rb_Excellent);
                    evaluation_student_hefez = Common.EXCELLENT;
                    evaluation_student_mourahae = Common.EXCELLENT;
                    break;
                case Common.VERY_GOOD:
                    rg_evaluation_student.check(R.id.rb_very_good);
                    evaluation_student_hefez = Common.VERY_GOOD;
                    evaluation_student_mourahae = Common.VERY_GOOD;
                    break;
                case Common.GOOD:
                    rg_evaluation_student.check(R.id.rb_good);
                    evaluation_student_hefez = Common.GOOD;
                    evaluation_student_mourahae = Common.GOOD;
                    break;
                case Common.WEEK:
                    rg_evaluation_student.check(R.id.rb_Weak);
                    evaluation_student_hefez = Common.WEEK;
                    evaluation_student_mourahae = Common.WEEK;
                    break;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void getPersonData() {
        db.collection("Person").document(UID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                tv_Person_name.setText(documentSnapshot.getString("fname")
                        + " " + documentSnapshot.getString("mname") + " " +
                        documentSnapshot.get("lname"));

                if (!Objects.requireNonNull(documentSnapshot.getString("image")).isEmpty()) {
                    Picasso.get().load(documentSnapshot.getString("image")).into(img_profile);
                }
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void resetDefaultDate(boolean b) {
        if (b) {
            showDialogPickerDate();
            edt_Date.setOnClickListener(null);
        } else {
            edt_Date.setText("");
            day = 0;
            month = 0;
            year = 0;
            edt_Date.setOnClickListener(view -> listenerDateOnbtn());
        }
    }

    private void checkStatusStudent() {
        if (status_student != null) {
            switch (status_student) {
                case Common.PRESENT:
                    checkInputPresent();
                    break;
                case Common.ABSENT:
                    validataInputApsent();
                    break;
                case Common.AUTHORIZED:
                    validataInputAuthorized();
                    break;
            }
        } else {
            sweetAlertDialog.showDialogError("يجب إختيار حالة الطالب");
        }
    }

    private void validataInputAuthorized() {
        if (!Objects.requireNonNull(ed_Notes.getText()).toString().trim().isEmpty()
                && !Objects.requireNonNull(edt_Date.getText()).toString().isEmpty()) {
            Notes_Authorized = ed_Notes.getText().toString();
            updateDataAuthorized(Notes_Authorized);
        } else {
            if (Objects.requireNonNull(ed_Notes.getText()).toString().trim().isEmpty()) {
                Notes_Authorized = "لا توجد ملاحظات";
                updateDataAuthorized(Notes_Authorized);
            } else if (Objects.requireNonNull(edt_Date.getText()).toString().isEmpty()) {
                sweetAlertDialog.showDialogError("يجب التحقق من تاريخ اليوم");
            }
        }
    }

    private void updateDataAuthorized(String notes_authorized) {
        HashMap<String, Object> map = new HashMap<>();
        if (day != retdailyReport.getDay()) {
            map.put("day", day);
            map.put("dayOfWeek", dayOfWeek);
            Log.d("sss", "day");
        }
        if (month != retdailyReport.getMonth()) {
            map.put("month", month);
            map.put("dayOfWeek", dayOfWeek);
            Log.d("sss", "month");
        }
        if (year != retdailyReport.getYear()) {
            map.put("year", year);
            map.put("dayOfWeek", dayOfWeek);
            Log.d("sss", "year");
        }
        if (!notes_authorized.equals(retdailyReport.getNotes())) {
            map.put("Notes", notes_authorized);
        }

        if (!map.isEmpty()) {
            db.collection("DailyReport").document(id_Report).update(map);
        }
        sweetAlertDialog.showDialogSuccess("OK", "تم تحديث الحفظ اليومي للطالب بنجاح !")
                .setConfirmButton("OK", sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    finish();
                });
    }


    private void validataInputApsent() {
        if (!Objects.requireNonNull(ed_Notes.getText()).toString().trim().isEmpty()
                && !Objects.requireNonNull(edt_Date.getText()).toString().isEmpty()) {
            Notes_Absent = ed_Notes.getText().toString();
            updateDataApsent(Notes_Absent);
        } else {
            if (Objects.requireNonNull(ed_Notes.getText()).toString().trim().isEmpty()) {
                Notes_Absent = "لا توجد ملاحظات";
                updateDataApsent(Notes_Absent);
            } else if (Objects.requireNonNull(edt_Date.getText()).toString().isEmpty()) {
                sweetAlertDialog.showDialogError("يجب التحقق من تاريخ اليوم");
            }
        }
    }

    private void updateDataApsent(String notes_absent) {
        HashMap<String, Object> map = new HashMap<>();
        if (day != retdailyReport.getDay()) {
            map.put("day", day);
            map.put("dayOfWeek", dayOfWeek);
            Log.d("sss", "day");
        }
        if (month != retdailyReport.getMonth()) {
            map.put("month", month);
            map.put("dayOfWeek", dayOfWeek);
            Log.d("sss", "month");
        }
        if (year != retdailyReport.getYear()) {
            map.put("year", year);
            map.put("dayOfWeek", dayOfWeek);
            Log.d("sss", "year");
        }
        if (!notes_absent.equals(retdailyReport.getNotes())) {
            map.put("Notes", notes_absent);
        }

        if (!map.isEmpty()) {
            db.collection("DailyReport").document(id_Report).update(map);
        }
        sweetAlertDialog.showDialogSuccess("OK", "تم تحديث الحفظ اليومي للطالب بنجاح !")
                .setConfirmButton("OK", sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    finish();
                });

    }

    private void checkInputPresent() {
        if (status_present != null) {
            switch (status_present) {
                case Common.HAFEZ:
                    validataInputHafez();
                    break;
                case Common.MURAJAEA:
                    validataInputMurajaea();
                    break;
                case Common.NO_HAFEZ:
                    validataInputNo_hafez();
                    break;
            }
        } else {
            sweetAlertDialog.showDialogError("يجب إختيار حالة حفظ الطالب");
        }
    }

    private void validataInputNo_hafez() {
        if (!Objects.requireNonNull(ed_Notes.getText()).toString().trim().isEmpty()
                && !Objects.requireNonNull(edt_Date.getText()).toString().isEmpty()) {
            Notes_No_hafez = ed_Notes.getText().toString();
            updateDataNoHafez(Notes_No_hafez);
        } else {
            if (Objects.requireNonNull(ed_Notes.getText()).toString().trim().isEmpty()) {
                Notes_No_hafez = "لا توجد ملاحظات";
                updateDataNoHafez(Notes_No_hafez);
            } else if (Objects.requireNonNull(edt_Date.getText()).toString().isEmpty()) {
                sweetAlertDialog.showDialogError("يجب التحقق من تاريخ اليوم");
            }
        }
    }

    private void updateDataNoHafez(String notes_no_hafez) {
        HashMap<String, Object> map = new HashMap<>();
        if (day != retdailyReport.getDay()) {
            map.put("day", day);
            map.put("dayOfWeek", dayOfWeek);
            Log.d("sss", "day");
        }
        if (month != retdailyReport.getMonth()) {
            map.put("month", month);
            map.put("dayOfWeek", dayOfWeek);
            Log.d("sss", "month");
        }
        if (year != retdailyReport.getYear()) {
            map.put("year", year);
            map.put("dayOfWeek", dayOfWeek);
            Log.d("sss", "year");
        }
        if (!notes_no_hafez.equals(retdailyReport.getNotes())) {
            map.put("Notes", notes_no_hafez);
        }

        if (!map.isEmpty()) {
            db.collection("DailyReport").document(id_Report).update(map);
        }
        sweetAlertDialog.showDialogSuccess("OK", "تم تحديث الحفظ اليومي للطالب بنجاح !")
                .setConfirmButton("OK", sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    finish();
                });
    }

    private void clearInputs_Murajaea() {
        sp_surah_start_Murajaea.setSelection(0);
        sp_surah_end_Murajaea.setSelection(0);
        evaluation_student_mourahae = "";
        edt_aya_start_Murajaea.setText("");
        edt_aya_end_Murajaea.setText("");
    }

    private void clearInputs_Hefea() {
        evaluation_student_hefez = "";
        sp_surah_start.setSelection(0);
        sp_surah_end.setSelection(0);
        ed_aya_start.setText("");
        ed_aya_end.setText("");
    }

    private void validataInputMurajaea() {
        if (sp_surah_start_Murajaea.getSelectedItemPosition() != 0 &&
                sp_surah_end_Murajaea.getSelectedItemPosition() != 0 &&
                !Objects.requireNonNull(edt_aya_start_Murajaea.getText()).toString().isEmpty() &&
                !Objects.requireNonNull(edt_aya_end_Murajaea.getText()).toString().isEmpty() &&
                evaluation_student_mourahae != null &&
                !evaluation_student_mourahae.isEmpty() &&
                !Objects.requireNonNull(edt_Date.getText()).toString().isEmpty()) {
            if (sp_surah_start_Murajaea.getSelectedItem().toString()
                    .equals(sp_surah_end_Murajaea.getSelectedItem().toString())) {
                if (Integer.parseInt(edt_aya_start_Murajaea.getText().toString()) <= arrayOfAyaNumber[sp_surah_start_Murajaea.getSelectedItemPosition()]
                        && Integer.parseInt(edt_aya_end_Murajaea.getText().toString()) <= arrayOfAyaNumber[sp_surah_end_Murajaea.getSelectedItemPosition()]) {
                    if (Integer.parseInt(edt_aya_start_Murajaea.getText().toString()) >=
                            Integer.parseInt(edt_aya_end_Murajaea.getText().toString())) {
                        sweetAlertDialog.showDialogError("يجب أن يكون رقم أية البداية أقل من رقم أية النهاية");
                    } else {
                        updateDataMurajaea();
                    }
                } else {
                    sweetAlertDialog.showDialogError("عذرا عدد أيات سورة " + sp_surah_end_Murajaea.getSelectedItem().toString() + " " + arrayOfAyaNumber[sp_surah_end_Murajaea.getSelectedItemPosition()]);
                }
            } else {
                if (Integer.parseInt(edt_aya_start_Murajaea.getText().toString()) <= arrayOfAyaNumber[sp_surah_start_Murajaea.getSelectedItemPosition()]) {
                    if (Integer.parseInt(edt_aya_end_Murajaea.getText().toString()) <= arrayOfAyaNumber[sp_surah_end_Murajaea.getSelectedItemPosition()]) {
                        updateDataMurajaea();
                    } else {
                        sweetAlertDialog.showDialogError("عذرا عدد أيات سورة " + sp_surah_end_Murajaea.getSelectedItem().toString() + " " + arrayOfAyaNumber[sp_surah_end_Murajaea.getSelectedItemPosition()]);
                    }
                } else {
                    sweetAlertDialog.showDialogError("عذرا عدد أيات سورة " + sp_surah_start_Murajaea.getSelectedItem().toString() + " " + arrayOfAyaNumber[sp_surah_start_Murajaea.getSelectedItemPosition()]);
                }

            }
        } else {
            if (sp_surah_start_Murajaea.getSelectedItemPosition() == 0) {
                sweetAlertDialog.showDialogError("يجب اختيار سورة البداية");
            } else if (sp_surah_end_Murajaea.getSelectedItemPosition() == 0) {
                sweetAlertDialog.showDialogError("يجب اختيار سورة النهاية");
            } else if (Objects.requireNonNull(edt_aya_start_Murajaea.getText()).toString().isEmpty()) {
                sweetAlertDialog.showDialogError("حقل أية البداية فارغ");
            } else if (Objects.requireNonNull(edt_aya_end_Murajaea.getText()).toString().isEmpty()) {
                sweetAlertDialog.showDialogError("حقل أية النهاية فارغ");
            } else if (evaluation_student_mourahae == null || evaluation_student_mourahae.isEmpty()) {
                sweetAlertDialog.showDialogError("يجب تقييم حفظ الطالب");
            } else if (Objects.requireNonNull(edt_Date.getText()).toString().isEmpty()) {
                sweetAlertDialog.showDialogError("يجب التحقق من تاريخ اليوم");
            }
        }
    }

    private void validataInputHafez() {
        if (sp_surah_start.getSelectedItemPosition() != 0 &&
                sp_surah_end.getSelectedItemPosition() != 0 &&
                !Objects.requireNonNull(ed_aya_start.getText()).toString().isEmpty() &&
                !Objects.requireNonNull(ed_aya_end.getText()).toString().isEmpty() &&
                evaluation_student_hefez != null &&
                !evaluation_student_hefez.isEmpty() &&
                !Objects.requireNonNull(edt_Date.getText()).toString().isEmpty()) {
            if (sp_surah_start.getSelectedItem().toString()
                    .equals(sp_surah_end.getSelectedItem().toString())) {
                if (Integer.parseInt(ed_aya_start.getText().toString()) <= arrayOfAyaNumber[sp_surah_start.getSelectedItemPosition()]
                        && Integer.parseInt(ed_aya_end.getText().toString()) <= arrayOfAyaNumber[sp_surah_end.getSelectedItemPosition()]) {
                    if (Integer.parseInt(ed_aya_start.getText().toString()) >=
                            Integer.parseInt(ed_aya_end.getText().toString())) {
                        sweetAlertDialog.showDialogError("يجب أن يكون رقم أية البداية أقل من رقم أية النهاية");
                    } else {
                        updateDataHefez();
                    }
                } else {
                    sweetAlertDialog.showDialogError("عذرا عدد أيات سورة " + sp_surah_start.getSelectedItem().toString() + " " + arrayOfAyaNumber[sp_surah_start.getSelectedItemPosition()]);
                }
            } else {
                if (Integer.parseInt(ed_aya_start.getText().toString()) <= arrayOfAyaNumber[sp_surah_start.getSelectedItemPosition()]) {
                    if (Integer.parseInt(ed_aya_end.getText().toString()) <= arrayOfAyaNumber[sp_surah_end.getSelectedItemPosition()]) {
                        updateDataHefez();
                    } else {
                        sweetAlertDialog.showDialogError("عذرا عدد أيات سورة " + sp_surah_end.getSelectedItem().toString() + " " + arrayOfAyaNumber[sp_surah_end.getSelectedItemPosition()]);
                    }
                } else {
                    sweetAlertDialog.showDialogError("عذرا عدد أيات سورة " + sp_surah_start.getSelectedItem().toString() + " " + arrayOfAyaNumber[sp_surah_start.getSelectedItemPosition()]);
                }

            }
        } else {
            if (sp_surah_start.getSelectedItemPosition() == 0) {
                sweetAlertDialog.showDialogError("يجب اختيار سورة البداية");
            } else if (sp_surah_end.getSelectedItemPosition() == 0) {
                sweetAlertDialog.showDialogError("يجب اختيار سورة النهاية");
            } else if (Objects.requireNonNull(ed_aya_start.getText()).toString().isEmpty()) {
                sweetAlertDialog.showDialogError("حقل أية البداية فارغ");
            } else if (Objects.requireNonNull(ed_aya_end.getText()).toString().isEmpty()) {
                sweetAlertDialog.showDialogError("حقل أية النهاية فارغ");
            } else if (evaluation_student_hefez == null || evaluation_student_hefez.isEmpty()) {
                sweetAlertDialog.showDialogError("يجب تقييم حفظ الطالب");
            } else if (Objects.requireNonNull(edt_Date.getText()).toString().isEmpty()) {
                sweetAlertDialog.showDialogError("يجب التحقق من تاريخ اليوم");
            }
        }
    }

    private void updateDataMurajaea() {
        if (!validateUpdateMurajaeaDB().isEmpty()) {
            db.collection("DailyReport").document(id_Report).update(validateUpdateMurajaeaDB());
        }

        sweetAlertDialog.showDialogSuccess("OK", "تم تحديث الحفظ اليومي للطالب بنجاح !")
                .setConfirmButton("OK", sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    clearInputs_Hefea();
                    finish();
                });
    }

    private HashMap<String, Object> validateUpdateMurajaeaDB() {
        HashMap<String, Object> map = new HashMap<>();
        DailyReport dailyReport = new DailyReport(id_Report, UID, Common.currentGroupId, sp_surah_start_Murajaea.getSelectedItem().toString(),
                sp_surah_end_Murajaea.getSelectedItem().toString(), status_student, status_present, evaluation_student_mourahae, day, month
                , year, "لا توجد ملاحظات", dayOfWeek, Integer.parseInt(Objects.requireNonNull(edt_aya_start_Murajaea.getText()).toString())
                , Integer.parseInt(Objects.requireNonNull(edt_aya_end_Murajaea.getText()).toString()));
        if (retdailyReport != null) {
            if (!dailyReport.getSuratStart().equals(retdailyReport.getSuratStart())) {
                map.put("suratStart", dailyReport.getSuratStart());
            }
            if (!dailyReport.getSuratEnd().equals(retdailyReport.getSuratEnd())) {
                map.put("suratEnd", dailyReport.getSuratEnd());
            }
            if (dailyReport.getAyaStart() != retdailyReport.getAyaStart()) {
                map.put("ayaStart", dailyReport.getAyaStart());
            }
            if (dailyReport.getAyaEnd() != retdailyReport.getAyaEnd()) {
                map.put("ayaEnd", dailyReport.getAyaEnd());
            }
            if (!dailyReport.getStatusStudent().equals(retdailyReport.getStatusStudent())) {
                map.put("StatusStudent", dailyReport.getStatusStudent());
            }
            if (!dailyReport.getStatusHefeaz().equals(retdailyReport.getStatusHefeaz())) {
                map.put("statusHefeaz", dailyReport.getStatusHefeaz());
            }
            if (!dailyReport.getEvaluationStudent().equals(retdailyReport.getEvaluationStudent())) {
                map.put("evaluationStudent", dailyReport.getEvaluationStudent());
            }
            if (dailyReport.getDay() != retdailyReport.getDay()) {
                map.put("day", dailyReport.getDay());
                map.put("dayOfWeek", dayOfWeek);
                Log.d("sss", "day");
            }
            if (dailyReport.getMonth() != retdailyReport.getMonth()) {
                map.put("month", dailyReport.getMonth());
                map.put("dayOfWeek", dayOfWeek);
                Log.d("sss", "month");
            }
            if (dailyReport.getYear() != retdailyReport.getYear()) {
                map.put("year", dailyReport.getYear());
                map.put("dayOfWeek", dayOfWeek);
                Log.d("sss", "year");
            }
            if (!dailyReport.getNotes().equals(retdailyReport.getNotes())) {
                map.put("Notes", dailyReport.getNotes());
            }
        }

        return map;
    }

    private void updateDataHefez() {
        if (!validateUpdateHafezDB().isEmpty()) {
            db.collection("DailyReport").document(id_Report).update(validateUpdateHafezDB());
        }
        sweetAlertDialog.showDialogSuccess("OK", "تم تحديث الحفظ اليومي للطالب بنجاح !")
                .setConfirmButton("OK", sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    clearInputs_Hefea();
                    finish();
                });
    }

    private HashMap<String, Object> validateUpdateHafezDB() {
        HashMap<String, Object> map = new HashMap<>();
        DailyReport dailyReport = new DailyReport(id_Report, UID, Common.currentGroupId, sp_surah_start.getSelectedItem().toString(),
                sp_surah_end.getSelectedItem().toString(), status_student, status_present, evaluation_student_hefez, day, month
                , year, "لا توجد ملاحظات", dayOfWeek, Integer.parseInt(Objects.requireNonNull(ed_aya_start.getText()).toString())
                , Integer.parseInt(Objects.requireNonNull(ed_aya_end.getText()).toString()));
        if (!dailyReport.getSuratStart().equals(retdailyReport.getSuratStart())) {
            map.put("suratStart", dailyReport.getSuratStart());
        }
        if (!dailyReport.getSuratEnd().equals(retdailyReport.getSuratEnd())) {
            map.put("suratEnd", dailyReport.getSuratEnd());
        }
        if (dailyReport.getAyaStart() != retdailyReport.getAyaStart()) {
            map.put("ayaStart", dailyReport.getAyaStart());
        }
        if (dailyReport.getAyaEnd() != retdailyReport.getAyaEnd()) {
            map.put("ayaEnd", dailyReport.getAyaEnd());
        }
        if (!dailyReport.getStatusStudent().equals(retdailyReport.getStatusStudent())) {
            map.put("StatusStudent", dailyReport.getStatusStudent());
        }
        if (!dailyReport.getStatusHefeaz().equals(retdailyReport.getStatusHefeaz())) {
            map.put("statusHefeaz", dailyReport.getStatusHefeaz());
        }
        if (!dailyReport.getEvaluationStudent().equals(retdailyReport.getEvaluationStudent())) {
            map.put("evaluationStudent", dailyReport.getEvaluationStudent());
        }
        if (dailyReport.getDay() != retdailyReport.getDay()) {
            map.put("day", dailyReport.getDay());
            map.put("dayOfWeek", dayOfWeek);
        }
        if (dailyReport.getMonth() != retdailyReport.getMonth()) {
            map.put("month", dailyReport.getMonth());
            map.put("dayOfWeek", dayOfWeek);
        }
        if (dailyReport.getYear() != retdailyReport.getYear()) {
            map.put("year", dailyReport.getYear());
            map.put("dayOfWeek", dayOfWeek);
        }
        if (!dailyReport.getNotes().equals(retdailyReport.getNotes())) {
            map.put("Notes", dailyReport.getNotes());
        }
        return map;
    }


    @SuppressLint("SetTextI18n")
    private void showDialogPickerDate() {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("EEE");
        dayOfWeek = df.format(calendar.getTime());
        Toast.makeText(this, "" + dayOfWeek, Toast.LENGTH_SHORT).show();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = month + 1;
        edt_Date.setText(day + "/" + month + "/" + year);

        setListener = (datePicker, year1, month1, day1) -> {
            month1 = month1 + 1;
            String date = day1 + "/" + month1 + "/" + year1;
            edt_Date.setText(date);
        };
    }

    private void InitializeFields() {
        sp_surah_start = findViewById(R.id.sp_surah_start);
        sp_surah_end = findViewById(R.id.sp_surah_end);
        ed_Notes = findViewById(R.id.edt_notes);
        ed_aya_start = findViewById(R.id.edt_aya_start);
        ed_aya_end = findViewById(R.id.edt_aya_end);
        rg_status_hafez = findViewById(R.id.radio_group_hafiz_student);
        rg_evaluation_student = findViewById(R.id.radio_group_evaluation_student);
        tv_evaluation_student = findViewById(R.id.tv_evaluation_student);
        sp_surah_start.setTitle("اختر سورة البداية");
        sp_surah_end.setTitle("اختر سورة النهاية");
        rg_status_student = findViewById(R.id.radio_group_status_student);
        update_daily_memorization = findViewById(R.id.btn_update_daily_memorization);
        edt_Date = findViewById(R.id.edt_time);
        checkbox_date_default = findViewById(R.id.checkbox_date_default);
        sp_surah_start_Murajaea = findViewById(R.id.sp_surah_start_Murajaea);
        sp_surah_end_Murajaea = findViewById(R.id.sp_surah_end_Murajaea);
        edt_aya_start_Murajaea = findViewById(R.id.edt_aya_start_Murajaea);
        edt_aya_end_Murajaea = findViewById(R.id.edt_aya_end_Murajaea);
        img_profile = findViewById(R.id.img_profile);
        tv_Person_name = findViewById(R.id.tv_Person_name);
        Values = getResources().getStringArray(
                R.array.surah_array);
        arrayOfAyaNumber = getResources().getIntArray(R.array.ayat_array);

        db = FirebaseFirestore.getInstance();
        sweetAlertDialog = new SweetAlertDialog_(this);
    }

    private void listenerDateOnbtn() {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("EEE");
        dayOfWeek = df.format(calendar.getTime());
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        setListener = (datePicker, year1, month1, day1) -> {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.set(year1, month1, day1);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dateFormat.setCalendar(gregorianCalendar);
            dayOfWeek = df.format(dateFormat.getCalendar().getTime());
            day = day1;
            year = year1;
            month1 = month1 + 1;
            month = month1;
            String date = day1 + "/" + month1 + "/" + year;
            Toast.makeText(this, "" + dayOfWeek, Toast.LENGTH_SHORT).show();
            edt_Date.setText(date);
        };
        DatePickerDialog pickerDialog = new DatePickerDialog(this,
                setListener, year, month, day);
        pickerDialog.show();
    }

}