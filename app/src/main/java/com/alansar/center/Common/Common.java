package com.alansar.center.Common;


import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.InputType;
import android.util.Log;

import com.alansar.center.Activitys.LoginActivity;
import com.alansar.center.Models.Person;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import io.paperdb.Paper;

import static android.content.Context.NOTIFICATION_SERVICE;


public class Common {
    public static final String PERMISSION = "Permission";
    public static final String PERSON = "Person";
    public static final String ISLOGIN = "isLogin";
    public static final String UPDATE = "تحديث";
    public static final String DELETE = "حذف";
    public static final String VIEW_THE_LATEST_MONTHLY_REPORT = "عرض أخر تقرير شهري";
    public static final String VIEW_THE_LATEST_EXAM = "عرض أخر اختبار";
    public static final String START_THE_EXAM = "بدء الإختبار";
    public static final String MORE_DETAILS = "تفاصيل أكثر";
    public static final String UPDATE_THE_EXAM = "تحديث الإختبار";
    public static final String PART_OF_AMA = "الجزء الثلاثين ( عم )";
    public static final String PRESENT = "حاضر";
    public static final String ABSENT = "غائب";
    public static final String AUTHORIZED = "مأذون";
    public static final String HAFEZ = "حافظ";
    public static final String MURAJAEA = "مراجعة";
    public static final String NO_HAFEZ = "لم يحفظ";
    public static final String EXCELLENT = "ممتاز";
    public static final String VERY_GOOD = "جيد جدا";
    public static final String GOOD = "متوسط";
    public static final String WEEK = "ضعيف";
    public static final String PLACE_AN_ORDER = "إجراء الطلب";
    public static final String DELETE_AN_ORDER = "حذف الطلب";
    public static final String ADD_DAILY_RECITATIONS = "إضافة التسميع اليومي";
    public static final String UPDATE_DAILY_RECITATIONS = "تحديث التسميع اليومي";
    public static final String DELETE_DAILY_RECITATIONS = "حذف التسميع اليومي";
    public static final String REQUEST_A_TEST = "طلب إختبار";
    public static final String ISENABLEACCOUNT = "تمكين الحساب";
    public static final String ISDISABLEACCOUNT = "تعطيل الحساب";
    public static final int PICK_IMAGE_REQUEST = 71;
    public static final String PERMISSIONS_STUDENTN = "std";
    public static final String PERMISSIONS_MOHAFEZ = "moh";
    public static final String PERMISSIONS_ADMIN = "admin";
    public static final String PERMISSIONS_EDARE = "edr";
    public static final String PERMISSIONS_SUPER_VISOR = "sup_vis";
    public static final String PERMISSIONS_SUPER_VISOR_EXAMS = "sup_vis_exam";
    public static final String PERMISSIONS_TESTER = "tester";
    public static final String SUP_STAGE = "مرحلة البراعم";
    public static final String FOUNDATION_STAGE = "المرحلة التأسيسية";
    public static final String INTERMEDIATE_STAGE = "المرحلة المتوسطة";
    public static final String THE_UPPER_STAGE = "المرحلة العليا";
    public static final String STAGE = "Stage";
    public static final String GROUPID = "GroupId";
    public static Person currentPerson;
    public static String currentPermission;
    public static String currentSTAGE;
    public static String currentGroupId;

    public static void disableSoftInputFromAppearing(MaterialEditText editText) {
        editText.setRawInputType(InputType.TYPE_NULL);
        editText.setTextIsSelectable(true);
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null;
    }

    public static boolean hasActiveInternetConnection(Context context) {
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("https://clients3.google.com/generate_204")
                                .openConnection());

                return (urlc.getResponseCode() != 204 || urlc.getContentLength() != 0);
            } catch (IOException e) {
                Log.e("sss", "Error checking internet connection", e);
            }
        } else {
            Log.d("sss", "No network available!");
        }
        return true;
    }

    public static void SignOut(FirebaseAuth mauth, Activity activity, ListenerRegistration registration) {
        Paper.book().destroy();
        mauth.signOut();
        activity.startActivity(new Intent(activity, LoginActivity.class));
        if (registration != null) {
            registration.remove();
        }
        activity.finish();
    }

    public static NotificationManager getNotificationManager(NotificationManager notificationManager, Context context) {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public static String ConvertPermissionToNameArabic(String Permission) {
        if (Permission != null && !Permission.isEmpty()) {
            switch (Permission) {
                case Common.PERMISSIONS_MOHAFEZ:
                    return "محفظ";
                case Common.PERMISSIONS_SUPER_VISOR:
                    return "مشرف";
                case Common.PERMISSIONS_TESTER:
                    return "مختبر";
                case Common.PERMISSIONS_EDARE:
                    return "إداري";
                case Common.PERMISSIONS_ADMIN:
                    return "أمير المركز";
                case Common.PERMISSIONS_SUPER_VISOR_EXAMS:
                    return "مشرف الإختبارات";
            }
        }
        return "";
    }

    public static String ConvertPermissionToNameEnglish(String Permission) {
        if (Permission != null && !Permission.isEmpty()) {
            switch (Permission) {
                case "محفظ":
                    return Common.PERMISSIONS_MOHAFEZ;
                case "مشرف":
                    return Common.PERMISSIONS_SUPER_VISOR;
                case "مختبر":
                    return Common.PERMISSIONS_TESTER;
                case "إداري":
                    return Common.PERMISSIONS_EDARE;
                case "أمير المركز":
                    return Common.PERMISSIONS_ADMIN;
                case "مشرف الإختبارات":
                    return Common.PERMISSIONS_SUPER_VISOR_EXAMS;
            }
        }
        return "";
    }

}