package com.alansar.center.supervisor_exams.Activitys;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Adapter.Multiple_accounts_Adapter;
import com.alansar.center.Common.Common;
import com.alansar.center.FButton;
import com.alansar.center.Models.AccountItem;
import com.alansar.center.Models.Person;
import com.alansar.center.Notifications.Token;
import com.alansar.center.R;
import com.alansar.center.SweetAlertDialog_;
import com.alansar.center.supervisor_exams.Fragments.ExamsFragment;
import com.alansar.center.supervisor_exams.Fragments.ExamsSettingsFragment;
import com.alansar.center.supervisor_exams.Fragments.HomeFragment;
import com.alansar.center.supervisor_exams.Fragments.Orders_Exams_Fragment;
import com.alansar.center.supervisor_exams.Fragments.TestersFragment;
import com.alansar.center.supervisor_exams.Fragments.TodayTestsFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class SuperVisorExamsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static Fragment fragment;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    Class fragmentClass;
    FirebaseFirestore db;
    private TextView tv_person_name, countUnread_orders_exams, countUnread_exams, countUnread_orders_exams_today;
    private CircleImageView img_profile;
    private FirebaseAuth mauth;
    private SweetAlertDialog_ sweetAlertDialog_;
    private ArrayList<AccountItem> accountItems;
    private ListenerRegistration registration;
    private String typeFragment;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_exams);

        sweetAlertDialog_ = new SweetAlertDialog_(this);
        Paper.init(this);
        drawerLayout = findViewById(R.id.supervisor_exams_drawer);
        toolbar = findViewById(R.id.supervisor_exams_toolbar);
        navigationView = findViewById(R.id.navigation_view);
        View view = navigationView.getHeaderView(0);
        Button backButton = view.findViewById(R.id.btn_back);
        backButton.setOnClickListener(view1 -> drawerLayout.closeDrawers());
        mauth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        accountItems = new ArrayList<>();

        tv_person_name = view.findViewById(R.id.tv_Person_name);
        TextView tv_person_Permission = view.findViewById(R.id.tv_Person_Permission);
        img_profile = view.findViewById(R.id.img_person_profile);

        if (getIntent() != null && getIntent().getStringExtra("Permission") != null) {
            tv_person_Permission.setText(Common.ConvertPermissionToNameArabic(getIntent().getStringExtra("Permission")));
            Common.currentPermission = getIntent().getStringExtra("Permission");
            if (getIntent().getStringExtra("typeFragment") != null) {
                typeFragment = getIntent().getStringExtra("typeFragment");
            }
        } else {
            Common.SignOut(mauth, SuperVisorExamsActivity.this, registration);
        }

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawerOpen, R.string.drawerClose);
        toggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        if (typeFragment == null) {
            fragmentClass = HomeFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            HomeFragment home = new HomeFragment();
            moveFragment(home);
            toolbar.setTitle("الصفحة الرئيسية");
            toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else if (typeFragment.equals("Orders_Exams_Fragment")) {
            fragmentClass = Orders_Exams_Fragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            Orders_Exams_Fragment home = new Orders_Exams_Fragment();
            moveFragment(home);
            toolbar.setTitle("طلبات الإختبارات");
            toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else if (typeFragment.equals("ExamsFragment")) {
            fragmentClass = ExamsFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            ExamsFragment home = new ExamsFragment();
            moveFragment(home);
            toolbar.setTitle("الإختبارات");
            toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        MenuItem menuItem_unCheck_order_exams = navigationView.getMenu().findItem(R.id.supervisor_exams_orders_exams);
        MenuItem menuItem_unCheck_order_exams_today = navigationView.getMenu().findItem(R.id.supervisor_exams_today_tests);
        MenuItem menuItem_unCheck_exams = navigationView.getMenu().findItem(R.id.supervisor_exams_exams);
        countUnread_orders_exams = (TextView) menuItem_unCheck_order_exams.getActionView();
        countUnread_orders_exams_today = (TextView) menuItem_unCheck_order_exams_today.getActionView();
        countUnread_exams = (TextView) menuItem_unCheck_exams.getActionView();
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            if (instanceIdResult != null) {
                updateToken(instanceIdResult.getToken());
            }
        });
    }

    private void updateToken(String token) {
        db.collection("Token")
                .document(Common.currentPerson.getId()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        documentSnapshot.getReference().update("token", token);
                    } else {
                        db.collection("Token")
                                .document(Common.currentPerson.getId()).
                                set(new Token(token));
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Common.currentPerson != null && Common.currentPermission != null) {
            listenPersonFromDB(Common.currentPerson.getId());
            if (Common.currentPerson.getPermissions().size() == 1) {
                hideItem();
            }
            checkUnreadDataOfExamFromDB();
        } else {
            Common.currentPerson = Paper.book().read(Common.PERSON);
            Common.currentPermission = Paper.book().read(Common.PERMISSION);
            if (Common.currentPerson != null) {
                listenPersonFromDB(Common.currentPerson.getId());
                if (Common.currentPerson.getPermissions().size() == 1) {
                    hideItem();
                }
                checkUnreadDataOfExamFromDB();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (registration != null) {
            registration.remove();
        }
    }

    public void moveFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(android.R.animator.fade_in,
                android.R.animator.fade_out).replace(R.id.frameLayout, fragment).commit();
    }

    @SuppressLint("SetTextI18n")
    private void checkUnreadDataOfExamFromDB() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(-2);
        list.add(-3);
        registration = db.collection("Exam")
                .whereIn("statusAcceptance", list)
                .orderBy("year", Query.Direction.DESCENDING)
                .orderBy("month", Query.Direction.DESCENDING)
                .orderBy("day", Query.Direction.DESCENDING)
                .whereEqualTo("isSeenExam.isSeenMoshrefExam", false)
                .addSnapshotListener(this, (queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.w("sss", "listen:error" + e.getLocalizedMessage());
                        return;
                    }
                    if (queryDocumentSnapshots != null) {
                        if (countUnread_orders_exams != null) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                countUnread_orders_exams.setVisibility(View.VISIBLE);
                                countUnread_orders_exams.setText("+" + queryDocumentSnapshots.size());
                            } else {
                                countUnread_orders_exams.setVisibility(View.GONE);
                            }
                        }
                    }
                });
        registration = db.collection("Exam")
                .whereEqualTo("statusAcceptance", 3)
                .orderBy("year", Query.Direction.DESCENDING)
                .orderBy("month", Query.Direction.DESCENDING)
                .orderBy("day", Query.Direction.DESCENDING)
                .whereEqualTo("isSeenExam.isSeenMoshrefExam", false)
                .addSnapshotListener(this, (queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.w("sss", "listen:error" + e.getLocalizedMessage());
                        return;
                    }
                    if (queryDocumentSnapshots != null) {
                        if (countUnread_exams != null) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                countUnread_exams.setVisibility(View.VISIBLE);
                                countUnread_exams.setText("+" + queryDocumentSnapshots.size());
                            } else {
                                countUnread_exams.setVisibility(View.GONE);
                            }
                        }
                    }
                });

        Calendar calendar = Calendar.getInstance();
        registration = db.collection("Exam")
                .whereEqualTo("day", calendar.get(Calendar.DAY_OF_MONTH))
                .whereEqualTo("month", calendar.get(Calendar.MONTH) + 1)
                .whereEqualTo("year", calendar.get(Calendar.YEAR))
                .whereEqualTo("statusAcceptance", 2)
                .whereEqualTo("isSeenExam.isSeenMoshrefExam", false)
                .addSnapshotListener(this, (queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.w("sss", "listen:error" + e.getLocalizedMessage());
                        return;
                    }
                    if (queryDocumentSnapshots != null) {
                        if (countUnread_orders_exams_today != null) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                countUnread_orders_exams_today.setVisibility(View.VISIBLE);
                                countUnread_orders_exams_today.setText("+" + queryDocumentSnapshots.size());
                            } else {
                                countUnread_orders_exams_today.setVisibility(View.GONE);
                            }
                        }
                    }

                });
    }


    @SuppressLint("SetTextI18n")
    private void listenPersonFromDB(String UID) {
        registration = db.collection("Person").document(UID).addSnapshotListener((documentSnapshot, e) -> {
            assert documentSnapshot != null;
            if (documentSnapshot.exists()) {
                Person person = documentSnapshot.toObject(Person.class);
                assert person != null;
                Common.currentPerson = person;
                tv_person_name.setText(person.getFname() + " " + person.getMname() + " " + person.getLname());
                if (!person.getImage().isEmpty()) {
                    Picasso.get().load(person.getImage()).into(img_profile);
                } else {
                    img_profile.setImageResource(R.drawable.profile_image);
                }

                if (!person.isEnableAccount()) {
                    if (!this.isFinishing()) {
                        sweetAlertDialog_.showDialogError("لقد تم تعطيل حسابك , يرجى مراجعة إدارة التطبيق !")
                                .setConfirmButton("OK", sweetAlertDialog1 -> Common.SignOut(mauth, SuperVisorExamsActivity.this, registration));
                    }
                }

                Paper.book().write(Common.PERSON, person);
                if (person.getPermissions().size() == 1 &&
                        person.getPermissions().contains(Common.currentPermission)) {
                    hideItem();
                } else if (person.getPermissions().size() == 1 &&
                        !person.getPermissions().contains(Common.currentPermission)) {
                    drawerLayout.closeDrawers();
                    if (!this.isFinishing()) {
                        sweetAlertDialog_.showDialogError("لقد تم تعطيل صلاحية دخولك بحساب " + Common.currentPermission + " , يرجى مراجعة إدارة التطبيق !")
                                .setConfirmButton("OK", sweetAlertDialog -> showDialogMultipleAccounts());
                    }

                } else if (person.getPermissions().size() > 1 &&
                        person.getPermissions().contains(Common.currentPermission)) {
                    showItem();
                } else if (person.getPermissions().size() > 1 &&
                        !person.getPermissions().contains(Common.currentPermission)) {
                    drawerLayout.closeDrawers();
                    if (!this.isFinishing()) {
                        sweetAlertDialog_.showDialogError("لقد تم تعطيل صلاحية دخولك بحساب " + Common.currentPermission + " , يرجى مراجعة إدارة التطبيق !")
                                .setConfirmButton("OK", sweetAlertDialog -> showDialogMultipleAccounts());
                    }
                } else if (person.getPermissions().isEmpty()) {
                    if (!this.isFinishing()) {
                        sweetAlertDialog_.showDialogError("لا يوجد لديك أي صلاحية لتسجيل الدخول إلى حسابك , راجع إدارة التطبيق")
                                .setConfirmButton("OK", sweetAlertDialog -> Common.SignOut(mauth, SuperVisorExamsActivity.this, registration));
                    }
                }

            }
        });
    }

    private void hideItem() {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.supervisor_exams_switch_account).setVisible(false);
    }

    private void showItem() {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.supervisor_exams_switch_account).setVisible(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.supervisor_exams_home:
                HomeFragment home = new HomeFragment();
                moveFragment(home);
                toolbar.setTitle("الصفحة الرئيسية");
                toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                drawerLayout.closeDrawers();
                menuItem.setChecked(true);
                menuItem.setCheckable(true);
                break;
            case R.id.supervisor_exams_orders_exams:
                Orders_Exams_Fragment orders_exams_fragment = new Orders_Exams_Fragment();
                moveFragment(orders_exams_fragment);
                toolbar.setTitle("طلبات الإختبارات");
                toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                drawerLayout.closeDrawers();
                menuItem.setChecked(true);
                menuItem.setCheckable(true);
                break;
            case R.id.supervisor_exams_today_tests:
                TodayTestsFragment todayTestsFragment = new TodayTestsFragment();
                moveFragment(todayTestsFragment);
                toolbar.setTitle("اختبارات اليوم");
                toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                drawerLayout.closeDrawers();
                menuItem.setChecked(true);
                menuItem.setCheckable(true);
                break;
            case R.id.supervisor_exams_exams:
                ExamsFragment examsFragment = new ExamsFragment();
                moveFragment(examsFragment);
                toolbar.setTitle("الإختبارات");
                toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                drawerLayout.closeDrawers();
                menuItem.setChecked(true);
                menuItem.setCheckable(true);
                break;
            case R.id.supervisor_exams_members:
                TestersFragment testersFragment = new TestersFragment();
                moveFragment(testersFragment);
                toolbar.setTitle("المختبرين");
                toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                drawerLayout.closeDrawers();
                menuItem.setChecked(true);
                menuItem.setCheckable(true);
                break;
            case R.id.supervisor_exams_Exams_Settings:
                ExamsSettingsFragment examsSettingsFragment = new ExamsSettingsFragment();
                moveFragment(examsSettingsFragment);
                toolbar.setTitle("إعدادات الإختبارات");
                toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                drawerLayout.closeDrawers();
                menuItem.setChecked(true);
                menuItem.setCheckable(true);
                break;
            case R.id.supervisor_exams_switch_account:
                showDialogMultipleAccounts();
                drawerLayout.closeDrawers();
                menuItem.setChecked(true);
                menuItem.setCheckable(true);
                break;
            case R.id.supervisor_exams_logout:
                menuItem.setChecked(true);
                menuItem.setCheckable(true);
                Common.SignOut(mauth, SuperVisorExamsActivity.this, registration);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void showDialogMultipleAccounts() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View content = inflater.inflate(R.layout.multiple_accounts_dialog, null);
        builder.setView(content);
        RecyclerView mRecyclerView = content.findViewById(R.id.accounts_recycler_view);
        FButton logout = content.findViewById(R.id.btn_logout);

        if (Common.currentPerson != null) {
            for (int i = 0; i < Common.currentPerson.getPermissions().size(); i++) {
                Person person = Common.currentPerson;
                AccountItem accountItem = new AccountItem();
                accountItem.setName(person.getFname() + " " + person.getMname() + " " + person.getLname());
                accountItem.setPermission(person.getPermissions().get(i));
                accountItem.setImage(person.getImage());
                accountItems.add(accountItem);
            }
            logout.setOnClickListener(view -> Common.SignOut(mauth, SuperVisorExamsActivity.this, registration));
            Multiple_accounts_Adapter adapter = new Multiple_accounts_Adapter(accountItems, this);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            builder.setCancelable(false);
            builder.show();
        }
    }
}
