package com.huangyu.mdfolder.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.huangyu.mdfolder.R;

/**
 * Created by huangyu on 2017-5-22.
 */
public class AlertUtils {

    private AlertUtils() {
    }

    /**
     * 显示Toast
     *
     * @param context context
     * @param content 内容
     */
    public static void showToast(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示Snack
     *
     * @param view    view
     * @param content 内容
     */
    public static void showSnack(@NonNull View view, String content) {
        Snackbar.make(view, content, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * 显示提示框
     *
     * @param context       context
     * @param message       信息
     * @param positiveClick 肯定按钮事件
     * @param negativeClick 否定按钮事件
     * @return dialog
     */
    public static AlertDialog showAlert(Context context, String message, String positiveString, String negativeString, DialogInterface.OnClickListener positiveClick, DialogInterface.OnClickListener negativeClick) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).setMessage(message).setNeutralButton(context.getString(R.string.act_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton(positiveString, positiveClick).setNegativeButton(negativeString, negativeClick).create();
        alertDialog.show();
        return alertDialog;
    }

}
