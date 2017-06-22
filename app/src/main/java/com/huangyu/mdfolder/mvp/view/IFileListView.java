package com.huangyu.mdfolder.mvp.view;

import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.huangyu.library.mvp.IBaseView;
import com.huangyu.mdfolder.bean.FileItem;

import java.util.List;

/**
 * Created by huangyu on 2017/5/22.
 */

public interface IFileListView extends IBaseView {

    void startRefresh();

    void stopRefresh();

    void addTab(String path);

    boolean removeTab();

    void removeAllTabs();

    void refreshData(boolean ifClearSelected);

    void refreshData(List<FileItem> filesList, boolean ifClearSelected);

    void finishAction();

    void showProgressDialog(String message);

    void hideProgressDialog();

    void showMessage(String message);

    void showError(String error);

    void showKeyboard(EditText editText);

    void hideKeyboard(EditText editText);

    void showInfoBottomSheet(FileItem fileItem, DialogInterface.OnCancelListener onCancelListener);

    View inflateAlertDialogLayout();

    TextInputLayout findTextInputLayout(View view);

    EditText findAlertDialogEditText(View view);

    AlertDialog showInputFileNameAlert(View view, DialogInterface.OnShowListener onShowListener);

    AlertDialog showNormalAlert(String message, String positiveString, DialogInterface.OnClickListener positiveClick);

    void closeFloatingActionMenu();

    String getResString(int resId);

}
