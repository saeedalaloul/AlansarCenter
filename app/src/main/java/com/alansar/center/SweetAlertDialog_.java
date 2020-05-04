package com.alansar.center;

import android.content.Context;
import android.graphics.Color;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SweetAlertDialog_ {
    public SweetAlertDialog sweetAlertDialog;
    public Context context;

    public SweetAlertDialog_(Context context) {
        this.context = context;
    }

    public SweetAlertDialog showDialogSuccess(String titleText, String contentText) {
        sweetAlertDialog = new cn.pedant.SweetAlert.SweetAlertDialog(context, cn.pedant.SweetAlert.SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(titleText)
                .setContentText(contentText);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setCanceledOnTouchOutside(false);
        sweetAlertDialog.setConfirmButton("OK", SweetAlertDialog::dismissWithAnimation);
        sweetAlertDialog.show();
        return sweetAlertDialog;
    }

    public SweetAlertDialog showDialogError(String message) {
        sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(message);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setCanceledOnTouchOutside(false);
        sweetAlertDialog.show();
        return sweetAlertDialog;
    }

    public SweetAlertDialog showdialogProgress() {
        sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setTitleText("Loading");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setCanceledOnTouchOutside(false);
        sweetAlertDialog.show();

        return sweetAlertDialog;
    }

    public void cancelDialog() {
        if (sweetAlertDialog != null) {
            sweetAlertDialog.dismissWithAnimation();
        }
    }
}
