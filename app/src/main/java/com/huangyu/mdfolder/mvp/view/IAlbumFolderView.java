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
 * Created by huangyu on 2017/7/1.
 */

public interface IAlbumFolderView extends IBaseView {

    void showTabs();

    void startRefresh();

    void stopRefresh();

    void showError(String error);

    void refreshAlbum(ArrayList<FileItem> albumFolderList, int scrollY);

    void refreshData(ArrayList<FileItem> imageList, boolean ifClearSelected);

    void finishAction();

    void addTab(String s);

    String getResString(int res);

    void showMessage(String message);

    void showProgressDialog(String message);

    void hideProgressDialog();

    void showKeyboard(EditText editText);

    void hideKeyboard(EditText editText);

    void showInfoBottomSheet(FileItem fileItem, DialogInterface.OnCancelListener onCancelListener);

    View inflateFilenameInputDialogLayout();

    View inflatePasswordInputDialogLayout();

    TextInputLayout findTextInputLayout(View view);

    EditText findAlertDialogEditText(View view);

    AlertDialog showInputFileNameAlert(View view, DialogInterface.OnShowListener onShowListener);

    AlertDialog showNormalAlert(String message, String positiveString, DialogInterface.OnClickListener positiveClick);

}
