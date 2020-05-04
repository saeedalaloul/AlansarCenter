package com.alansar.center.Activitys;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.alansar.center.Common.Common;
import com.alansar.center.Edare.Activitys.EdareActivity;
import com.alansar.center.Fragments.LoginFragment;
import com.alansar.center.Mohafez.Activitys.MohafezActivity;
import com.alansar.center.Moshref.Activity.MoshrefActivity;
import com.alansar.center.R;
import com.alansar.center.administrator.Activitys.AdminActivity;
import com.alansar.center.supervisor_exams.Activitys.SuperVisorExamsActivity;
import com.alansar.center.testers.Activitys.TesterActivity;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        Paper.init(this);
        addFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean isLogin = Paper.book().read(Common.ISLOGIN, false);
        if (isLogin) {
            if (Paper.book().read(Common.PERSON) != null
                    && Paper.book().read(Common.PERMISSION) != null)
                Common.currentPerson = Paper.book().read(Common.PERSON);
            Common.currentPermission = Paper.book().read(Common.PERMISSION);
            SendUserToMainActivity();
        }
    }

    private void addFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_login_layout, new LoginFragment()).commit();
    }


    private void SendUserToMainActivity() {
        Intent homeIntent = null;
        switch (Paper.book().read(Common.PERMISSION, "")) {
            case Common.PERMISSIONS_ADMIN:
                homeIntent = new Intent(LoginActivity.this, AdminActivity.class);
                break;
            case Common.PERMISSIONS_SUPER_VISOR:
                homeIntent = new Intent(LoginActivity.this, MoshrefActivity.class);
                break;
            case Common.PERMISSIONS_EDARE:
                homeIntent = new Intent(LoginActivity.this, EdareActivity.class);
                break;
            case Common.PERMISSIONS_MOHAFEZ:
                homeIntent = new Intent(LoginActivity.this, MohafezActivity.class);
                break;
            case Common.PERMISSIONS_SUPER_VISOR_EXAMS:
                homeIntent = new Intent(LoginActivity.this, SuperVisorExamsActivity.class);
                break;
            case Common.PERMISSIONS_TESTER:
                homeIntent = new Intent(LoginActivity.this, TesterActivity.class);
                break;
        }
        if (homeIntent != null) {
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            homeIntent.putExtra("Permission", Paper.book().read(Common.PERMISSION).toString());
            startActivity(homeIntent);
            finish();
        }
    }
}