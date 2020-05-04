package com.alansar.center.supervisor_exams.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.alansar.center.Common.Common;
import com.alansar.center.R;
import com.alansar.center.SweetAlertDialog_;
import com.alansar.center.supervisor_exams.Model.ExamsSettings;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;


public class ExamsSettingsFragment extends Fragment {
    private FirebaseFirestore db;
    private MaterialCheckBox ch_data_allow_testers, checkbox_data_default;
    private Spinner sp_mix_questions_exam, sp_min_questions_exam,
            sp_number_questions_exam_part, sp_number_days_order_exam;
    private String[] numberQuestions, numberDays;
    private ExamsSettings examsSettings;
    private SweetAlertDialog_ sweetAlertDialog;
    private ListenerRegistration registration;


    public ExamsSettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_supervisor_exams_exams_settings, container, false);
        Initialization(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        ch_data_allow_testers.setOnCheckedChangeListener((compoundButton, b) -> setAllowTestersUpdateExam(b));

        sp_mix_questions_exam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (sp_mix_questions_exam.getSelectedItemPosition() != 0) {
                    if (Integer.parseInt(sp_mix_questions_exam.getSelectedItem().toString()) >=
                            Integer.parseInt(sp_min_questions_exam.getSelectedItem().toString())) {
                        setMaxQuestionsUpdateExam(Integer.parseInt(sp_mix_questions_exam.getSelectedItem().toString()));
                    } else {
                        sweetAlertDialog.showDialogError("يجب أن يكون أدنى عدد أسئلة أقل أو يساوي أقصى عدد أسئلة");
                        if (examsSettings != null) {
                            for (int j = 0; j < numberQuestions.length; j++) {
                                if (examsSettings.getMaxQuestionsExam() == Integer.parseInt(numberQuestions[j])) {
                                    sp_mix_questions_exam.setSelection(j);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_min_questions_exam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (sp_min_questions_exam.getSelectedItemPosition() != 0) {
                    if (Integer.parseInt(sp_mix_questions_exam.getSelectedItem().toString()) >=
                            Integer.parseInt(sp_min_questions_exam.getSelectedItem().toString())) {
                        setMinQuestionsUpdateExam(Integer.parseInt(sp_min_questions_exam.getSelectedItem().toString()));
                    } else {
                        sweetAlertDialog.showDialogError("يجب أن يكون أدنى عدد أسئلة أقل أو يساوي أقصى عدد أسئلة");
                        if (examsSettings != null) {
                            for (int j = 0; j < numberQuestions.length; j++) {
                                if (examsSettings.getMinQuestionsExam() == Integer.parseInt(numberQuestions[j])) {
                                    sp_min_questions_exam.setSelection(j);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_number_questions_exam_part.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (sp_number_questions_exam_part.getSelectedItemPosition() != 0) {
                    setNumberQuestionsExam(Integer.parseInt(sp_number_questions_exam_part.getSelectedItem().toString()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_number_days_order_exam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (sp_number_days_order_exam.getSelectedItemPosition() != 0) {
                    setNumberDaysOrdersExam(Integer.parseInt(sp_number_days_order_exam.getSelectedItem().toString()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        checkbox_data_default.setOnCheckedChangeListener((compoundButton, b) -> setDataDefaultFromDB(b));

        getDataFromDB();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (registration != null) {
            registration.remove();
        }
    }

    private void getDataFromDB() {
        registration = db.collection("ExamsSettings")
                .document(Common.currentPerson.getId())
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        Log.w("sss", "listen:error" + e.getLocalizedMessage());
                        return;
                    }
                    if (documentSnapshot != null) {
                        if (documentSnapshot.exists()) {
                            examsSettings = documentSnapshot.toObject(ExamsSettings.class);
                            if (examsSettings != null) {
                                if (examsSettings.isDefaultData()) {
                                    resetData();
                                    checkbox_data_default.setChecked(true);
                                } else {
                                    setDataFields(examsSettings);
                                }
                            }
                        } else {
                            resetData();
                            setDefaultDataFromDB();
                            if (sweetAlertDialog != null) {
                                sweetAlertDialog.cancelDialog();
                            }
                        }

                    }
                });
    }

    private void setDataFields(ExamsSettings examsSettings) {
        checkbox_data_default.setChecked(false);
        if (examsSettings.isUpdateExam()) {
            ch_data_allow_testers.setChecked(true);
        } else {
            ch_data_allow_testers.setChecked(false);
        }
        for (int i = 0; i < numberQuestions.length; i++) {
            if (examsSettings.getMaxQuestionsExam() == Integer.parseInt(numberQuestions[i])) {
                sp_mix_questions_exam.setSelection(i);
            }
            if (examsSettings.getMinQuestionsExam() == Integer.parseInt(numberQuestions[i])) {
                sp_min_questions_exam.setSelection(i);
            }
            if (examsSettings.getNumberQuestionsPart() == Integer.parseInt(numberQuestions[i])) {
                sp_number_questions_exam_part.setSelection(i);
            }
        }

        for (int i = 0; i < numberDays.length; i++) {
            if (examsSettings.getNumberOrdersExamDay() == Integer.parseInt(numberDays[i])) {
                sp_number_days_order_exam.setSelection(i);
            }
        }
    }

    private void setDataDefaultFromDB(boolean b) {
        if (b) {
            resetData();
            setDefaultDataFromDB();

        } else {
            UnResetData();
        }
    }

    private void setDefaultDataFromDB() {
        db.collection("ExamsSettings")
                .document(Common.currentPerson.getId())
                .set(new ExamsSettings(false,
                        true, Integer.parseInt(sp_mix_questions_exam.getSelectedItem().toString()),
                        Integer.parseInt(sp_min_questions_exam.getSelectedItem().toString()),
                        Integer.parseInt(sp_number_questions_exam_part.getSelectedItem().toString()),
                        Integer.parseInt(sp_number_days_order_exam.getSelectedItem().toString())));
    }

    private void UnResetData() {
        ch_data_allow_testers.setEnabled(true);
        sp_mix_questions_exam.setEnabled(true);
        sp_min_questions_exam.setEnabled(true);
        sp_number_questions_exam_part.setEnabled(true);
        sp_number_days_order_exam.setEnabled(true);
        db.collection("ExamsSettings").document(Common.currentPerson.getId())
                .update("defaultData", false);
    }

    private void resetData() {
        ch_data_allow_testers.setChecked(false);
        ch_data_allow_testers.setEnabled(false);
        sp_mix_questions_exam.setSelection(4);
        sp_min_questions_exam.setSelection(4);
        sp_number_days_order_exam.setSelection(14);
        sp_number_questions_exam_part.setSelection(7);
        sp_mix_questions_exam.setEnabled(false);
        sp_min_questions_exam.setEnabled(false);
        sp_number_questions_exam_part.setEnabled(false);
        sp_number_days_order_exam.setEnabled(false);
    }

    private void setNumberDaysOrdersExam(int NumberDay) {
        ExamsSettings examsSettings = new ExamsSettings();
        examsSettings.setNumberOrdersExamDay(NumberDay);
        db.collection("ExamsSettings")
                .document(Common.currentPerson.getId())
                .get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                documentSnapshot.getReference().update("numberOrdersExamDay", NumberDay);
            } else {
                documentSnapshot.getReference().set(examsSettings);
            }
        });
    }

    private void setNumberQuestionsExam(int NumberQuestions) {
        ExamsSettings examsSettings = new ExamsSettings();
        examsSettings.setNumberQuestionsPart(NumberQuestions);
        db.collection("ExamsSettings")
                .document(Common.currentPerson.getId())
                .get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                documentSnapshot.getReference().update("numberQuestionsPart", NumberQuestions);
            } else {
                documentSnapshot.getReference().set(examsSettings);
            }
        });
    }

    private void setMaxQuestionsUpdateExam(int maxQuestions) {
        ExamsSettings examsSettings = new ExamsSettings();
        examsSettings.setMaxQuestionsExam(maxQuestions);
        db.collection("ExamsSettings")
                .document(Common.currentPerson.getId())
                .get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                documentSnapshot.getReference().update("maxQuestionsExam", maxQuestions);
            } else {
                documentSnapshot.getReference().set(examsSettings);
            }
        });
    }

    private void setMinQuestionsUpdateExam(int minQuestions) {
        ExamsSettings examsSettings = new ExamsSettings();
        examsSettings.setMinQuestionsExam(minQuestions);
        db.collection("ExamsSettings")
                .document(Common.currentPerson.getId())
                .get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                documentSnapshot.getReference().update("minQuestionsExam", minQuestions);
            } else {
                documentSnapshot.getReference().set(examsSettings);
            }
        });
    }

    private void setAllowTestersUpdateExam(boolean b) {
        ExamsSettings examsSettings = new ExamsSettings();
        examsSettings.setUpdateExam(b);
        db.collection("ExamsSettings")
                .document(Common.currentPerson.getId())
                .get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                documentSnapshot.getReference().update("updateExam", b);
            } else {
                documentSnapshot.getReference().set(examsSettings);
            }
        });
    }

    private void Initialization(View view) {
        ch_data_allow_testers = view.findViewById(R.id.checkbox_data_allow_testers);
        sp_mix_questions_exam = view.findViewById(R.id.sp_mix_questions_exam);
        sp_min_questions_exam = view.findViewById(R.id.sp_min_questions_exam);
        sp_number_questions_exam_part = view.findViewById(R.id.sp_number_questions_exam_part);
        sp_number_days_order_exam = view.findViewById(R.id.sp_number_days_order_exam);
        checkbox_data_default = view.findViewById(R.id.checkbox_data_default);
        numberQuestions = getResources().getStringArray(
                R.array.number_questions);
        numberDays = getResources().getStringArray(
                R.array.number_days);
        db = FirebaseFirestore.getInstance();
        sweetAlertDialog = new SweetAlertDialog_(getContext());
    }
}
