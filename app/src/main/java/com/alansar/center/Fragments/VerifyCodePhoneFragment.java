package com.alansar.center.Fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
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
import com.chaos.view.PinView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 */
public class VerifyCodePhoneFragment extends Fragment {
    private String mVerificationId;
    private FirebaseAuth mAuth;
    private Button btn_verify_code;
    private PinView pinView;
    private String PhoneNumber;
    private FirebaseFirestore db;
    private ArrayList<AccountItem> accountItems;
    private SweetAlertDialog_ sweetAlertDialog_;
    private AlertDialog dialogMultipleAccounts;


    public VerifyCodePhoneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view1 = inflater.inflate(R.layout.fragment_verify_code_phone, container, false);
        InitializeFields(view1);
        if (this.getArguments() != null && this.getArguments().getString("AuthCredentials") != null
                && this.getArguments().getString("PhoneNumber") != null) {
            mVerificationId = getArguments().getString("AuthCredentials");
            PhoneNumber = getArguments().getString("PhoneNumber");
        }

        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() == 6) {
                    validOTP(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        btn_verify_code.setOnClickListener(view -> validOTP(Objects.requireNonNull(pinView.getText()).toString().trim()));
        return view1;
    }


    private void VerifyPhoneofDatabase() {
        db.collection("Person")
                .whereEqualTo("phone", PhoneNumber)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Person person = queryDocumentSnapshots.toObjects(Person.class).get(0);
                        if (!person.getPermissions().isEmpty()) {
                            Common.currentPerson = person;
                            Paper.book().write(Common.ISLOGIN, true);
                            Paper.book().write(Common.PERSON, person);
                            sweetAlertDialog_.cancelDialog();
                            sweetAlertDialog_.showDialogSuccess("OK",
                                    "تمت عملية تسجيل الدخول إلى حسابك بنجاح !")
                                    .setConfirmButton("OK", sweetAlertDialog1 -> {
                                        sweetAlertDialog1.dismissWithAnimation();
                                        if (person.getPermissions().size() == 1) {
                                            getStageOfPersonFromDB(person.getPermissions().get(0), person.getId());
                                        } else {
                                            sweetAlertDialog1.dismissWithAnimation();
                                            sweetAlertDialog_.cancelDialog();
                                            showDialogMultipleAccounts();
                                        }
                                    });
                        } else {
                            if (getActivity() != null)
                                sweetAlertDialog_.showDialogError("لا يوجد لديك أي صلاحية لتسجيل الدخول إلى حسابك , راجع إدارة التطبيق")
                                        .setConfirmButton("OK", sweetAlertDialog -> Common.SignOut(mAuth, getActivity(), null));
                        }
                    } else {
                        sweetAlertDialog_.cancelDialog();
                        sweetAlertDialog_.showDialogError("لم يتم العثور على حساب في النظام يجب التأكد من رقم الهاتف المدخل ..");
                    }
                });
    }

    private void validOTP(String CodeOTP) {
        if (CodeOTP.isEmpty()) {
            sweetAlertDialog_.showDialogError("يجب تعبئة حقل كود التحقق");
        } else if (CodeOTP.length() != 6) {
            sweetAlertDialog_.showDialogError("يجب أن لا يقل كود التحقق عن 6 أرقام");
        } else {
            sweetAlertDialog_.showdialogProgress();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, CodeOTP);
            signInWithPhoneAuthCredential(credential);
        }
    }

    private void InitializeFields(View view) {
        mAuth = FirebaseAuth.getInstance();
        btn_verify_code = view.findViewById(R.id.btn_verify_code);
        pinView = view.findViewById(R.id.pinView);
        db = FirebaseFirestore.getInstance();
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
                });
    }

    private void showDialogMultipleAccounts() {
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
        if (getActivity() != null)
            logout.setOnClickListener(view -> Common.SignOut(mAuth, getActivity(), null));

        Multiple_accounts_Adapter adapter = new Multiple_accounts_Adapter(accountItems, getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        builder.setCancelable(false);
        dialogMultipleAccounts = builder.create();
        dialogMultipleAccounts.show();
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
                            });
                    break;
                case Common.PERMISSIONS_SUPER_VISOR:
                    db.collection("Moshref").document(UID).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    Common.currentSTAGE = documentSnapshot.getString("stage");
                                    Paper.book().write(Common.STAGE, Common.currentSTAGE);
                                    SendUserToMainActivity(Permission);
                                }
                            });
                    break;
                case Common.PERMISSIONS_TESTER:
                    db.collection("Tester").document(UID).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    Common.currentSTAGE = documentSnapshot.getString("stage");
                                    Paper.book().write(Common.STAGE, Common.currentSTAGE);
                                    SendUserToMainActivity(Permission);
                                }
                            });
                    break;
                case Common.PERMISSIONS_EDARE:
                    db.collection("Edare").document(UID).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    Common.currentSTAGE = documentSnapshot.getString("stage");
                                    Paper.book().write(Common.STAGE, Common.currentSTAGE);
                                    SendUserToMainActivity(Permission);
                                }
                            });
                    break;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dialogMultipleAccounts != null && dialogMultipleAccounts.isShowing()) {
            dialogMultipleAccounts.dismiss();
        }
    }
}