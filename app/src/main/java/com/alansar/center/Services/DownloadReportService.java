package com.alansar.center.Services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.alansar.center.Common.Common;
import com.alansar.center.Models.GroupMembers;
import com.alansar.center.Mohafez.Model.CalcStudentClass;
import com.alansar.center.Mohafez.Model.MonthlyReport;
import com.alansar.center.Mohafez.Model.ReportDesign;
import com.alansar.center.R;
import com.alansar.center.supervisor_exams.Model.Exam;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class DownloadReportService extends Service {
    private static final String ID = "com.alansar.center.Downloads";
    public static boolean isRunning;
    private PowerManager.WakeLock wakeLock;
    private int month, year;
    private RequestQueue requestQueue;
    private FirebaseFirestore db;
    private ArrayList<String> groupMembers;
    private HashMap<String, String> StudentsClass, StudentsNames, StartSura, EndSura, StartSuraMoragea, EndSuraMoragea, NotesAndExams;
    private ArrayList<MonthlyReport> reports;
    private String[] Values, Surat;
    private HashMap<String, Integer> StudentsTotalConservation, StartAya1,
            StartAya2, EndAya1, EndAya2, StartAya1Moragea, StartAya2Moragea, EndAya1Moragea, EndAya2Moragea, StudentsAbsentDays;
    private HashMap<String, Double> StudentTotalPagesHefaz, StudentTotalPagesMoragea;
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        Log.d("sss", "onCreate");
        initial();
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "ExampleApp:Wakelock");
        }
        wakeLock.acquire(2 * 60 * 1000L /*10 minutes*/);
        Log.d("sss", "Wakelock acquired");
        createNotification("تجهيز التقرير الشهري", "جاري تحميل البيانات من السيرفر ...", 0);
    }

    private void createNotification(String contentTitle, String contentText, int type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, ID)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setColor(Color.GREEN)
                    .setOnlyAlertOnce(true)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.logo_ansar)
                    .setChannelId(ID)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_ansar));
            if (type == 0) {
                startForeground(1, notification.build());
            } else {
                Uri uri;
                if (ReportDesign.file != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", ReportDesign.file);
                    } else {
                        uri = Uri.fromFile(ReportDesign.file);
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(uri, "application/vnd.ms-excel");
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                    notification.setContentIntent(pendingIntent);
                    getNotificationManager().notify(10, notification.build());
                }
            }
        }
    }

    public NotificationManager getNotificationManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.hasExtra("month")
                    && intent.hasExtra("year")) {
                month = intent.getIntExtra("month", 0);
                year = intent.getIntExtra("year", 0);
                getIdsOfStudentsFromDB(year, month);
                if (requestQueue != null) {
                    final int[] i = {0};
                    i[0] = 1;
                    requestQueue.addRequestFinishedListener(request1 -> {
                        i[0] = i[0]++;
                        new Handler().postDelayed(() -> {
                            if (getBaseContext() != null && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED) {
                                int size = StudentsNames.size();
                                if (size > 0 && size == StudentsClass.size() &&
                                        size == StudentsTotalConservation.size() &&
                                        size == StartSura.size() &&
                                        size == StartSuraMoragea.size() &&
                                        size == EndSura.size() &&
                                        size == EndSuraMoragea.size() &&
                                        size == StartAya1.size() &&
                                        size == StartAya1Moragea.size() &&
                                        size == EndAya2.size() &&
                                        size == EndAya2Moragea.size() &&
                                        size == NotesAndExams.size() &&
                                        size == StudentsAbsentDays.size() &&
                                        size == StudentTotalPagesHefaz.size() &&
                                        size == StudentTotalPagesMoragea.size()) {
                                    for (int i1 = 0; i1 < size; i1++) {
                                        MonthlyReport report = new MonthlyReport();
                                        report.setStudentClass(StudentsClass.get(groupMembers.get(i1)));
                                        report.setStudentName(StudentsNames.get(groupMembers.get(i1)));
                                        report.setTotalConservation(StudentsTotalConservation.get(groupMembers.get(i1)));
                                        if (StartSura.get(groupMembers.get(i1)) != null && !StartSura.get(groupMembers.get(i1)).isEmpty()) {
                                            report.setSuratStart(StartSura.get(groupMembers.get(i1)));
                                        } else {
                                            report.setSuratStart(StartSuraMoragea.get(groupMembers.get(i1)));
                                        }

                                        if (EndSura.get(groupMembers.get(i1)) != null && !EndSura.get(groupMembers.get(i1)).isEmpty()) {
                                            report.setSuratEnd(EndSura.get(groupMembers.get(i1)));
                                        } else {
                                            report.setSuratEnd(EndSuraMoragea.get(groupMembers.get(i1)));
                                        }

                                        if (StartAya1.get(groupMembers.get(i1)) != null && !StartAya1.get(groupMembers.get(i1)).equals(0)) {
                                            int ayaStart1 = StartAya1.get(groupMembers.get(i1));
                                            report.setAyaStart(ayaStart1);
                                        } else {
                                            int ayaStart1 = StartAya1Moragea.get(groupMembers.get(i1));
                                            report.setAyaStart(ayaStart1);
                                        }

                                        if (EndAya2.get(groupMembers.get(i1)) != null && !EndAya2.get(groupMembers.get(i1)).equals(0)) {
                                            report.setAyaEnd(EndAya2.get(groupMembers.get(i1)));
                                        } else {
                                            report.setAyaEnd(EndAya2Moragea.get(groupMembers.get(i1)));
                                        }
                                        report.setNotes(NotesAndExams.get(groupMembers.get(i1)));
                                        if (StudentsAbsentDays != null && StudentsAbsentDays.get(groupMembers.get(i1)) != null) {
                                            int daysAbsence = StudentsAbsentDays.get(groupMembers.get(i1));
                                            report.setDaysAbsence(daysAbsence);
                                        }
                                        double result = roundCount(StudentTotalPagesHefaz.get(groupMembers.get(i1)));
                                        report.setSafahatAlhafz(result);
                                        double result1 = roundCount(StudentTotalPagesMoragea.get(groupMembers.get(i1)));
                                        report.setSafahatAlmurajaea(result1);
                                        reports.add(report);
                                    }
                                }
                                if (reports.size() > 0 && reports.size() == size) {
                                    if (Common.currentPerson != null) {
                                        createNotification("تجهيز التقرير الشهري", "جاري الإنتهاء من إعداد التقري الشهري ...", 0);
                                        String NameMohafez = Common.currentPerson.getFname()
                                                + " " + Common.currentPerson.getMname()
                                                + " " + Common.currentPerson.getLname();
                                        if (month != 0 && year != 0) {
                                            new ReportDesign("" + NameMohafez, new XSSFWorkbook(), month, year, reports);
                                            reports.clear();
                                            stopSelf();
                                        }
                                    }
                                }
                            }
                        }, i[0] * 10000);
                    });
                }
            }
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        Log.d("sss", "onDestroy");
        wakeLock.release();
        Log.d("sss", "Wakelock released");
        createNotification("تجهيز التقرير الشهري", "تم تجهيز التقرير الشهري بنجاح ...", 1);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initial() {
        requestQueue = Volley.newRequestQueue(getBaseContext());
        reports = new ArrayList<>();
        StudentsNames = new HashMap<>();
        StudentsClass = new HashMap<>();
        StudentsTotalConservation = new HashMap<>();
        StartSura = new HashMap<>();
        EndSura = new HashMap<>();
        StartAya1 = new HashMap<>();
        EndAya1 = new HashMap<>();
        StartAya2 = new HashMap<>();
        EndAya2 = new HashMap<>();
        StartSuraMoragea = new HashMap<>();
        EndSuraMoragea = new HashMap<>();
        StartAya1Moragea = new HashMap<>();
        EndAya1Moragea = new HashMap<>();
        StartAya2Moragea = new HashMap<>();
        EndAya2Moragea = new HashMap<>();
        StudentTotalPagesHefaz = new HashMap<>();
        StudentTotalPagesMoragea = new HashMap<>();

        NotesAndExams = new HashMap<>();
        StudentsAbsentDays = new HashMap<>();
        Values = getResources().getStringArray(
                R.array.parts_quran);
        Surat = getResources().getStringArray(
                R.array.surah_array);

        db = FirebaseFirestore.getInstance();
    }

    private void getDateOfBirth() {
        if (groupMembers != null && !groupMembers.isEmpty()) {
            for (int i = 0; i < groupMembers.size(); i++) {
                int finalI = i;
                db.collection("Person")
                        .document(groupMembers.get(i))
                        .get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String dob = documentSnapshot.getString("dob");
                        if (dob != null) {
                            int index = dob.lastIndexOf("/");
                            int result = Integer.parseInt(dob.substring(index + 1));
                            StudentsClass.put(groupMembers.get(finalI), CalcStudentClass.calcStudentClass(result));
                        }
                    }
                });
            }
        }
    }

    private void getStartHefezFromDB(int month, int year) {
        if (groupMembers != null && !groupMembers.isEmpty()) {
            for (int i = 0; i < groupMembers.size(); i++) {
                int finalI = i;
                db.collection("DailyReport")
                        .whereEqualTo("uid", groupMembers.get(i))
                        .whereEqualTo("statusHefeaz", "" + Common.HAFEZ)
                        .whereEqualTo("year", year)
                        .whereEqualTo("month", month)
                        .orderBy("day", Query.Direction.DESCENDING)
                        .limitToLast(1)
                        .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        StartSura.put(groupMembers.get(finalI), queryDocumentSnapshots.getDocuments().get(0).getString("suratStart"));
                        StartAya1.put(groupMembers.get(finalI), queryDocumentSnapshots.getDocuments().get(0).get("ayaStart", Integer.TYPE));
                        EndAya1.put(groupMembers.get(finalI), queryDocumentSnapshots.getDocuments().get(0).get("ayaEnd", Integer.TYPE));
                    } else {
                        StartSura.put(groupMembers.get(finalI), "");
                        StartAya1.put(groupMembers.get(finalI), 0);
                        EndAya1.put(groupMembers.get(finalI), 0);
                    }
                }).addOnFailureListener(e -> Log.d("sss", "listen:error" + e.getLocalizedMessage()));
            }
        }
    }

    private void getAbsentFromDB(int month, int year) {
        if (groupMembers != null && !groupMembers.isEmpty()) {
            for (int i = 0; i < groupMembers.size(); i++) {
                int finalI = i;
                db.collection("DailyReport")
                        .whereEqualTo("uid", groupMembers.get(i))
                        .whereEqualTo("statusStudent", "" + Common.ABSENT)
                        .whereEqualTo("year", year)
                        .whereEqualTo("month", month)
                        .orderBy("day", Query.Direction.ASCENDING)
                        .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        StudentsAbsentDays.put(groupMembers.get(finalI), queryDocumentSnapshots.size());
                    } else {
                        StudentsAbsentDays.put(groupMembers.get(finalI), 0);
                    }
                }).addOnFailureListener(e -> Log.d("sss", "listen:error" + e.getLocalizedMessage()));

            }
        }
    }

    private void getStartMorageaFromDB(int month, int year) {
        if (groupMembers != null && !groupMembers.isEmpty()) {
            for (int i = 0; i < groupMembers.size(); i++) {
                int finalI = i;
                db.collection("DailyReport")
                        .whereEqualTo("uid", groupMembers.get(i))
                        .whereEqualTo("statusHefeaz", "" + Common.MURAJAEA)
                        .whereEqualTo("year", year)
                        .whereEqualTo("month", month)
                        .orderBy("day", Query.Direction.DESCENDING)
                        .limitToLast(1)
                        .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        StartSuraMoragea.put(groupMembers.get(finalI), queryDocumentSnapshots.getDocuments().get(0).getString("suratStart"));
                        StartAya1Moragea.put(groupMembers.get(finalI), queryDocumentSnapshots.getDocuments().get(0).get("ayaStart", Integer.TYPE));
                        EndAya1Moragea.put(groupMembers.get(finalI), queryDocumentSnapshots.getDocuments().get(0).get("ayaEnd", Integer.TYPE));
                    } else {
                        StartSuraMoragea.put(groupMembers.get(finalI), "");
                        StartAya1Moragea.put(groupMembers.get(finalI), 0);
                        EndAya1Moragea.put(groupMembers.get(finalI), 0);
                    }
                }).addOnFailureListener(e -> Log.d("sss", "listen:error" + e.getLocalizedMessage()));
            }
        }
    }

    private void getEndMorageaFromDB(int month, int year) {
        if (groupMembers != null && !groupMembers.isEmpty()) {
            for (int i = 0; i < groupMembers.size(); i++) {
                int finalI = i;
                db.collection("DailyReport")
                        .whereEqualTo("uid", groupMembers.get(i))
                        .whereEqualTo("statusHefeaz", "" + Common.MURAJAEA)
                        .whereEqualTo("year", year)
                        .whereEqualTo("month", month)
                        .orderBy("day", Query.Direction.ASCENDING)
                        .limitToLast(1)
                        .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        EndSuraMoragea.put(groupMembers.get(finalI), queryDocumentSnapshots.getDocuments().get(0).getString("suratEnd"));
                        StartAya2Moragea.put(groupMembers.get(finalI), queryDocumentSnapshots.getDocuments().get(0).get("ayaStart", Integer.TYPE));
                        EndAya2Moragea.put(groupMembers.get(finalI), queryDocumentSnapshots.getDocuments().get(0).get("ayaEnd", Integer.TYPE));

                    } else {
                        EndSuraMoragea.put(groupMembers.get(finalI), "");
                        StartAya2Moragea.put(groupMembers.get(finalI), 0);
                        EndAya2Moragea.put(groupMembers.get(finalI), 0);
                    }

                }).addOnFailureListener(e -> Log.d("sss", "listen:error" + e.getLocalizedMessage()));
            }
        }
    }

    private void getEndHefezFromDB(int month, int year) {
        if (groupMembers != null && !groupMembers.isEmpty()) {
            for (int i = 0; i < groupMembers.size(); i++) {
                int finalI = i;
                db.collection("DailyReport")
                        .whereEqualTo("uid", groupMembers.get(i))
                        .whereEqualTo("statusHefeaz", "" + Common.HAFEZ)
                        .whereEqualTo("year", year)
                        .whereEqualTo("month", month)
                        .orderBy("day", Query.Direction.ASCENDING)
                        .limitToLast(1)
                        .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        StartAya2.put(groupMembers.get(finalI), queryDocumentSnapshots.getDocuments().get(0).get("ayaStart", Integer.TYPE));
                        EndSura.put(groupMembers.get(finalI), queryDocumentSnapshots.getDocuments().get(0).getString("suratEnd"));
                        EndAya2.put(groupMembers.get(finalI), queryDocumentSnapshots.getDocuments().get(0).get("ayaEnd", Integer.TYPE));
                    } else {
                        EndSura.put(groupMembers.get(finalI), "");
                        EndAya2.put(groupMembers.get(finalI), 0);
                        StartAya2.put(groupMembers.get(finalI), 0);
                    }
                }).addOnFailureListener(e -> Log.d("sss", "listen:error" + e.getLocalizedMessage()));
            }
        }
    }

    private void getNumberPageSuraFromApi() {
        createNotification("جاري معالجة البيانات ...", "تجهيز التقرير الشهري", 0);
        if (groupMembers != null && !groupMembers.isEmpty()
                && StartSura != null && !StartSura.isEmpty()
                && StartAya1 != null && !StartAya1.isEmpty()
                && EndSura != null && !EndSura.isEmpty()
                && EndAya1 != null && !EndAya1.isEmpty()) {
            for (int i = 0; i < groupMembers.size(); i++) {
                if (StudentTotalPagesHefaz.get(groupMembers.get(i)) == null) {
                    StudentTotalPagesHefaz.put(groupMembers.get(i), 0.0);
                }
                String suraStart = StartSura.get(groupMembers.get(i));
                String suraEnd = EndSura.get(groupMembers.get(i));
                if (suraStart != null) {
                    if (suraStart.equals(suraEnd)) {
                        Log.d("sss", "" + StartSura.get(groupMembers.get(i)));
                        queryFromApi(suraEnd, suraStart, StartAya1.get(groupMembers.get(i)),
                                EndAya1.get(groupMembers.get(i)), 0, groupMembers.get(i), 0);
                    } else {
                        int indexStart = 0;
                        int indexEnd = 0;
                        for (int i1 = 1; i1 < Surat.length; i1++) {
                            String suraStart1 = StartSura.get(groupMembers.get(i));
                            String suraEnd1 = EndSura.get(groupMembers.get(i));
                            String sura1 = Surat[i1];
                            if (suraStart1 != null && suraStart1.equals(sura1)) {
                                indexStart = i1;
                            }
                            if (suraEnd1 != null && suraEnd1.equals(sura1)) {
                                indexEnd = i1;
                            }
                        }
                        for (int j = indexEnd; j <= indexStart; j++) {
                            if (j == indexEnd) {
                                queryFromApi(EndSura.get(groupMembers.get(i)), StartSura.get(groupMembers.get(i)), StartAya2.get(groupMembers.get(i)),
                                        EndAya2.get(groupMembers.get(i)), 1, groupMembers.get(i), 0);
                            } else if (j == indexStart) {
                                queryFromApi(EndSura.get(groupMembers.get(i)), StartSura.get(groupMembers.get(i)), StartAya1.get(groupMembers.get(i)),
                                        EndAya1.get(groupMembers.get(i)), 2, groupMembers.get(i), 0);
                            } else {
                                queryFromApi(Surat[j], Surat[j], StartAya1.get(groupMembers.get(i)),
                                        EndAya1.get(groupMembers.get(i)), 3, groupMembers.get(i), 0);
                            }
                        }
                    }
                }
            }
        }

    }

    private void getNumberPageSuraMorageaFromApi() {
        if (groupMembers != null && !groupMembers.isEmpty()
                && StartSuraMoragea != null && !StartSuraMoragea.isEmpty()
                && StartAya1Moragea != null && !StartAya1Moragea.isEmpty()
                && EndSuraMoragea != null && !EndSuraMoragea.isEmpty()
                && EndAya1Moragea != null && !EndAya1Moragea.isEmpty()) {
            for (int i = 0; i < groupMembers.size(); i++) {
                if (StudentTotalPagesMoragea.get(groupMembers.get(i)) == null) {
                    StudentTotalPagesMoragea.put(groupMembers.get(i), 0.0);
                }
                String suraStart = StartSuraMoragea.get(groupMembers.get(i));
                String suraEnd = EndSuraMoragea.get(groupMembers.get(i));
                if (suraStart != null) {
                    if (suraStart.equals(suraEnd)) {
                        Log.d("sss", "" + StartSuraMoragea.get(groupMembers.get(i)));
                        queryFromApi(suraEnd, suraStart, StartAya1Moragea.get(groupMembers.get(i)),
                                EndAya1Moragea.get(groupMembers.get(i)), 0, groupMembers.get(i), 1);
                    } else {
                        int indexStart = 0;
                        int indexEnd = 0;
                        for (int i1 = 1; i1 < Surat.length; i1++) {
                            String suraStart1 = StartSuraMoragea.get(groupMembers.get(i));
                            String suraEnd1 = EndSuraMoragea.get(groupMembers.get(i));
                            String sura1 = Surat[i1];
                            if (suraStart1 != null && suraStart1.equals(sura1)) {
                                indexStart = i1;
                            }
                            if (suraEnd1 != null && suraEnd1.equals(sura1)) {
                                indexEnd = i1;
                            }
                        }
                        for (int j = indexEnd; j <= indexStart; j++) {
                            if (j == indexEnd) {
                                queryFromApi(EndSuraMoragea.get(groupMembers.get(i)), StartSuraMoragea.get(groupMembers.get(i)), StartAya2Moragea.get(groupMembers.get(i)),
                                        EndAya2Moragea.get(groupMembers.get(i)), 1, groupMembers.get(i), 1);
                            } else if (j == indexStart) {
                                queryFromApi(EndSuraMoragea.get(groupMembers.get(i)), StartSuraMoragea.get(groupMembers.get(i)), StartAya1Moragea.get(groupMembers.get(i)),
                                        EndAya1Moragea.get(groupMembers.get(i)), 2, groupMembers.get(i), 1);
                            } else {
                                queryFromApi(Surat[j], Surat[j], StartAya1Moragea.get(groupMembers.get(i)),
                                        EndAya1Moragea.get(groupMembers.get(i)), 3, groupMembers.get(i), 1);
                            }
                        }
                    }
                }
            }
        }
    }

    private void queryFromApi(String suraEnd, String suraStart, Integer ayaStart, Integer ayaend, int type, String uid, int typeMap) {
        StringRequest request = new StringRequest(Request.Method.POST, "https://alansar-center.000webhostapp.com/index.php", response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getBoolean("success")) {
                    if (!jsonObject.isNull("message")) {
                        if (type == 0) {
                            if (typeMap == 0) {
                                StudentTotalPagesHefaz.put(uid, jsonObject.getDouble("message"));
                            } else {
                                StudentTotalPagesMoragea.put(uid, jsonObject.getDouble("message"));
                            }
                        } else {
                            if (typeMap == 0) {
                                if (StudentTotalPagesHefaz != null && StudentTotalPagesHefaz.get(uid) != null) {
                                    double result = StudentTotalPagesHefaz.get(uid);
                                    StudentTotalPagesHefaz.put(uid, result + jsonObject.getDouble("message"));
                                    Log.d("sss", "h " + jsonObject.getDouble("message"));
                                }
                            } else {
                                if (StudentTotalPagesMoragea != null && StudentTotalPagesMoragea.get(uid) != null) {
                                    double result = StudentTotalPagesMoragea.get(uid);
                                    StudentTotalPagesMoragea.put(uid, result + jsonObject.getDouble("message"));
                                    Log.d("sss", "m " + jsonObject.getDouble("message"));
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.d("sss", "error : " + error)) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("suraStart", "" + suraStart);
                map.put("suraEnd", "" + suraEnd);
                map.put("ayaStart", "" + ayaStart);
                map.put("ayaEnd", "" + ayaend);
                map.put("type", "" + type);
                return map;
            }
        };
        request.setShouldCache(true);
        requestQueue.add(request);
    }

    private void getNameStudentsFromDB() {
        if (groupMembers != null && !groupMembers.isEmpty()) {
            for (int i = 0; i < groupMembers.size(); i++) {
                int finalI = i;
                db.collection("Student")
                        .document(groupMembers.get(i))
                        .get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        StudentsNames.put(groupMembers.get(finalI), documentSnapshot.getString("name"));
                    }
                });
            }
        }
    }

    private void getTotalConservationFromDB(int year, int month) {
        if (groupMembers != null && !groupMembers.isEmpty()) {
            for (int i = 0; i < groupMembers.size(); i++) {
                int finalI = i;
                db.collection("Exam")
                        .whereEqualTo("idStudent", groupMembers.get(i))
                        .whereEqualTo("idMohafez", Common.currentPerson.getId())
                        .whereEqualTo("statusAcceptance", 3)
                        .whereEqualTo("year", year)
                        .whereEqualTo("month", month)
                        .orderBy("day", Query.Direction.DESCENDING)
                        .limit(1)
                        .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        double result = calcMarksExam(Objects.requireNonNull(queryDocumentSnapshots.getDocuments()
                                .get(0).toObject(Exam.class)).getMarksExamQuestions());
                        String examPart = Objects.requireNonNull(queryDocumentSnapshots.getDocuments().get(0).toObject(Exam.class)).getExamPart();
                        if (result >= 80.00) {
                            int start = examPart.indexOf("(");
                            int end = examPart.indexOf(")");
                            String castExamPart = examPart.substring(start + 1, end);
                            String note = "اجتاز الطالب اختبار جزء" + castExamPart + "بمعدل" + result;
                            NotesAndExams.put(groupMembers.get(finalI), note);
                            for (int j = 0; j < Values.length; j++) {
                                if (examPart.equals(Values[j])) {
                                    int countPart = 31 - j;
                                    if (j == 30) {
                                        StudentsTotalConservation.put(groupMembers.get(finalI), 1);
                                    } else {
                                        StudentsTotalConservation.put(groupMembers.get(finalI), countPart);
                                    }
                                }
                            }
                        } else if (result < 80.00) {
                            NotesAndExams.put(groupMembers.get(finalI), "");
                            for (int j = 0; j < Values.length; j++) {
                                if (examPart.equals(Values[j])) {
                                    int countPart = 31 - (j + 1);
                                    if (countPart == -1) {
                                        StudentsTotalConservation.put(groupMembers.get(finalI), 0);
                                    } else {
                                        StudentsTotalConservation.put(groupMembers.get(finalI), countPart);
                                    }
                                }
                            }
                        }
                    } else {
                        db.collection("Exam")
                                .whereEqualTo("idStudent", groupMembers.get(finalI))
                                .whereEqualTo("idMohafez", Common.currentPerson.getId())
                                .whereEqualTo("statusAcceptance", 3)
                                .whereEqualTo("year", year)
                                .whereLessThan("month", month)
                                .orderBy("month", Query.Direction.DESCENDING)
                                .orderBy("day", Query.Direction.DESCENDING)
                                .limit(1)
                                .get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                            if (queryDocumentSnapshots1 != null && !queryDocumentSnapshots1.isEmpty()) {
                                double result = calcMarksExam(Objects.requireNonNull(queryDocumentSnapshots1.getDocuments()
                                        .get(0).toObject(Exam.class)).getMarksExamQuestions());
                                String examPart = Objects.requireNonNull(queryDocumentSnapshots1.getDocuments().get(0).toObject(Exam.class)).getExamPart();
                                if (result >= 80.00) {
                                    NotesAndExams.put(groupMembers.get(finalI), "");
                                    for (int j = 0; j < Values.length; j++) {
                                        if (examPart.equals(Values[j])) {
                                            int countPart = 31 - j;
                                            if (j == 30) {
                                                StudentsTotalConservation.put(groupMembers.get(finalI), 1);
                                            } else {
                                                StudentsTotalConservation.put(groupMembers.get(finalI), countPart);
                                            }
                                        }
                                    }
                                } else if (result < 80.00) {
                                    NotesAndExams.put(groupMembers.get(finalI), "");
                                    for (int j = 0; j < Values.length; j++) {
                                        if (examPart.equals(Values[j])) {
                                            int countPart = 31 - (j + 1);
                                            if (countPart == -1) {
                                                StudentsTotalConservation.put(groupMembers.get(finalI), 0);
                                            } else {
                                                StudentsTotalConservation.put(groupMembers.get(finalI), countPart);
                                            }
                                        }
                                    }
                                }
                            } else {
                                NotesAndExams.put(groupMembers.get(finalI), "");
                                StudentsTotalConservation.put(groupMembers.get(finalI), 0);
                            }
                        }).addOnFailureListener(e -> Log.d("sss", "listen:error" + e.getLocalizedMessage()));
                    }
                });
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
        }
        return 0;
    }

    private double round(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private double roundCount(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void getIdsOfStudentsFromDB(int year, int month) {
        if (Common.currentPerson != null && !Common.currentPerson.getId().isEmpty()
                && Common.currentGroupId != null) {
            db.collection("GroupMembers")
                    .document(Common.currentGroupId)
                    .get().addOnSuccessListener(documentSnapshot1 -> {
                if (documentSnapshot1.exists()) {
                    GroupMembers groupMembers1 = documentSnapshot1.toObject(GroupMembers.class);
                    if (groupMembers1 != null) {
                        groupMembers = groupMembers1.getGroupMembers();
                        getNameStudentsFromDB();
                        getDateOfBirth();
                        getTotalConservationFromDB(year, month);
                        getStartHefezFromDB(month, year);
                        getAbsentFromDB(month, year);
                        getEndHefezFromDB(month, year);
                        getStartMorageaFromDB(month, year);
                        getEndMorageaFromDB(month, year);
                        new Handler().postDelayed(() -> {
                            getNumberPageSuraMorageaFromApi();
                            getNumberPageSuraFromApi();
                        }, 2000);
                    }
                }
            });
        }
    }

}
