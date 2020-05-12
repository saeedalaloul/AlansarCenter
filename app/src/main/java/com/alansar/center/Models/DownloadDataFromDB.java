package com.alansar.center.Models;

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
import com.alansar.center.Mohafez.Model.CalcStudentClass;
import com.alansar.center.R;
import com.alansar.center.supervisor_exams.Model.Exam;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DownloadDataFromDB {
    private static final String ID = "com.alansar.center.Downloads";
    private Context context;
    private ArrayList<ReportDatabase> reportDatabases;
    private HashMap<String, String> StudentsNames, StudentsClass,
            StudentsDateOfBirth,
            StudentsStage, StudentsMohafezName;
    private HashMap<String, Integer> StudentsAge, StudentsTotalConservation,
            StudentsIdentificationNumber, StudentsYearOfBirth, StudentsPhoneNumber;
    private String[] Values;
    private ArrayList<String> StudentsIds;
    private List<String> Columns;
    private NotificationManager notificationManager;
    private int type;


    public DownloadDataFromDB(Context context, List<String> Columns, FirebaseFirestore db, String groupId, String stage) {
        this.Columns = Columns;
        this.context = context;
        if (!Columns.isEmpty()) {
            Values = context.getResources().getStringArray(
                    R.array.parts_quran);
            StudentsNames = new HashMap<>();
            StudentsIds = new ArrayList<>();
            StudentsClass = new HashMap<>();
            StudentsAge = new HashMap<>();
            StudentsYearOfBirth = new HashMap<>();
            StudentsDateOfBirth = new HashMap<>();
            StudentsPhoneNumber = new HashMap<>();
            StudentsIdentificationNumber = new HashMap<>();
            StudentsStage = new HashMap<>();
            StudentsMohafezName = new HashMap<>();
            StudentsTotalConservation = new HashMap<>();
            reportDatabases = new ArrayList<>();
            if (stage != null) {
                if (groupId != null) {
                    getStudentsNamesFromStageAndGroup(db, stage, groupId);
                    type = 2;
                } else {
                    getStudentsNamesFromStage(db, stage);
                    type = 1;
                }
            } else {
                getStudentsNames(db);
                type = 0;
            }
        }
    }

    private void getStudentsNamesFromStageAndGroup(FirebaseFirestore db, String stage, String groupId) {
        if (db != null && stage != null && groupId != null) {
            db.collection("Student")
                    .whereEqualTo("stage", stage)
                    .whereEqualTo("groupId", groupId)
                    .get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                        StudentsNames.put(queryDocumentSnapshots.getDocuments().get(i).getId(), queryDocumentSnapshots.getDocuments().get(i).getString("name"));
                        StudentsIds.add(queryDocumentSnapshots.getDocuments().get(i).getId());
                        StudentsStage.put(queryDocumentSnapshots.getDocuments().get(i).getId(), queryDocumentSnapshots.getDocuments().get(i).getString("stage"));
                        getMohafezNames(queryDocumentSnapshots.getDocuments().get(i).getString("groupId"), db, queryDocumentSnapshots.getDocuments().get(i).getId());
                    }
                }
            }).addOnFailureListener(e -> Log.d("sss", "Error :" + e.getLocalizedMessage()));
        }
    }

    private void getStudentsNamesFromStage(FirebaseFirestore db, String stage) {
        db.collection("Student")
                .whereEqualTo("stage", stage)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                    StudentsNames.put(queryDocumentSnapshots.getDocuments().get(i).getId(), queryDocumentSnapshots.getDocuments().get(i).getString("name"));
                    StudentsIds.add(queryDocumentSnapshots.getDocuments().get(i).getId());
                    StudentsStage.put(queryDocumentSnapshots.getDocuments().get(i).getId(), queryDocumentSnapshots.getDocuments().get(i).getString("stage"));
                    getMohafezNames(queryDocumentSnapshots.getDocuments().get(i).getString("groupId"), db, queryDocumentSnapshots.getDocuments().get(i).getId());
                }
            }
        }).addOnFailureListener(e -> Log.d("sss", "Error :" + e.getLocalizedMessage()));
    }

    private void getStudentsNames(FirebaseFirestore db) {
        db.collection("Student")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                    StudentsNames.put(queryDocumentSnapshots.getDocuments().get(i).getId(), queryDocumentSnapshots.getDocuments().get(i).getString("name"));
                    StudentsIds.add(queryDocumentSnapshots.getDocuments().get(i).getId());
                    StudentsStage.put(queryDocumentSnapshots.getDocuments().get(i).getId(), queryDocumentSnapshots.getDocuments().get(i).getString("stage"));
                    getMohafezNames(queryDocumentSnapshots.getDocuments().get(i).getString("groupId"), db, queryDocumentSnapshots.getDocuments().get(i).getId());
                }
            }
        }).addOnFailureListener(e -> Log.d("sss", "Error :" + e.getLocalizedMessage()));
    }

    private void getMohafezNames(String groupId, FirebaseFirestore db, String id) {
        if (groupId != null && id != null) {
            db.collection("Mohafez")
                    .whereEqualTo("groupId", groupId)
                    .limit(1)
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot != null && !documentSnapshot.isEmpty()) {
                    StudentsMohafezName.put(id, documentSnapshot.getDocuments().get(0).getString("name"));
                    getTotalConservationFromDB(id, documentSnapshot.getDocuments().get(0).getId(), db);
                    getDateOfBirth(db, id);
                }
            }).addOnFailureListener(e -> Log.d("sss", "Error :" + e.getLocalizedMessage()));
        }
    }

    private void getTotalConservationFromDB(String StudentId, String MohafezId, FirebaseFirestore db) {
        if (StudentId != null && MohafezId != null && db != null) {
            db.collection("Exam")
                    .whereEqualTo("idStudent", StudentId)
                    .whereEqualTo("idMohafez", MohafezId)
                    .whereEqualTo("statusAcceptance", 3)
                    .orderBy("year", Query.Direction.DESCENDING)
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
                                int countPart = 31 - j;
                                if (j == 30) {
                                    StudentsTotalConservation.put(StudentId, 1);
                                } else {
                                    StudentsTotalConservation.put(StudentId, countPart);
                                }
                            }
                        }
                    } else if (result < 80.00) {
                        for (int j = 0; j < Values.length; j++) {
                            if (examPart.equals(Values[j])) {
                                int countPart = 31 - (j + 1);
                                if (countPart == -1) {
                                    StudentsTotalConservation.put(StudentId, 0);
                                } else {
                                    StudentsTotalConservation.put(StudentId, countPart);
                                }
                            }
                        }
                    }
                } else {
                    StudentsTotalConservation.put(StudentId, 0);
                }
            }).addOnFailureListener(e -> Log.d("sss", "Error :" + e.getLocalizedMessage()));

            new Handler().postDelayed(() -> {
                if (!StudentsIds.isEmpty()
                        && StudentsIds.size() == StudentsNames.size()
                        && StudentsIds.size() == StudentsClass.size()
                        && StudentsIds.size() == StudentsStage.size()
                        && StudentsIds.size() == StudentsTotalConservation.size()
                        && StudentsIds.size() == StudentsAge.size()
                        && StudentsIds.size() == StudentsIdentificationNumber.size()
                        && StudentsIds.size() == StudentsDateOfBirth.size()
                        && StudentsIds.size() == StudentsYearOfBirth.size()
                        && StudentsIds.size() == StudentsPhoneNumber.size()
                        && StudentsIds.size() == StudentsMohafezName.size()
                ) {
                    for (int i = 0; i < StudentsIds.size(); i++) {
                        reportDatabases.add(new ReportDatabase(StudentsNames.get(StudentsIds.get(i)),
                                StudentsClass.get(StudentsIds.get(i)), StudentsStage.get(StudentsIds.get(i)),
                                StudentsDateOfBirth.get(StudentsIds.get(i)), StudentsMohafezName.get(StudentsIds.get(i)),
                                StudentsTotalConservation.get(StudentsIds.get(i)), StudentsAge.get(StudentsIds.get(i)),
                                StudentsPhoneNumber.get(StudentsIds.get(i)), StudentsYearOfBirth.get(StudentsIds.get(i)),
                                StudentsIdentificationNumber.get(StudentsIds.get(i))));
                    }
                    if (reportDatabases.size() > 0 && reportDatabases.size() == StudentsIds.size()) {
                        new ReportDesignDatabase(new XSSFWorkbook(), reportDatabases, Columns, type);
                        createNotification();
                    }
                } else {
                    Toast.makeText(context, "حدث خطأ في تحميل البيانات , راجع إدارة التطبيق !", Toast.LENGTH_SHORT).show();
                }
            }, 2000);
        }
    }

    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ReportDesignDatabase.file != null) {
                String fileName = ReportDesignDatabase.file.getName().substring(0, ReportDesignDatabase.file.getName().indexOf("."));
                NotificationCompat.Builder notification = new NotificationCompat.Builder(context, ID)
                        .setContentTitle("تحميل " + fileName)
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
                uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", ReportDesignDatabase.file);
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


    private void getDateOfBirth(FirebaseFirestore db, String id) {
        Calendar calendar = Calendar.getInstance();
        db.collection("Person")
                .document(id)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.exists()) {
                String dob = queryDocumentSnapshots.getString("dob");
                if (dob != null) {
                    int index = dob.lastIndexOf("/");
                    int result = Integer.parseInt(dob.substring(index + 1));
                    int age = calendar.get(Calendar.YEAR) - result;
                    StudentsClass.put(id, CalcStudentClass.calcStudentClass(result));
                    StudentsAge.put(id, age);
                    StudentsYearOfBirth.put(id, result);
                    StudentsDateOfBirth.put(id, dob);
                    StudentsPhoneNumber.put(id, Integer.parseInt("" + queryDocumentSnapshots.getString("phone")));
                    StudentsIdentificationNumber.put(id, Integer.parseInt("" + queryDocumentSnapshots.getString("identificationNumber")));
                }
            }
        }).addOnFailureListener(e -> Log.d("sss", "Error :" + e.getLocalizedMessage()));
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
