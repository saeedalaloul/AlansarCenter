package com.alansar.center.Edare.Activitys;

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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Adapter.Multiple_accounts_Adapter;
import com.alansar.center.Common.Common;
import com.alansar.center.Edare.Fragment.CenterReportsFragment;
import com.alansar.center.Edare.Fragment.ExamsFragment;
import com.alansar.center.Edare.Fragment.HalakatFragment;
import com.alansar.center.Edare.Fragment.HomeFragment;
import com.alansar.center.Edare.Fragment.MohafezFragment;
import com.alansar.center.Edare.Fragment.StudentFragment;
import com.alansar.center.FButton;
import com.alansar.center.Models.AccountItem;
import com.alansar.center.Models.Person;
import com.alansar.center.Notifications.Token;
import com.alansar.center.R;
import com.alansar.center.SweetAlertDialog_;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class EdareActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static Fragment fragment;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    Class fragmentClass;
    FirebaseFirestore db;
    private TextView tv_person_name, tv_person_Permission;
    private CircleImageView img_profile;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mauth;
    private SweetAlertDialog_ sweetAlertDialog_;
    private ArrayList<AccountItem> accountItems;
    private ListenerRegistration registration;
    private TextView countUnread_exams;
    private AlertDialog dialogMultipleAccounts;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edare);

        sweetAlertDialog_ = new SweetAlertDialog_(this);
        accountItems = new ArrayList<>();
        mauth = FirebaseAuth.getInstance();
        mCurrentUser = mauth.getCurrentUser();
        Paper.init(this);
        drawerLayout = findViewById(R.id.edare_drawer);
        toolbar = findViewById(R.id.edare_toolbar);
        db = FirebaseFirestore.getInstance();
        navigationView = findViewById(R.id.navigation_view);
        View view = navigationView.getHeaderView(0);
        Button backButton = view.findViewById(R.id.btn_back);
        backButton.setOnClickListener(view1 -> drawerLayout.closeDrawers());
        tv_person_name = view.findViewById(R.id.tv_Person_name);
        tv_person_Permission = view.findViewById(R.id.tv_Person_Permission);
        img_profile = view.findViewById(R.id.img_person_profile);

        if (getIntent() != null && getIntent().getStringExtra("Permission") != null) {
            Common.currentPermission = getIntent().getStringExtra("Permission");
        } else {
            Common.SignOut(mauth, EdareActivity.this, registration);
        }

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawerOpen, R.string.drawerClose);
        toggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();
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
        MenuItem menuItem_unCheck_exams = navigationView.getMenu().findItem(R.id.edare_exams);
        countUnread_exams = (TextView) menuItem_unCheck_exams.getActionView();
        MenuItem menuItem_home = navigationView.getMenu().findItem(R.id.edare_home);
        menuItem_home.setChecked(true);
        menuItem_home.setCheckable(true);
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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        if (Common.currentPerson != null && Common.currentSTAGE != null && Common.currentPermission != null) {
            listenPersonFromDB(Common.currentPerson.getId());
            if (Common.currentPerson.getPermissions().size() == 1) {
                hideItem();
            }
            tv_person_Permission.setText(Common.ConvertPermissionToNameArabic(Common.currentPermission) + " " + Common.currentSTAGE);
            checkUnreadDataOfExamFromDB();
        } else {
            Common.currentSTAGE = Paper.book().read(Common.STAGE);
            Common.currentPerson = Paper.book().read(Common.PERSON);
            Common.currentPermission = Paper.book().read(Common.PERMISSION);
            if (Common.currentPerson != null) {
                listenPersonFromDB(Common.currentPerson.getId());
                if (Common.currentPerson.getPermissions().size() == 1) {
                    hideItem();
                }
                tv_person_Permission.setText(Common.ConvertPermissionToNameArabic(Common.currentPermission) + " " + Common.currentSTAGE);
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
        if (dialogMultipleAccounts != null && dialogMultipleAccounts.isShowing()) {
            dialogMultipleAccounts.dismiss();
        }
    }

    @SuppressLint("SetTextI18n")
    private void checkUnreadDataOfExamFromDB() {
        if (Common.currentSTAGE != null && Common.currentPerson != null) {
            registration = db.collection("Exam")
                    .whereEqualTo("statusAcceptance", 3)
                    .whereEqualTo("stage", Common.currentSTAGE)
                    .orderBy("year", Query.Direction.DESCENDING)
                    .orderBy("month", Query.Direction.DESCENDING)
                    .orderBy("day", Query.Direction.DESCENDING)
                    .whereEqualTo("isSeenExam.isSeenEdare", false)
                    .addSnapshotListener(this, (queryDocumentSnapshots, e) -> {
                        if (e != null) {
                            Log.w("sss", "listen:error" + e.getLocalizedMessage());
                            return;
                        }
                        if (queryDocumentSnapshots != null) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                countUnread_exams.setVisibility(View.VISIBLE);
                                countUnread_exams.setText("+" + queryDocumentSnapshots.size());
                            } else {
                                countUnread_exams.setVisibility(View.GONE);
                            }
                        }

                    });
        }
    }


    private void hideItem() {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.edare_switch_account).setVisible(false);
    }

    private void showItem() {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.edare_switch_account).setVisible(true);
    }

    public void moveFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(android.R.animator.fade_in,
                android.R.animator.fade_out).replace(R.id.frameLayout, fragment).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.edare_home:
                HomeFragment home = new HomeFragment();
                moveFragment(home);
                toolbar.setTitle("الصفحة الرئيسية");
                toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                drawerLayout.closeDrawers();
                menuItem.setChecked(true);
                menuItem.setCheckable(true);
                break;
            case R.id.edare_halakat:
                HalakatFragment halakat = new HalakatFragment();
                moveFragment(halakat);
                toolbar.setTitle("الحلقات");
                toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                drawerLayout.closeDrawers();
                menuItem.setChecked(true);
                menuItem.setCheckable(true);
                break;
            case R.id.edare_exams:
                ExamsFragment examsFragment = new ExamsFragment();
                moveFragment(examsFragment);
                toolbar.setTitle("الإختبارات");
                toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                drawerLayout.closeDrawers();
                menuItem.setChecked(true);
                menuItem.setCheckable(true);
                break;
            case R.id.edare_student:
                StudentFragment studentsFragment = new StudentFragment();
                moveFragment(studentsFragment);
                toolbar.setTitle("الطلاب");
                toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                drawerLayout.closeDrawers();
                menuItem.setChecked(true);
                menuItem.setCheckable(true);
                break;
            case R.id.edare_mohafezeen:
                MohafezFragment mohafez = new MohafezFragment();
                moveFragment(mohafez);
                toolbar.setTitle("المحفظين");
                toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                drawerLayout.closeDrawers();
                menuItem.setChecked(true);
                menuItem.setCheckable(true);
                break;
            case R.id.edare_stage_reports:
                CenterReportsFragment centerReportsFragment = new CenterReportsFragment();
                moveFragment(centerReportsFragment);
                toolbar.setTitle("تقارير المرحلة");
                toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                drawerLayout.closeDrawers();
                menuItem.setChecked(true);
                menuItem.setCheckable(true);
                break;
            case R.id.edare_switch_account:
                menuItem.setChecked(true);
                menuItem.setCheckable(true);
                showDialogMultipleAccounts();
                drawerLayout.closeDrawers();
                break;
            case R.id.edare_logout:
                menuItem.setChecked(true);
                menuItem.setCheckable(true);
                Common.SignOut(mauth, EdareActivity.this, registration);
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentUser == null) {
            Common.SignOut(mauth, EdareActivity.this, registration);
        }
    }

    @SuppressLint("SetTextI18n")
    private void listenPersonFromDB(String UID) {
        registration = db.collection("Person").document(UID).addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.w("sss", "listen:error" + e.getLocalizedMessage());
                return;
            }
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
                Paper.book().write(Common.PERSON, person);
                if (!person.isEnableAccount()) {
                    if (!this.isFinishing()) {
                        sweetAlertDialog_.showDialogError("لقد تم تعطيل حسابك , يرجى مراجعة إدارة التطبيق !")
                                .setConfirmButton("OK", sweetAlertDialog1 -> Common.SignOut(mauth, EdareActivity.this, registration));
                    }
                }

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
                                .setConfirmButton("OK", sweetAlertDialog -> Common.SignOut(mauth, EdareActivity.this, registration));
                    }
                }

            }
        });
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

        for (int i = 0; i < Common.currentPerson.getPermissions().size(); i++) {
            Person person = Common.currentPerson;
            AccountItem accountItem = new AccountItem();
            accountItem.setName(person.getFname() + " " + person.getMname() + " " + person.getLname());
            accountItem.setPermission(person.getPermissions().get(i));
            accountItem.setImage(person.getImage());
            accountItems.add(accountItem);
        }

        logout.setOnClickListener(view -> Common.SignOut(mauth, EdareActivity.this, registration));

        Multiple_accounts_Adapter adapter = new Multiple_accounts_Adapter(accountItems, this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getBaseContext(), 1));
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        builder.setCancelable(false);
        dialogMultipleAccounts = builder.create();
        dialogMultipleAccounts.show();
    }

}
