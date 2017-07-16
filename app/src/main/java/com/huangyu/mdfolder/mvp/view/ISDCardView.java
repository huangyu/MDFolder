package com.huangyu.mdfolder.mvp.view;

import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.huangyu.library.mvp.IBaseView;
import com.huangyu.mdfolder.bean.FileItem;

import java.util.ArrayList;

/**
 * Created by huangyu on 2017/5/22.
 */

public interface ISDCardView extends IBaseView {

    void startRefresh();

    void stopRefresh();

    void addTab(String path);

    boolean removeTab();

    void removeAllTabs();

    void hideTabs();

    void showTabs();

    void refreshData(boolean ifClearSelected, final int position);

    void refreshData(ArrayList<FileItem> filesList, boolean ifClearSelected, final int scrollY);

    void finishAction();

    void showProgressDialog(String message);

    void hideProgressDialog();

    void showMessage(String message);

    void showError(String error);

    void showKeyboard(EditText editText);

    void hideKeyboard(EditText editText);

    void showInfoBottomSheet(FileItem fileItem, DialogInterface.OnCancelListener onCancelListener);

    View inflateFilenameInputDialogLayout();

    View inflatePasswordInputDialogLayout();

    TextInputLayout findTextInputLayout(View view);

    EditText findAlertDialogEditText(View view);

    AlertDialog showInputFileNameAlert(View view, DialogInterface.OnShowListener onShowListener);

    AlertDialog showNormalAlert(String message, String positiveString, DialogInterface.OnClickListener positiveClick);

    void closeFloatingActionMenu();

    String getResString(int resId);

}
