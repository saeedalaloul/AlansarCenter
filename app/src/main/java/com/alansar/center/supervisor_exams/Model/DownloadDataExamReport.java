package com.alansar.center.supervisor_exams.Model;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.alansar.center.Common.Common;
import com.alansar.center.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DownloadDataExamReport {
    private static final String ID = "com.alansar.center.Downloads";
    private Context context;
    private ArrayList<ReportExam> reportExams;
    private HashMap<String, String> StudentsNames, partsExams,
            dateOfExams, mohafezsNames, testersNames, stages, notes;
    private HashMap<String, HashMap<String, Double>> marksExamQuestions;
    private HashMap<String, Double> averages;
    private HashMap<String, Integer> countExamQuestions;
    private ArrayList<String> examsIds;
    private List<String> Columns;
    private NotificationManager notificationManager;
    private int type;

    public DownloadDataExamReport(Context context, List<String> Columns, FirebaseFirestore db, String mohafezId, String stage) {
        this.Columns = Columns;
        this.context = context;
        if (!Columns.isEmpty()) {
            reportExams = new ArrayList<>();
            StudentsNames = new HashMap<>();
            partsExams = new HashMap<>();
            dateOfExams = new HashMap<>();
            mohafezsNames = new HashMap<>();
            testersNames = new HashMap<>();
            stages = new HashMap<>();
            notes = new HashMap<>();
            marksExamQuestions = new HashMap<>();
            averages = new HashMap<>();
            countExamQuestions = new HashMap<>();
            examsIds = new ArrayList<>();

            if (stage != null) {
                if (mohafezId != null) {
                    getDataReportExamByGroupFromDB(db, stage, mohafezId);
                    type = 2;
                } else {
                    getDataReportExamByStageFromDB(db, stage);
                    type = 1;
                }
            } else {
                getDataReportExamFromDB(db);
                type = 0;
            }
        }
    }

    private void getDataReportExamByStageFromDB(FirebaseFirestore db, String stage) {
        if (db != null && stage != null) {
            db.collection("Exam")
                    .whereEqualTo("statusAcceptance", 3)
                    .whereEqualTo("stage", stage)
                    .get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                        Exam exam = queryDocumentSnapshots.getDocuments().get(i).toObject(Exam.class);
                        if (exam != null) {
                            examsIds.add(exam.getId());
                            getStudentsNames(exam.getIdStudent(), exam.getId(), db);
                            getTestersNames(exam.getIdTester(), exam.getId(), db);
                            getMohafezNames(exam.getIdMohafez(), exam.getId(), db);
                            partsExams.put("" + exam.getId(), exam.getExamPart());
                            dateOfExams.put("" + exam.getId(), exam.getDay() + "/" + exam.getMonth() + "/" + exam.getYear());
                            countExamQuestions.put("" + exam.getId(), exam.getMarksExamQuestions().size());
                            marksExamQuestions.put("" + exam.getId(), exam.getMarksExamQuestions());
                            notes.put("" + exam.getId(), exam.getNotes());
                            averages.put("" + exam.getId(), calcMarksExam(exam.getMarksExamQuestions()));
                        }
                    }
                }
            }).addOnFailureListener(e -> Log.d("sss", "Error :" + e.getLocalizedMessage()));
            designReportOfListExam();
        }
    }

    private void getDataReportExamByGroupFromDB(FirebaseFirestore db, String stage, String mohafezId) {
        if (db != null && stage != null && mohafezId != null) {
            db.collection("Exam")
                    .whereEqualTo("statusAcceptance", 3)
                    .whereEqualTo("stage", stage)
                    .whereEqualTo("idMohafez", mohafezId)
                    .get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                        Exam exam = queryDocumentSnapshots.getDocuments().get(i).toObject(Exam.class);
                        if (exam != null) {
                            examsIds.add(exam.getId());
                            getStudentsNames(exam.getIdStudent(), exam.getId(), db);
                            getTestersNames(exam.getIdTester(), exam.getId(), db);
                            getMohafezNames(exam.getIdMohafez(), exam.getId(), db);
                            partsExams.put("" + exam.getId(), exam.getExamPart());
                            dateOfExams.put("" + exam.getId(), exam.getDay() + "/" + exam.getMonth() + "/" + exam.getYear());
                            countExamQuestions.put("" + exam.getId(), exam.getMarksExamQuestions().size());
                            marksExamQuestions.put("" + exam.getId(), exam.getMarksExamQuestions());
                            notes.put("" + exam.getId(), exam.getNotes());
                            averages.put("" + exam.getId(), calcMarksExam(exam.getMarksExamQuestions()));
                        }
                    }
                } else {
                    Log.d("sss", "this");
                }
            }).addOnFailureListener(e -> Log.d("sss", "Error :" + e.getLocalizedMessage()));
            designReportOfListExam();
        }
    }

    private void getDataReportExamFromDB(FirebaseFirestore db) {
        if (db != null) {
            db.collection("Exam")
                    .whereEqualTo("statusAcceptance", 3)
                    .get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                        Exam exam = queryDocumentSnapshots.getDocuments().get(i).toObject(Exam.class);
                        if (exam != null) {
                            examsIds.add(exam.getId());
                            getStudentsNames(exam.getIdStudent(), exam.getId(), db);
                            getTestersNames(exam.getIdTester(), exam.getId(), db);
                            getMohafezNames(exam.getIdMohafez(), exam.getId(), db);
                            partsExams.put("" + exam.getId(), exam.getExamPart());
                            dateOfExams.put("" + exam.getId(), exam.getDay() + "/" + exam.getMonth() + "/" + exam.getYear());
                            countExamQuestions.put("" + exam.getId(), exam.getMarksExamQuestions().size());
                            marksExamQuestions.put("" + exam.getId(), exam.getMarksExamQuestions());
                            notes.put("" + exam.getId(), exam.getNotes());
                            averages.put("" + exam.getId(), calcMarksExam(exam.getMarksExamQuestions()));
                        }
                    }
                }
            }).addOnFailureListener(e -> Log.d("sss", "Error :" + e.getLocalizedMessage()));
            designReportOfListExam();
        }
    }

    private void designReportOfListExam() {
        new Handler().postDelayed(() -> {
            if (!examsIds.isEmpty()) {
                if (examsIds.size() == StudentsNames.size()
                        && examsIds.size() == stages.size()
                        && examsIds.size() == testersNames.size()
                        && examsIds.size() == mohafezsNames.size()
                        && examsIds.size() == partsExams.size()
                        && examsIds.size() == dateOfExams.size()
                        && examsIds.size() == countExamQuestions.size()
                        && examsIds.size() == marksExamQuestions.size()
                        && examsIds.size() == notes.size()
                        && examsIds.size() == averages.size()) {
                    for (int i = 0; i < examsIds.size(); i++) {
                        reportExams.add(new ReportExam(StudentsNames.get(examsIds.get(i)),
                                partsExams.get(examsIds.get(i)), dateOfExams.get(examsIds.get(i)),
                                mohafezsNames.get(examsIds.get(i)), testersNames.get(examsIds.get(i)),
                                stages.get(examsIds.get(i)), notes.get(examsIds.get(i)),
                                countExamQuestions.get(examsIds.get(i)), marksExamQuestions.get(examsIds.get(i)),
                                averages.get(examsIds.get(i))));
                    }
                    if (reportExams.size() > 0 && reportExams.size() == examsIds.size()) {
                        new ReportDesignExam(new XSSFWorkbook(), reportExams, Columns, type);
                        createNotification();
                    }
                } else {
                    Toast.makeText(context, "حدث خطأ في تحميل البيانات , راجع إدارة التطبيق !", Toast.LENGTH_SHORT).show();
                }
            }
        }, 2000);
    }

    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ReportDesignExam.file != null) {
                String fileName = ReportDesignExam.file.getName().substring(0, ReportDesignExam.file.getName().indexOf("."));
                NotificationCompat.Builder notification = new NotificationCompat.Builder(context, ID)
                        .setContentTitle("تحميل قاعدة بيانات للإختبارات")
                        .setContentText("تم الإنتهاء من إعداد " + fileName + "  ولفتح التقرير اضغط هنا ...")
                        .setColor(Color.GREEN)
                        .setOnlyAlertOnce(true)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("تم الإنتهاء من إعداد " + fileName + " ,لفتح التقرير اضغط هنا ..."))
                        .setSmallIcon(R.drawable.logo_ansar)
                        .setChannelId(ID)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_ansar));
                Uri uri;
                uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", ReportDesignExam.file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(uri, "application/vnd.ms-excel");
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                notification.setContentIntent(pendingIntent);
                Common.getNotificationManager(notificationManager, context).notify(Timestamp.now().getNanoseconds(), notification.build());
            }
        }
    }

    private void getStudentsNames(String idStudent, String idExam, FirebaseFirestore db) {
        if (idStudent != null) {
            db.collection("Student")
                    .document(idStudent)
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    StudentsNames.put("" + idExam, documentSnapshot.getString("name"));
                    stages.put("" + idExam, documentSnapshot.getString("stage"));
                }
            }).addOnFailureListener(e -> Log.d("sss", "Error :" + e.getLocalizedMessage()));
        }
    }

    private void getTestersNames(String idTester, String idExam, FirebaseFirestore db) {
        if (idTester != null) {
            db.collection("Tester")
                    .document(idTester)
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    testersNames.put("" + idExam, documentSnapshot.getString("name"));
                }
            }).addOnFailureListener(e -> Log.d("sss", "Error :" + e.getLocalizedMessage()));
        }
    }

    private void getMohafezNames(String idMohafez, String idExam, FirebaseFirestore db) {
        if (idMohafez != null) {
            db.collection("Mohafez")
                    .document(idMohafez)
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    mohafezsNames.put("" + idExam, documentSnapshot.getString("name"));
                }
            }).addOnFailureListener(e -> Log.d("sss", "Error :" + e.getLocalizedMessage()));
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
        }
        return 0;
    }

    private double round(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
