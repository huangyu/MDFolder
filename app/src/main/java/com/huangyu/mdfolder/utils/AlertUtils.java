package com.huangyu.mdfolder.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.listener.OnAlertButtonClick;

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
     * 显示Snack
     *
     * @param view            view
     * @param content         内容
     * @param action          按钮操作
     * @param onClickListener 按钮事件
     */
    public static void showSnack(@NonNull View view, String content, String action, View.OnClickListener onClickListener) {
        Snackbar.make(view, content, Snackbar.LENGTH_SHORT).setDuration(Snackbar.LENGTH_INDEFINITE).setAction(action, onClickListener).show();
    }

    /**
     * 显示普通提示框
     *
     * @param context        context
     * @param message        信息
     * @param positiveString 肯定按钮信息
     * @param negativeString 否定按钮信息
     * @param positiveClick  肯定按钮事件
     * @param negativeClick  否定按钮事件
     * @return dialog
     */
    public static AlertDialog showNormalAlert(Context context, String message, String positiveString, String negativeString, DialogInterface.OnClickListener positiveClick, DialogInterface.OnClickListener negativeClick) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).setMessage(message).setNeutralButton(context.getString(R.string.act_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton(positiveString, positiveClick).setNegativeButton(negativeString, negativeClick).create();
        alertDialog.show();
        return alertDialog;
    }

    /**
     * 显示普通提示框
     *
     * @param context       context
     * @param message       信息
     * @param positiveClick 肯定按钮事件
     * @return dialog
     */
    public static AlertDialog showNormalAlert(Context context, String message, String positiveString, DialogInterface.OnClickListener positiveClick) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).setMessage(message).setNegativeButton(context.getString(R.string.act_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton(positiveString, positiveClick).create();
        alertDialog.show();
        return alertDialog;
    }

    /**
     * 显示自定义View提示框
     *
     * @param context       context
     * @param title         标题
     * @param positiveClick 肯定按钮事件
     * @return
     */
    public static AlertDialog showCustomAlert(final Context context, String title, View view, final OnAlertButtonClick positiveClick, final OnAlertButtonClick negativeClick) {
//        final AlertDialog alertDialog = new CustomAlertDialogBuilder(context).setTitle(title).setView(view).setPositiveButton(context.getString(R.string.act_confirm), null).setNegativeButton(context.getString(R.string.act_cancel), null).show();
        final AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle(title).setView(view).setPositiveButton(context.getString(R.string.act_confirm), null).setNegativeButton(context.getString(R.string.act_cancel), null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                Button positionButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (positiveClick.onClick()) {
                            alertDialog.dismiss();
                        }
                    }
                });
                Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (negativeClick.onClick()) {
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });
        alertDialog.show();
        return alertDialog;
    }

}
