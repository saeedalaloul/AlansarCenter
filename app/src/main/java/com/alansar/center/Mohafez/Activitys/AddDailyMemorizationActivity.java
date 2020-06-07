package com.alansar.center.Mohafez.Activitys;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddDailyMemorizationActivity extends AppCompatActivity {

    private SearchableSpinner spinner_aya_start, spinner_aya_end, sp_surah_start_Murajaea, sp_surah_end_Murajaea;
    private RadioGroup rg_status_hafez, rg_status_student, rg_evaluation_student;
    private MaterialEditText ed_Notes, ed_aya_start, ed_aya_end, edt_Date, edt_aya_start_Murajaea, edt_aya_end_Murajaea;
    private String UID;
    private TextView tv_evaluation_student, tv_Person_name;
    private FButton add_daily_memorization;
    private String status_student, status_present, evaluation_student_hefez, evaluation_student_mourahae;
    private SweetAlertDialog_ sweetAlertDialog;
    private DatePickerDialog.OnDateSetListener setListener;
    private int year, month, day;
    private MaterialCheckBox checkbox_date_default;
    private String Notes_No_hafez, Notes_Absent, Notes_Authorized, dayOfWeek;
    private FirebaseFirestore db;
    private CircleImageView img_profile;
    private int[] arrayOfAyaNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_daily_memorization);
        InitializeFields();
        showDialogPickerDate();
        if (getIntent() != null && getIntent().getStringExtra("UID") != null) {
            UID = getIntent().getStringExtra("UID");
        }
        checkbox_date_default.setOnCheckedChangeListener((compoundButton, b) -> resetDefaultDate(b));
        rg_status_student.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.rb_Present:
                    findViewById(R.id.liner_ed_notes).setVisibility(View.GONE);
                    findViewById(R.id.liner_status_hefez).setVisibility(View.VISIBLE);
                    status_student = Common.PRESENT;
                    rg_status_hafez.clearCheck();
                    break;
                case R.id.rb_absent:
                    findViewById(R.id.liner_ed_notes).setVisibility(View.VISIBLE);
                    findViewById(R.id.liner_status_hefez).setVisibility(View.GONE);
                    findViewById(R.id.liner_status_hefez_report).setVisibility(View.GONE);
                    findViewById(R.id.liner_evaluation_student).setVisibility(View.GONE);
                    status_student = Common.ABSENT;
                    Notes_Absent = "";
                    break;
                case R.id.rb_authorized:
                    findViewById(R.id.liner_ed_notes).setVisibility(View.VISIBLE);
                    findViewById(R.id.liner_status_hefez).setVisibility(View.GONE);
                    findViewById(R.id.liner_status_hefez_report).setVisibility(View.GONE);
                    findViewById(R.id.liner_evaluation_student).setVisibility(View.GONE);
                    status_student = Common.AUTHORIZED;
                    Notes_Authorized = "";
                    break;
            }
            findViewById(R.id.liner_evaluation_student).setVisibility(View.GONE);
            findViewById(R.id.liner_status_hefez_report).setVisibility(View.GONE);
            findViewById(R.id.liner_status_Murajaea_report).setVisibility(View.GONE);
        });

        rg_status_hafez.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.rb_hafiz:
                    findViewById(R.id.liner_status_hefez_report).setVisibility(View.VISIBLE);
                    findViewById(R.id.liner_ed_notes).setVisibility(View.GONE);
                    findViewById(R.id.liner_status_Murajaea_report).setVisibility(View.GONE);
                    findViewById(R.id.liner_evaluation_student).setVisibility(View.VISIBLE);
                    tv_evaluation_student.setText("تقييم حفظ الطالب :");
                    status_present = Common.HAFEZ;
                    clearInputs_Hefea();
                    rg_evaluation_student.clearCheck();
                    break;
                case R.id.rb_Murajaea:
                    findViewById(R.id.liner_ed_notes).setVisibility(View.GONE);
                    findViewById(R.id.liner_status_hefez_report).setVisibility(View.GONE);
                    findViewById(R.id.liner_status_Murajaea_report).setVisibility(View.VISIBLE);
                    findViewById(R.id.liner_evaluation_student).setVisibility(View.VISIBLE);
                    tv_evaluation_student.setText("تقييم مراجعة الطالب :");
                    status_present = Common.MURAJAEA;
                    clearInputs_Murajaea();
                    rg_evaluation_student.clearCheck();
                    break;
                case R.id.rb_not_hafiz:
                    findViewById(R.id.liner_ed_notes).setVisibility(View.VISIBLE);
                    findViewById(R.id.liner_status_hefez_report).setVisibility(View.GONE);
                    findViewById(R.id.liner_status_Murajaea_report).setVisibility(View.GONE);
                    findViewById(R.id.liner_evaluation_student).setVisibility(View.GONE);
                    status_present = Common.NO_HAFEZ;
                    Notes_No_hafez = "";
                    break;
            }
        });

        spinner_aya_start.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinner_aya_end.setSelection(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_surah_start_Murajaea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sp_surah_end_Murajaea.setSelection(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

        add_daily_memorization.setOnClickListener(view -> checkStatusStudent());
        getPersonData();
    }

    @SuppressLint("SetTextI18n")
    private void getPersonData() {
        db.collection("Person").document(UID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot != null) {
                if (documentSnapshot.exists()) {
                    tv_Person_name.setText(documentSnapshot.getString("fname")
                            + " " + documentSnapshot.getString("mname") + " " +
                            documentSnapshot.get("lname"));

                    if (documentSnapshot.getString("image") != null && !Objects.requireNonNull(documentSnapshot.getString("image")).isEmpty()) {
                        Picasso.get().load(documentSnapshot.getString("image")).into(img_profile);
                    }
                }
            }
        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
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
            edt_Date.setOnClickListener(view -> {
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
                DatePickerDialog pickerDialog = new DatePickerDialog(view.getContext(),
                        setListener, year, month, day);
                pickerDialog.show();
            });
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
            addDataAuthorized(Notes_Authorized);
        } else if (!Objects.requireNonNull(edt_Date.getText()).toString().isEmpty()
                && Objects.requireNonNull(ed_Notes.getText()).toString().trim().isEmpty()) {
            Notes_Authorized = "لا توجد ملاحظات";
            addDataAuthorized(Notes_Authorized);
        } else if (Objects.requireNonNull(edt_Date.getText()).toString().isEmpty()) {
            sweetAlertDialog.showDialogError("يجب التحقق من تاريخ اليوم");
        }
    }


    private void validataInputApsent() {
        if (!Objects.requireNonNull(ed_Notes.getText()).toString().trim().isEmpty()
                && !Objects.requireNonNull(edt_Date.getText()).toString().isEmpty()) {
            Notes_Absent = ed_Notes.getText().toString();
            addDataApsent(Notes_Absent);
        } else if (!Objects.requireNonNull(edt_Date.getText()).toString().isEmpty()
                && Objects.requireNonNull(ed_Notes.getText()).toString().trim().isEmpty()) {
            Notes_Absent = "لا توجد ملاحظات";
            addDataApsent(Notes_Absent);
        } else if (Objects.requireNonNull(edt_Date.getText()).toString().isEmpty()) {
            sweetAlertDialog.showDialogError("يجب التحقق من تاريخ اليوم");
        }
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
            addDataNoHafez(Notes_No_hafez);
        } else {
            if (Objects.requireNonNull(ed_Notes.getText()).toString().trim().isEmpty()) {
                Notes_No_hafez = "لا توجد ملاحظات";
                addDataNoHafez(Notes_No_hafez);
            } else if (Objects.requireNonNull(edt_Date.getText()).toString().isEmpty()) {
                sweetAlertDialog.showDialogError("يجب التحقق من تاريخ اليوم");
            }
        }
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
        spinner_aya_start.setSelection(0);
        spinner_aya_end.setSelection(0);
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
                        addDataMoragea();
                    }
                } else {
                    sweetAlertDialog.showDialogError("عذرا عدد أيات سورة " + sp_surah_end_Murajaea.getSelectedItem().toString() + " " + arrayOfAyaNumber[sp_surah_end_Murajaea.getSelectedItemPosition()]);
                }
            } else {
                if (Integer.parseInt(edt_aya_start_Murajaea.getText().toString()) <= arrayOfAyaNumber[sp_surah_start_Murajaea.getSelectedItemPosition()]) {
                    if (Integer.parseInt(edt_aya_end_Murajaea.getText().toString()) <= arrayOfAyaNumber[sp_surah_end_Murajaea.getSelectedItemPosition()]) {
                        addDataMoragea();
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
        if (spinner_aya_start.getSelectedItemPosition() != 0 &&
                spinner_aya_end.getSelectedItemPosition() != 0 &&
                !Objects.requireNonNull(ed_aya_start.getText()).toString().isEmpty() &&
                !Objects.requireNonNull(ed_aya_end.getText()).toString().isEmpty() &&
                evaluation_student_hefez != null &&
                !evaluation_student_hefez.isEmpty() &&
                !Objects.requireNonNull(edt_Date.getText()).toString().isEmpty()) {
            if (spinner_aya_start.getSelectedItem().toString()
                    .equals(spinner_aya_end.getSelectedItem().toString())) {
                if (Integer.parseInt(ed_aya_start.getText().toString()) <= arrayOfAyaNumber[spinner_aya_start.getSelectedItemPosition()]
                        && Integer.parseInt(ed_aya_end.getText().toString()) <= arrayOfAyaNumber[spinner_aya_end.getSelectedItemPosition()]) {
                    if (Integer.parseInt(ed_aya_start.getText().toString()) >=
                            Integer.parseInt(ed_aya_end.getText().toString())) {
                        sweetAlertDialog.showDialogError("يجب أن يكون رقم أية البداية أقل من رقم أية النهاية");
                    } else {
                        addDataHefez();
                    }
                } else {
                    sweetAlertDialog.showDialogError("عذرا عدد أيات سورة " + spinner_aya_start.getSelectedItem().toString() + " " + arrayOfAyaNumber[spinner_aya_start.getSelectedItemPosition()]);
                }
            } else {
                if (Integer.parseInt(ed_aya_start.getText().toString()) <= arrayOfAyaNumber[spinner_aya_start.getSelectedItemPosition()]) {
                    if (Integer.parseInt(ed_aya_end.getText().toString()) <= arrayOfAyaNumber[spinner_aya_end.getSelectedItemPosition()]) {
                        addDataHefez();
                    } else {
                        sweetAlertDialog.showDialogError("عذرا عدد أيات سورة " + spinner_aya_end.getSelectedItem().toString() + " " + arrayOfAyaNumber[spinner_aya_end.getSelectedItemPosition()]);
                    }
                } else {
                    sweetAlertDialog.showDialogError("عذرا عدد أيات سورة " + spinner_aya_start.getSelectedItem().toString() + " " + arrayOfAyaNumber[spinner_aya_start.getSelectedItemPosition()]);
                }

            }
        } else {
            if (spinner_aya_start.getSelectedItemPosition() == 0) {
                sweetAlertDialog.showDialogError("يجب اختيار سورة البداية");
            } else if (spinner_aya_end.getSelectedItemPosition() == 0) {
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

    private void addDataHefez() {
        db.collection("DailyReport")
                .whereEqualTo("uid", UID)
                .whereEqualTo("day", day)
                .whereEqualTo("month", month)
                .whereEqualTo("year", year)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null) {
                if (queryDocumentSnapshots.isEmpty()) {
                    String PushId = db.collection("DailyReport").document().getId();
                    DailyReport dailyReport = new DailyReport(PushId, UID, Common.currentGroupId, spinner_aya_start.getSelectedItem().toString(),
                            spinner_aya_start.getSelectedItem().toString(), status_student, status_present, evaluation_student_hefez, day, month
                            , year, "لا توجد ملاحظات", dayOfWeek, Integer.parseInt(Objects.requireNonNull(ed_aya_start.getText()).toString())
                            , Integer.parseInt(Objects.requireNonNull(ed_aya_end.getText()).toString()));
                    db.collection("DailyReport").document(PushId).set(dailyReport);
                    sweetAlertDialog.showDialogSuccess("OK", "تمت اضافة الحفظ اليومي للطالب بنجاح !")
                            .setConfirmButton("OK", sweetAlertDialog -> {
                                sweetAlertDialog.dismissWithAnimation();
                                clearInputs_Hefea();
                                finish();
                            });
                } else {
                    sweetAlertDialog.showDialogError("التاريخ المدخل موجود مسبقا !");
                }
            }
        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
    }

    private void addDataMoragea() {
        db.collection("DailyReport")
                .whereEqualTo("uid", UID)
                .whereEqualTo("day", day)
                .whereEqualTo("month", month)
                .whereEqualTo("year", year)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null) {
                if (queryDocumentSnapshots.isEmpty()) {
                    String PushId = db.collection("DailyReport").document().getId();
                    DailyReport dailyReport = new DailyReport(PushId, UID, Common.currentGroupId, sp_surah_start_Murajaea.getSelectedItem().toString(),
                            sp_surah_end_Murajaea.getSelectedItem().toString(), status_student, status_present, evaluation_student_hefez
                            , day, month
                            , year, "لا توجد ملاحظات",
                            dayOfWeek,
                            Integer.parseInt(Objects.requireNonNull(edt_aya_start_Murajaea.getText()).toString())
                            , Integer.parseInt(Objects.requireNonNull(edt_aya_end_Murajaea.getText()).toString()));
                    db.collection("DailyReport").document(PushId).set(dailyReport);
                    sweetAlertDialog.showDialogSuccess("OK", "تمت اضافة المراجعة للطالب بنجاح !")
                            .setConfirmButton("OK", sweetAlertDialog -> {
                                sweetAlertDialog.dismissWithAnimation();
                                clearInputs_Murajaea();
                                finish();
                            })
                            .show();
                } else {
                    sweetAlertDialog.showDialogError("التاريخ المدخل موجود مسبقا !");
                }
            }
        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
    }

    private void addDataNoHafez(String notes_No_hafez) {
        db.collection("DailyReport")
                .whereEqualTo("uid", UID)
                .whereEqualTo("day", day)
                .whereEqualTo("month", month)
                .whereEqualTo("year", year)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null) {
                if (queryDocumentSnapshots.isEmpty()) {
                    String PushId = db.collection("DailyReport").document().getId();
                    DailyReport dailyReport = new DailyReport(PushId, UID, Common.currentGroupId, status_student, status_present, day, month,
                            year, notes_No_hafez, dayOfWeek);
                    db.collection("DailyReport").document(PushId).set(dailyReport);
                    sweetAlertDialog.showDialogSuccess("OK", "تمت اضافة الحفظ اليومي للطالب بنجاح !")
                            .setConfirmButton("OK", sweetAlertDialog -> finish())
                            .show();
                } else {
                    sweetAlertDialog.showDialogError("التاريخ المدخل موجود مسبقا !");
                }
            }
        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
    }

    private void addDataApsent(String notes_No_hafez) {
        db.collection("DailyReport")
                .whereEqualTo("uid", UID)
                .whereEqualTo("day", day)
                .whereEqualTo("month", month)
                .whereEqualTo("year", year)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null) {
                if (queryDocumentSnapshots.isEmpty()) {
                    String PushId = db.collection("DailyReport").document().getId();
                    DailyReport dailyReport = new DailyReport(PushId, UID, Common.currentGroupId, status_student, status_present, day, month, year, notes_No_hafez, dayOfWeek);
                    db.collection("DailyReport").document(PushId).set(dailyReport);
                    sweetAlertDialog.showDialogSuccess("OK", "تمت اضافة الحفظ اليومي للطالب بنجاح !")
                            .setConfirmButton("OK", sweetAlertDialog -> finish())
                            .show();
                } else {
                    sweetAlertDialog.showDialogError("التاريخ المدخل موجود مسبقا !");
                }
            }
        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
    }

    private void addDataAuthorized(String notes_authorized) {
        db.collection("DailyReport")
                .whereEqualTo("uid", UID)
                .whereEqualTo("day", day)
                .whereEqualTo("month", month)
                .whereEqualTo("year", year)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null) {
                if (queryDocumentSnapshots.isEmpty()) {
                    String PushId = db.collection("DailyReport").document().getId();
                    DailyReport dailyReport = new DailyReport(PushId, UID, Common.currentGroupId, status_student, status_present, day, month, year, notes_authorized, dayOfWeek);
                    db.collection("DailyReport").document(PushId).set(dailyReport);
                    sweetAlertDialog.showDialogSuccess("OK", "تمت اضافة الحفظ اليومي للطالب بنجاح !")
                            .setConfirmButton("OK", sweetAlertDialog -> {
                                sweetAlertDialog.dismissWithAnimation();
                                finish();
                            })
                            .show();
                } else {
                    sweetAlertDialog.showDialogError("التاريخ المدخل موجود مسبقا !");
                }
            }
        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
    }

    private void InitializeFields() {
        spinner_aya_start = findViewById(R.id.sp_surah_start);
        spinner_aya_end = findViewById(R.id.sp_surah_end);
        ed_Notes = findViewById(R.id.edt_notes);
        ed_aya_start = findViewById(R.id.edt_aya_start);
        ed_aya_end = findViewById(R.id.edt_aya_end);
        rg_status_hafez = findViewById(R.id.radio_group_hafiz_student);
        rg_evaluation_student = findViewById(R.id.radio_group_evaluation_student);
        tv_evaluation_student = findViewById(R.id.tv_evaluation_student);
        spinner_aya_start.setTitle("اختر سورة البداية");
        spinner_aya_end.setTitle("اختر سورة النهاية");
        rg_status_student = findViewById(R.id.radio_group_status_student);
        add_daily_memorization = findViewById(R.id.btn_add_daily_memorization);
        edt_Date = findViewById(R.id.edt_time);
        checkbox_date_default = findViewById(R.id.checkbox_date_default);
        sp_surah_start_Murajaea = findViewById(R.id.sp_surah_start_Murajaea);
        sp_surah_end_Murajaea = findViewById(R.id.sp_surah_end_Murajaea);
        edt_aya_start_Murajaea = findViewById(R.id.edt_aya_start_Murajaea);
        edt_aya_end_Murajaea = findViewById(R.id.edt_aya_end_Murajaea);
        img_profile = findViewById(R.id.img_profile);
        tv_Person_name = findViewById(R.id.tv_Person_name);
        arrayOfAyaNumber = getResources().getIntArray(R.array.ayat_array);
        db = FirebaseFirestore.getInstance();
        sweetAlertDialog = new SweetAlertDialog_(this);
    }

    @SuppressLint("SetTextI18n")
    private void showDialogPickerDate() {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("EEE");
        dayOfWeek = df.format(calendar.getTime());
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
}
