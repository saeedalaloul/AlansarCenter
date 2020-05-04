package com.alansar.center.Activitys;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.alansar.center.Fragments.Personal_Information__Fragment;
import com.alansar.center.Fragments.Update_Personal_Information__Fragment;
import com.alansar.center.R;

import java.util.Objects;

public class HostingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hosting);
        if (getIntent() != null && getIntent().getStringExtra("fragmentType") != null && getIntent().getStringExtra("permissions") != null) {
            addFragment(Objects.requireNonNull(getIntent().getStringExtra("fragmentType")), getIntent().getStringExtra("permissions"));
        }
    }

    private void addFragment(String fragmentType, String permissions) {
        Bundle bundle = new Bundle();
        bundle.putString("permissions", permissions);
        if (fragmentType.equals("Personal_Information__Fragment")) {
            Personal_Information__Fragment fragment = new Personal_Information__Fragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_host_layout, fragment).commit();
        } else if (fragmentType.equals("Update_Personal_Information__Fragment")) {
            bundle.putString("UID", getIntent().getStringExtra("UID"));
            Update_Personal_Information__Fragment fragment = new Update_Personal_Information__Fragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_host_layout, fragment).commit();
        }
    }
}
