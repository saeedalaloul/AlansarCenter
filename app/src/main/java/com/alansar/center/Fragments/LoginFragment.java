package com.alansar.center.Fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alansar.center.Adapter.Multiple_accounts_Adapter;
import com.alansar.center.Common.Common;
import com.alansar.center.Edare.Activitys.EdareActivity;
import com.alansar.center.FButton;
import com.alansar.center.Models.AccountItem;
import com.alansar.center.Models.Person;
import com.alansar.center.Mohafez.Activitys.MohafezActivity;
import com.alansar.center.Moshref.Activity.MoshrefActivity;
import com.alansar.center.R;
import com.alansar.center.SweetAlertDialog_;
import com.alansar.center.administrator.Activitys.AdminActivity;
import com.alansar.center.supervisor_exams.Activitys.SuperVisorExamsActivity;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    public static PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private EditText mEdPhoneOTP;
    private Button mBtnLogin;
    private FirebaseAuth mAuth;
    private TextView mtvNotifyLogin;
    private FirebaseFirestore db;
    private String PhoneNumber;
    private ArrayList<AccountItem> accountItems;
    private SweetAlertDialog_ sweetAlertDialog_;
    private ListenerRegistration registration;


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view1 = inflater.inflate(R.layout.fragment_login, container, false);
        InitializeFields(view1);
        mBtnLogin.setOnClickListener(view -> {
            String phoneNumber = mEdPhoneOTP.getText().toString().trim();
            if (phoneNumber.isEmpty()) {
                mtvNotifyLogin.setText("يجب تعبئة حقل رقم الهاتف !");
                mtvNotifyLogin.setTextColor(Color.RED);
            } else if (phoneNumber.length() != 7) {
                mtvNotifyLogin.setText("يجب أن لا يقل عدد أرقام الهاتف عن  سبعة أرقام !");
                mtvNotifyLogin.setTextColor(Color.RED);
            } else {
                VerifyPhoneOfDatabase(phoneNumber);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                mtvNotifyLogin.setText(e.getMessage());
                mtvNotifyLogin.setTextColor(Color.RED);
                Log.d("sss", Objects.requireNonNull(e.getMessage()));
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // Save verification ID and resending token so we can use them later
                if (getActivity() != null) {
                    VerifyCodePhoneFragment newFragment = new VerifyCodePhoneFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("AuthCredentials", verificationId);
                    bundle.putString("PhoneNumber", PhoneNumber);
                    newFragment.setArguments(bundle);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_login_layout, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        };
        return view1;
    }

    private void VerifyPhoneOfDatabase(String phoneNumber) {
        sweetAlertDialog_.showdialogProgress();
        String fullPhoneNumber = "059" + phoneNumber;
        PhoneNumber = fullPhoneNumber;
        registration = db.collection("Person")
                .whereEqualTo("phone", fullPhoneNumber)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.w("sss", "listen:error" + e.getLocalizedMessage());
                        return;
                    }
                    if (queryDocumentSnapshots != null) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            Person person = queryDocumentSnapshots.toObjects(Person.class).get(0);
                            if (person.isEnableAccount()) {
                                VerifyPhone(fullPhoneNumber);
                            } else {
                                sweetAlertDialog_.cancelDialog();
                                sweetAlertDialog_.showDialogError("عذرا, الحساب معطل , راجع إدارة التطبيق ...");
                            }
                        } else {
                            sweetAlertDialog_.cancelDialog();
                            sweetAlertDialog_.showDialogError("لم يتم العثور على حساب في النظام يجب التأكد من رقم الهاتف المدخل ..");
                        }
                    }
                });
    }

    private void InitializeFields(View view) {
        mBtnLogin = view.findViewById(R.id.btn_login);
        mEdPhoneOTP = view.findViewById(R.id.et_phone);
        mtvNotifyLogin = view.findViewById(R.id.tv_notify_login);
        db = FirebaseFirestore.getInstance();
        ///////////////////
        mAuth = FirebaseAuth.getInstance();
        accountItems = new ArrayList<>();
        sweetAlertDialog_ = new SweetAlertDialog_(getContext());
        Paper.init(Objects.requireNonNull(getContext()));
    }

    @SuppressLint("SetTextI18n")
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), task -> {
                    if (task.isSuccessful()) {
                        VerifyPhoneofDatabase();
                    } else {
                        sweetAlertDialog_.cancelDialog();
                        sweetAlertDialog_.showDialogError(Objects.requireNonNull(task.getException()).getMessage());
                        Log.d("sss", Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
                    }
                }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (registration != null) {
            registration.remove();
        }
    }

    private void VerifyPhone(String phoneNumber) {
        if (getActivity() != null) {
            String fullPhoneNumber = "+972" + phoneNumber.substring(1, 10);
            Log.d("sss", fullPhoneNumber);
            sweetAlertDialog_.cancelDialog();
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    fullPhoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    getActivity(),               // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallbacks
        }
    }

    private void VerifyPhoneofDatabase() {
        db.collection("Person")
                .whereEqualTo("phone", PhoneNumber)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                Person person = queryDocumentSnapshots.toObjects(Person.class).get(0);
                if (!person.getPermissions().isEmpty()) {
                    Common.currentPerson = person;
                    Paper.book().write(Common.ISLOGIN, true);
                    Paper.book().write(Common.PERSON, person);
                    sweetAlertDialog_.cancelDialog();
                    sweetAlertDialog_.showDialogSuccess("OK", "تمت عملية تسجيل الدخول إلى حسابك بنجاح !").setConfirmButton("OK", sweetAlertDialog1 -> {
                        sweetAlertDialog1.dismissWithAnimation();
                        if (person.getPermissions().size() == 1) {
                            getStageOfPersonFromDB(person.getPermissions().get(0), person.getId());
                        } else {
                            showDialogMultipleAccounts();
                        }
                    });
                } else {
                    if (getActivity() != null) {
                        sweetAlertDialog_.showDialogError("لا يوجد لديك أي صلاحية لتسجيل الدخول إلى حسابك , راجع إدارة التطبيق")
                                .setConfirmButton("OK", sweetAlertDialog -> Common.SignOut(mAuth, getActivity(), registration));
                    }
                }
            } else {
                sweetAlertDialog_.cancelDialog();
                sweetAlertDialog_.showDialogError("لم يتم العثور على حساب في النظام يجب التأكد من رقم الهاتف المدخل ..");
            }
        }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
    }

    private void showDialogMultipleAccounts() {
        sweetAlertDialog_.showdialogProgress();
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        LayoutInflater inflater = LayoutInflater.from(getContext());
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
        if (getActivity() != null) {
            logout.setOnClickListener(view -> Common.SignOut(mAuth, getActivity(), registration));
        }
        Multiple_accounts_Adapter adapter = new Multiple_accounts_Adapter(accountItems, getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        new Handler().postDelayed(() -> sweetAlertDialog_.cancelDialog(), 5000);
        builder.setCancelable(false);
        builder.show();

    }

    private void SendUserToMainActivity(String permission) {
        if (getActivity() != null && getContext() != null) {
            switch (permission) {
                case Common.PERMISSIONS_EDARE:
                    Paper.book().write(Common.PERMISSION, permission);
                    Common.currentPermission = permission;
                    getContext().startActivity(new Intent(getContext(), EdareActivity.class)
                            .putExtra("Permission", permission)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    getActivity().finish();
                    break;
                case Common.PERMISSIONS_SUPER_VISOR:
                    Paper.book().write(Common.PERMISSION, permission);
                    Common.currentPermission = permission;
                    getContext().startActivity(new Intent(getContext(), MoshrefActivity.class)
                            .putExtra("Permission", permission)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    getActivity().finish();
                    break;
                case Common.PERMISSIONS_ADMIN:
                    Paper.book().write(Common.PERMISSION, permission);
                    Common.currentPermission = permission;
                    getContext().startActivity(new Intent(getContext(), AdminActivity.class)
                            .putExtra("Permission", permission)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK))
                    ;
                    getActivity().finish();

                    break;
                case Common.PERMISSIONS_MOHAFEZ:
                    Paper.book().write(Common.PERMISSION, permission);
                    Common.currentPermission = permission;
                    getContext().startActivity(new Intent(getContext(), MohafezActivity.class)
                            .putExtra("Permission", permission)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK))
                    ;
                    getActivity().finish();
                    break;
                case Common.PERMISSIONS_SUPER_VISOR_EXAMS:
                    Paper.book().write(Common.PERMISSION, permission);
                    Common.currentPermission = permission;
                    getContext().startActivity(new Intent(getContext(), SuperVisorExamsActivity.class)
                            .putExtra("Permission", permission)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK))
                    ;
                    getActivity().finish();
                    break;
            }
        }
    }

    private void getStageOfPersonFromDB(String Permission, String UID) {
        if (Permission != null && UID != null) {
            switch (Permission) {
                case Common.PERMISSIONS_MOHAFEZ:
                    db.collection("Mohafez").document(UID).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    Common.currentSTAGE = documentSnapshot.getString("stage");
                                    Common.currentGroupId = documentSnapshot.getString("groupId");
                                    Paper.book().write(Common.STAGE, Common.currentSTAGE);
                                    Paper.book().write(Common.GROUPID, Common.currentGroupId);
                                    SendUserToMainActivity(Permission);
                                }
                            }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
                    break;
                case Common.PERMISSIONS_SUPER_VISOR:
                    db.collection("Moshref").document(UID).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    Common.currentSTAGE = documentSnapshot.getString("stage");
                                    Paper.book().write(Common.STAGE, Common.currentSTAGE);
                                    SendUserToMainActivity(Permission);
                                }
                            }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
                    break;
                case Common.PERMISSIONS_TESTER:
                    db.collection("Tester").document(UID).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    Common.currentSTAGE = documentSnapshot.getString("stage");
                                    Paper.book().write(Common.STAGE, Common.currentSTAGE);
                                    SendUserToMainActivity(Permission);
                                }
                            }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
                    break;
                case Common.PERMISSIONS_EDARE:
                    db.collection("Edare").document(UID).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    Common.currentSTAGE = documentSnapshot.getString("stage");
                                    Paper.book().write(Common.STAGE, Common.currentSTAGE);
                                    SendUserToMainActivity(Permission);
                                }
                            }).addOnFailureListener(e -> Log.d("sss", "" + e.getLocalizedMessage()));
                    break;
            }
        }
    }

}

