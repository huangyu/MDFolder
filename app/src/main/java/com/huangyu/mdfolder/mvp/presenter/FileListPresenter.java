package com.huangyu.mdfolder.mvp.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.huangyu.library.app.BaseApplication;
import com.huangyu.library.mvp.BasePresenter;
import com.huangyu.library.util.FileUtils;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.app.Constants;
import com.huangyu.mdfolder.bean.FileItem;
import com.huangyu.mdfolder.mvp.model.DocumentFileModel;
import com.huangyu.mdfolder.mvp.model.FileListModel;
import com.huangyu.mdfolder.mvp.model.FileModel;
import com.huangyu.mdfolder.mvp.view.IFileListView;
import com.huangyu.mdfolder.ui.activity.FileListActivity;
import com.huangyu.mdfolder.utils.CompressUtils;
import com.huangyu.mdfolder.utils.DocumentFileUtils;
import com.huangyu.mdfolder.utils.MediaScanUtils;
import com.huangyu.mdfolder.utils.MimeTypeUtils;
import com.huangyu.mdfolder.utils.SPUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;

import static com.huangyu.mdfolder.app.Constants.UNINSTALL_REQUEST_CODE;

/**
 * Created by huangyu on 2017/5/22.
 */

public class FileListPresenter extends BasePresenter<IFileListView> {

    private DocumentFileModel mDocumentFileModel;
    private FileListModel mFileListModel;
    private FileModel mFileModel;
    private Stack<String> mFileStack;   // 文件路径栈
    private Stack<Integer> mScrollYStack;   // 列表position栈

    private String mCurrentPath; // 当前路径
    private int mBeforeScrollY; // 当前position
    public int mEditType;   // 当前编辑状态
    public int mSelectType;   // 当前选择类型
    public int mSortType;   // 当前排序类型
    public int mOrderType;  // 当前升降序类型
    public boolean isPasteActonMode; // 是否是复制粘贴状态
    public boolean isActionModeActive;// 是否是ActionMode状态

    @Override
    public void create() {
        mDocumentFileModel = new DocumentFileModel();
        mFileListModel = new FileListModel();
        mFileModel = new FileModel();
        mFileStack = new Stack<>();
        mScrollYStack = new Stack<>();
        mEditType = Constants.EditType.NONE;
        mSelectType = Constants.SelectType.MENU_FILE;
        mSortType = SPUtils.getSortType();
        mOrderType = SPUtils.getOrderType();
    }

    /**
     * 获取根目录文件列表
     */
    public void onLoadRootFileList(final String searchStr) {
        Subscription subscription = Observable.defer(new Func0<Observable<ArrayList<FileItem>>>() {
            @Override
            public Observable<ArrayList<FileItem>> call() {
                return Observable.just(getCurrentFileList(searchStr));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mCurrentPath = mFileListModel.getRootPath();
                        mFileStack.clear();
                        mFileStack.push(mCurrentPath);
                        mBeforeScrollY = 0;
                        mScrollYStack.clear();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<FileItem>>() {
                    @Override
                    public void onStart() {
                        mView.showTabs();
                        mView.startRefresh();
                    }

                    @Override
                    public void onNext(ArrayList<FileItem> fileList) {
                        mView.removeAllTabs();
                        mView.addTab(mCurrentPath);
                        mView.refreshView(fileList, false, 0);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError(e.getMessage());
                        onCompleted();
                    }

                    @Override
                    public void onCompleted() {
                        mView.stopRefresh();
                    }
                });
        mRxManager.add(subscription);
    }

    /**
     * 获取存储器文件列表
     */
    public void onLoadStorageFileList(final boolean isOuter, final String searchStr) {
        Subscription subscription = Observable.defer(new Func0<Observable<ArrayList<FileItem>>>() {
            @Override
            public Observable<ArrayList<FileItem>> call() {
                return Observable.just(getCurrentFileList(searchStr));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mCurrentPath = mFileListModel.getStorageCardPath(isOuter);
                        mFileStack.clear();
                        mFileStack.push(mCurrentPath);
                        mBeforeScrollY = 0;
                        mScrollYStack.clear();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<FileItem>>() {
                    @Override
                    public void onStart() {
                        mView.showTabs();
                        mView.startRefresh();
                    }

                    @Override
                    public void onNext(ArrayList<FileItem> fileList) {
                        mView.removeAllTabs();
                        mView.addTab(mCurrentPath);
                        mView.refreshView(fileList, false, 0);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError(e.getMessage());
                        onCompleted();
                    }

                    @Override
                    public void onCompleted() {
                        mView.stopRefresh();
                    }
                });
        mRxManager.add(subscription);
    }

    /**
     * 获取下载文件列表
     */
    public void onLoadDownloadFileList(final String searchStr) {
        Subscription subscription = Observable.defer(new Func0<Observable<ArrayList<FileItem>>>() {
            @Override
            public Observable<ArrayList<FileItem>> call() {
                return Observable.just(getCurrentFileList(searchStr));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mCurrentPath = mFileListModel.getDownloadPath();
                        mFileStack.clear();
                        mFileStack.push(mCurrentPath);
                        mBeforeScrollY = 0;
                        mScrollYStack.clear();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<FileItem>>() {
                    @Override
                    public void onStart() {
                        mView.showTabs();
                        mView.startRefresh();
                    }

                    @Override
                    public void onNext(ArrayList<FileItem> fileList) {
                        mView.removeAllTabs();
                        mView.addTab(mCurrentPath);
                        mView.refreshView(fileList, false, 0);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError(e.getMessage());
                        onCompleted();
                    }

                    @Override
                    public void onCompleted() {
                        mView.stopRefresh();
                    }
                });
        mRxManager.add(subscription);
    }

    /**
     * 获取不同类型文件列表
     */
    public void onLoadMultiTypeFileList(final String searchStr) {
        Subscription subscription = Observable.defer(new Func0<Observable<ArrayList<FileItem>>>() {
            @Override
            public Observable<ArrayList<FileItem>> call() {
                return Observable.just(getCurrentFileList(searchStr));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mCurrentPath = "";
                        mFileStack.clear();
                        mScrollYStack.clear();
                        mBeforeScrollY = 0;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<FileItem>>() {
                    @Override
                    public void onStart() {
                        mView.showTabs();
                        mView.startRefresh();
                    }

                    @Override
                    public void onNext(ArrayList<FileItem> fileList) {
                        mView.removeAllTabs();
                        switch (mSelectType) {
                            case Constants.SelectType.MENU_RECENT:
                                mView.addTab(mView.getResString(R.string.menu_recent) + "  " + fileList.size());
                                break;
                            case Constants.SelectType.MENU_DOCUMENT:
                                mView.addTab(mView.getResString(R.string.menu_document) + "  " + fileList.size());
                                break;
                            case Constants.SelectType.MENU_PHOTO:
                                mView.addTab(mView.getResString(R.string.menu_image) + "  " + fileList.size());
                                break;
                            case Constants.SelectType.MENU_MUSIC:
                                mView.addTab(mView.getResString(R.string.menu_audio) + "  " + fileList.size());
                                break;
                            case Constants.SelectType.MENU_VIDEO:
                                mView.addTab(mView.getResString(R.string.menu_video) + "  " + fileList.size());
                                break;
                            case Constants.SelectType.MENU_APK:
                                mView.addTab(mView.getResString(R.string.menu_apk) + "  " + fileList.size());
                                break;
                            case Constants.SelectType.MENU_ZIP:
                                mView.addTab(mView.getResString(R.string.menu_compress_package) + "  " + fileList.size());
                                break;
                            case Constants.SelectType.MENU_APPS:
                                mView.addTab(mView.getResString(R.string.menu_apps) + "  " + fileList.size());
                                break;
                        }
                        mView.refreshView(fileList, false, 0);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError(e.getMessage());
                        onCompleted();
                    }

                    @Override
                    public void onCompleted() {
                        mView.stopRefresh();
                    }
                });
        mRxManager.add(subscription);
    }

    /**
     * 全局查询
     */
    public void onSearchFileList(final String searchStr) {
        Subscription subscription = Observable.defer(new Func0<Observable<ArrayList<FileItem>>>() {
            @Override
            public Observable<ArrayList<FileItem>> call() {
                return Observable.just(mFileListModel.getGlobalFileListBySearch(searchStr, mContext.getContentResolver()));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<FileItem>>() {
                    @Override
                    public void onStart() {
                        mView.hideTabs();
                        mView.startRefresh();
                    }

                    @Override
                    public void onNext(ArrayList<FileItem> fileList) {
                        mView.refreshView(fileList, false, 0);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError(e.getMessage());
                        onCompleted();
                    }

                    @Override
                    public void onCompleted() {
                        mView.stopRefresh();
                    }
                });
        mRxManager.add(subscription);
    }

    /**
     * 刷新界面（下拉）
     */
    public void onRefreshInSwipe(final String searchStr, final boolean ifClearSelected) {
        Subscription subscription = Observable.defer(new Func0<Observable<ArrayList<FileItem>>>() {
            @Override
            public Observable<ArrayList<FileItem>> call() {
                return Observable.just(getCurrentFileList(searchStr));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<FileItem>>() {
                    @Override
                    public void onStart() {
                        mView.showTabs();
                        mView.startRefresh();
                    }

                    @Override
                    public void onNext(ArrayList<FileItem> fileList) {
                        switch (mSelectType) {
                            case Constants.SelectType.MENU_DOCUMENT:
                                mView.removeAllTabs();
                                mView.addTab(mView.getResString(R.string.menu_document) + "  " + fileList.size());
                                break;
                            case Constants.SelectType.MENU_PHOTO:
                                mView.removeAllTabs();
                                mView.addTab(mView.getResString(R.string.menu_image) + "  " + fileList.size());
                                break;
                            case Constants.SelectType.MENU_MUSIC:
                                mView.removeAllTabs();
                                mView.addTab(mView.getResString(R.string.menu_audio) + "  " + fileList.size());
                                break;
                            case Constants.SelectType.MENU_VIDEO:
                                mView.removeAllTabs();
                                mView.addTab(mView.getResString(R.string.menu_video) + "  " + fileList.size());
                                break;
                            case Constants.SelectType.MENU_APK:
                                mView.removeAllTabs();
                                mView.addTab(mView.getResString(R.string.menu_apk) + "  " + fileList.size());
                                break;
                            case Constants.SelectType.MENU_ZIP:
                                mView.removeAllTabs();
                                mView.addTab(mView.getResString(R.string.menu_compress_package) + "  " + fileList.size());
                                break;
                            case Constants.SelectType.MENU_APPS:
                                mView.removeAllTabs();
                                mView.addTab(mView.getResString(R.string.menu_apps) + "  " + fileList.size());
                                break;
                        }
                        mView.refreshView(fileList, ifClearSelected, 0);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError(e.getMessage());
                        onCompleted();
                    }

                    @Override
                    public void onCompleted() {
                        if (mEditType == Constants.EditType.NONE) {
                            mView.finishAction();
                        }
                        mView.stopRefresh();
                    }
                });
        mRxManager.add(subscription);
    }

    /**
     * 刷新界面
     */
    public void onRefresh(final String searchStr, final boolean ifClearSelected, final int scrollY) {
        Subscription subscription = Observable.defer(new Func0<Observable<ArrayList<FileItem>>>() {
            @Override
            public Observable<ArrayList<FileItem>> call() {
                return Observable.just(getCurrentFileList(searchStr));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<FileItem>>() {
                    @Override
                    public void onStart() {
                        mView.showTabs();
                        mView.startRefresh();
                    }

                    @Override
                    public void onNext(ArrayList<FileItem> fileList) {
                        mView.refreshView(fileList, ifClearSelected, scrollY);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError(e.getMessage());
                        onCompleted();
                    }

                    @Override
                    public void onCompleted() {
                        if (mEditType == Constants.EditType.NONE) {
                            mView.finishAction();
                        }
                        mView.stopRefresh();
                    }
                });
        mRxManager.add(subscription);
    }

    /**
     * 新增文件（只支持单个文件）
     */
    public void onAddFile() {
        if (mSelectType != Constants.SelectType.MENU_FILE && mSelectType != Constants.SelectType.MENU_DOWNLOAD && mSelectType != Constants.SelectType.MENU_SDCARD) {
            mView.showMessage(mView.getResString(R.string.tips_add_file_error));
            return;
        }

        final View view = mView.inflateFilenameInputDialogLayout();
        final TextInputLayout textInputLayout = mView.findTextInputLayout(view);
        final EditText editText = mView.findAlertDialogEditText(view);
        mView.showKeyboard(mView.findAlertDialogEditText(view));
        mView.showInputFileNameAlert(view, new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button positionButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String filename = editText.getText().toString();
                        if (TextUtils.isEmpty(filename)) {
                            textInputLayout.setEnabled(true);
                            textInputLayout.setError(mView.getResString(R.string.tips_file_name_empty));
                            textInputLayout.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    textInputLayout.setError(null);
                                    textInputLayout.setErrorEnabled(false);
                                }
                            }, 2000);
                            return;
                        }
                        Subscription subscription = Observable.just(editText.getText().toString())
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .observeOn(Schedulers.io())
                                .map(new Func1<String, String>() {
                                    @Override
                                    public String call(String fileName) {
                                        return mCurrentPath + File.separator + fileName;
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .flatMap(new Func1<String, Observable<String>>() {
                                    @Override
                                    public Observable<String> call(String filePath) {
                                        Observable<Boolean> observable1 = Observable.just(filePath).exists(new Func1<String, Boolean>() {
                                            @Override
                                            public Boolean call(String filePath) {
                                                return mFileModel.isFileExists(filePath);
                                            }
                                        });

                                        Observable<Boolean> observable2 = Observable.just(filePath).exists(new Func1<String, Boolean>() {
                                            @Override
                                            public Boolean call(String filePath) {
                                                return mFileModel.isFolderExists(filePath);
                                            }
                                        });

                                        Observable<String> observable3 = Observable.just(filePath);

                                        return Observable.zip(observable1, observable2, observable3, new Func3<Boolean, Boolean, String, String>() {
                                            @Override
                                            public String call(Boolean isFileExists, Boolean isFolderExists, String filePath) {
                                                if (isFileExists) {
                                                    mView.showMessage(mView.getResString(R.string.tips_file_exist));
                                                } else if (isFolderExists) {
                                                    mView.showMessage(mView.getResString(R.string.tips_folder_exist));
                                                } else {
                                                    return filePath;
                                                }
                                                return null;
                                            }
                                        });
                                    }
                                })
                                .subscribe(new Subscriber<String>() {
                                    @Override
                                    public void onStart() {
                                        dialog.dismiss();
                                        mView.showProgressDialog(mContext.getString(R.string.tips_handling));
                                    }

                                    @Override
                                    public void onNext(String filePath) {
                                        if (filePath == null) {
                                            return;
                                        }
                                        if (mSelectType == Constants.SelectType.MENU_SDCARD) {
                                            DocumentFile documentFile = DocumentFileUtils.getDocumentFile(new File(filePath), false);
                                            mDocumentFileModel.addFile(documentFile, "application/octet-stream", filename);
                                            mView.showMessage(mView.getResString(R.string.tips_add_file_successfully));
                                            mView.addData(transformFile(new File(filePath)));
                                            mView.clearSelectedState();
                                        } else {
                                            if (mFileModel.addFile(filePath)) {
                                                scanFile(filePath);
                                                mView.showMessage(mView.getResString(R.string.tips_add_file_successfully));
                                                mView.addData(transformFile(new File(filePath)));
                                                mView.clearSelectedState();
                                            } else {
                                                mView.showMessage(mView.getResString(R.string.tips_add_file_error));
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        mView.showError(e.getMessage());
                                        onCompleted();
                                    }

                                    @Override
                                    public void onCompleted() {
                                        mView.hideProgressDialog();
                                        mView.hideKeyboard(mView.findAlertDialogEditText(view));
                                        mView.closeFloatingActionMenu();
                                    }
                                });
                        mRxManager.add(subscription);
                    }
                });
                Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mView.closeFloatingActionMenu();
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    /**
     * 新增文件夹（只支持单个文件）
     */
    public void onAddFolder() {
        if (mSelectType != Constants.SelectType.MENU_FILE && mSelectType != Constants.SelectType.MENU_DOWNLOAD && mSelectType != Constants.SelectType.MENU_SDCARD) {
            mView.showMessage(mView.getResString(R.string.tips_add_folder_error));
            return;
        }

        final View view = mView.inflateFilenameInputDialogLayout();
        final TextInputLayout textInputLayout = mView.findTextInputLayout(view);
        final EditText editText = mView.findAlertDialogEditText(view);
        mView.showKeyboard(mView.findAlertDialogEditText(view));
        mView.showInputFileNameAlert(view, new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button positionButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String filename = editText.getText().toString();
                        if (TextUtils.isEmpty(filename)) {
                            textInputLayout.setEnabled(true);
                            textInputLayout.setError(mView.getResString(R.string.tips_file_name_empty));
                            textInputLayout.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    textInputLayout.setError(null);
                                    textInputLayout.setErrorEnabled(false);
                                }
                            }, 2000);
                            return;
                        }
                        Subscription subscription = Observable.just(editText.getText().toString())
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .observeOn(Schedulers.io())
                                .map(new Func1<String, String>() {
                                    @Override
                                    public String call(String fileName) {
                                        return mCurrentPath + File.separator + fileName;
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .flatMap(new Func1<String, Observable<String>>() {
                                    @Override
                                    public Observable<String> call(String filePath) {
                                        Observable<Boolean> observable1 = Observable.just(filePath).exists(new Func1<String, Boolean>() {
                                            @Override
                                            public Boolean call(String filePath) {
                                                return mFileModel.isFileExists(filePath);
                                            }
                                        });

                                        Observable<Boolean> observable2 = Observable.just(filePath).exists(new Func1<String, Boolean>() {
                                            @Override
                                            public Boolean call(String filePath) {
                                                return mFileModel.isFolderExists(filePath);
                                            }
                                        });

                                        Observable<String> observable3 = Observable.just(filePath);

                                        return Observable.zip(observable1, observable2, observable3, new Func3<Boolean, Boolean, String, String>() {
                                            @Override
                                            public String call(Boolean isFileExists, Boolean isFolderExists, String filePath) {
                                                if (isFileExists) {
                                                    mView.showMessage(mView.getResString(R.string.tips_file_exist));
                                                } else if (isFolderExists) {
                                                    mView.showMessage(mView.getResString(R.string.tips_folder_exist));
                                                } else {
                                                    return filePath;
                                                }
                                                return null;
                                            }
                                        });
                                    }
                                })
                                .subscribe(new Subscriber<String>() {
                                    @Override
                                    public void onStart() {
                                        dialog.dismiss();
                                        mView.showProgressDialog(mContext.getString(R.string.tips_handling));
                                    }

                                    @Override
                                    public void onNext(String filePath) {
                                        if (filePath == null) {
                                            return;
                                        }
                                        if (mSelectType == Constants.SelectType.MENU_SDCARD) {
                                            DocumentFileUtils.getDocumentFile(new File(filePath), true);
                                            mView.showMessage(mView.getResString(R.string.tips_add_folder_successfully));
                                            mView.addData(transformFile(new File(filePath)));
                                            mView.clearSelectedState();
                                        } else {
                                            if (mFileModel.addFolder(filePath)) {
                                                scanFile(filePath);
                                                mView.showMessage(mView.getResString(R.string.tips_add_folder_successfully));
                                                mView.addData(transformFile(new File(filePath)));
                                                mView.clearSelectedState();
                                            } else {
                                                mView.showMessage(mView.getResString(R.string.tips_add_folder_error));
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        mView.showError(e.getMessage());
                                        onCompleted();
                                    }

                                    @Override
                                    public void onCompleted() {
                                        mView.hideProgressDialog();
                                        mView.hideKeyboard(mView.findAlertDialogEditText(view));
                                        mView.closeFloatingActionMenu();
                                    }
                                });
                        mRxManager.add(subscription);
                    }
                });
                Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mView.closeFloatingActionMenu();
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    /**
     * 重命名文件（只支持单个文件）
     *
     * @param fileList 文件列表
     */
    public void onRenameFile(final ArrayList<FileItem> fileList, final ArrayList<Integer> selectedItemList) {
        if (fileList.size() != 1 || selectedItemList.size() != 1) {
            mView.showMessage(mView.getResString(R.string.tips_choose_one_file));
        } else {
            final View view = mView.inflateFilenameInputDialogLayout();
            final TextInputLayout textInputLayout = mView.findTextInputLayout(view);
            final EditText editText = mView.findAlertDialogEditText(view);
            final FileItem fileItem = fileList.get(0);
            final String filename = fileItem.getName();
            final String filePath = fileItem.getPath();
            editText.setText(filename);
            if (filename.contains(".")) {
                editText.setSelection(0, filename.lastIndexOf("."));
            } else {
                editText.setSelection(0, filename.length());
            }
            mView.showKeyboard(mView.findAlertDialogEditText(view));
            mView.showInputFileNameAlert(view, new DialogInterface.OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog) {
                    Button positionButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    positionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String filename = editText.getText().toString();
                            if (TextUtils.isEmpty(filename)) {
                                textInputLayout.setEnabled(true);
                                textInputLayout.setError(mView.getResString(R.string.tips_file_name_empty));
                                textInputLayout.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        textInputLayout.setError(null);
                                        textInputLayout.setErrorEnabled(false);
                                    }
                                }, 2000);
                                return;
                            }
                            Subscription subscription = Observable.just(null)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(Schedulers.io())
                                    .map(new Func1<Object, Boolean>() {
                                        @Override
                                        public Boolean call(Object o) {
                                            boolean result;
                                            if (mSelectType == Constants.SelectType.MENU_SDCARD) {
                                                DocumentFile documentFile = DocumentFileUtils.getDocumentFile(new File(filePath), fileItem.isDirectory());
                                                result = mDocumentFileModel.renameFile(documentFile, filename);
                                            } else {
                                                result = mFileModel.renameFile(filePath, filename);
                                            }
                                            String newPath = new File(filePath).getParent() + File.separator + filename;
                                            if (result) {
                                                SPUtils.removeFileRemark(filePath);
                                                MediaScanUtils.renameMediaFile(mContext, filePath, newPath);
                                            }
                                            return result;
                                        }
                                    })
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Subscriber<Boolean>() {
                                        @Override
                                        public void onStart() {
                                            dialog.dismiss();
                                            mView.showProgressDialog(mContext.getString(R.string.tips_handling));
                                        }

                                        @Override
                                        public void onNext(Boolean result) {
                                            if (result) {
                                                String newPath = new File(filePath).getParent() + File.separator + filename;
                                                int position = selectedItemList.get(0);
                                                mView.changeData(transformFile(new File(newPath)), position);
                                                mView.clearSelectedState();
                                                mView.showMessage(mView.getResString(R.string.tips_rename_successfully));
                                            } else {
                                                mView.showMessage(mView.getResString(R.string.tips_rename_in_error));
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            mView.showError(e.getMessage());
                                            onCompleted();
                                        }

                                        @Override
                                        public void onCompleted() {
                                            mView.hideProgressDialog();
                                            mView.finishAction();
                                        }
                                    });
                            mRxManager.add(subscription);
                        }
                    });
                    Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                    negativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            });
        }
    }

    /**
     * 备注文件（只支持单个文件）
     *
     * @param fileList 文件列表
     */
    public void onRemark(final ArrayList<FileItem> fileList, final ArrayList<Integer> selectedItemList) {
        if (fileList.size() != 1 || selectedItemList.size() != 1) {
            mView.showMessage(mView.getResString(R.string.tips_choose_one_file));
        } else {
            final View view = mView.inflateFilenameInputDialogLayout();
            final TextInputLayout textInputLayout = mView.findTextInputLayout(view);
            final EditText editText = mView.findAlertDialogEditText(view);
            final String fileRemark = fileList.get(0).getRemark();
            final String filePath = fileList.get(0).getPath();
            textInputLayout.setHint(mView.getResString(R.string.tips_input_file_remark));
            if (!TextUtils.isEmpty(fileRemark)) {
                editText.setText(fileRemark);
                editText.setSelection(0, fileRemark.length());
            }
            mView.showKeyboard(mView.findAlertDialogEditText(view));
            mView.showInputFileNameAlert(view, new DialogInterface.OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog) {
                    Button positionButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    positionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String remark = editText.getText().toString();
                            Subscription subscription = Observable.just(null)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Subscriber<Object>() {
                                        @Override
                                        public void onStart() {
                                            dialog.dismiss();
                                            mView.showProgressDialog(mContext.getString(R.string.tips_handling));
                                        }

                                        @Override
                                        public void onNext(Object result) {
                                            SPUtils.setFileRemark(filePath, remark);
                                            int position = selectedItemList.get(0);
                                            mView.changeData(transformFile(new File(filePath)), position);
                                            mView.clearSelectedState();
                                            mView.showMessage(mView.getResString(R.string.tips_remark_successfully));
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            mView.showError(e.getMessage());
                                            onCompleted();
                                        }

                                        @Override
                                        public void onCompleted() {
                                            mView.hideProgressDialog();
                                            mView.finishAction();
                                        }
                                    });
                            mRxManager.add(subscription);
                        }
                    });
                    Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                    negativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            });
        }
    }

    /**
     * 显示/隐藏文件
     *
     * @param fileList 文件列表
     */
    public void onShowHideFile(final ArrayList<FileItem> fileList) {
        mView.showNormalAlert(mView.getResString(R.string.tips_show_hide_files), mView.getResString(R.string.act_show_hide), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Subscription subscription = Observable.from(fileList).groupBy(new Func1<FileItem, Boolean>() {
                    @Override
                    public Boolean call(FileItem file) {
                        return file.isShow();
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<GroupedObservable<Boolean, FileItem>>() {
                            @Override
                            public void call(final GroupedObservable<Boolean, FileItem> o) {
                                Subscription subscription = o.all(new Func1<FileItem, Boolean>() {
                                    @Override
                                    public Boolean call(FileItem file) {
                                        boolean result;
                                        String newPath;
                                        if (mSelectType == Constants.SelectType.MENU_SDCARD) {
                                            if (o.getKey()) {
                                                DocumentFile documentFile = DocumentFileUtils.getDocumentFile(new File(file.getPath()), file.isDirectory());
                                                result = mDocumentFileModel.hideFile(documentFile, file.getName());
                                                newPath = new File(file.getPath()).getParent() + File.separator + "." + file.getName();
                                            } else {
                                                DocumentFile documentFile = DocumentFileUtils.getDocumentFile(new File(file.getPath()), file.isDirectory());
                                                result = mDocumentFileModel.showFile(documentFile, file.getName());
                                                newPath = new File(file.getPath()).getParent() + File.separator + file.getName().replaceFirst("\\.", "");
                                            }
                                        } else {
                                            if (o.getKey()) {
                                                result = mFileModel.hideFile(file.getPath(), file.getName());
                                                newPath = new File(file.getPath()).getParent() + File.separator + "." + file.getName();
                                            } else {
                                                result = mFileModel.showFile(file.getPath(), file.getName());
                                                newPath = new File(file.getPath()).getParent() + File.separator + file.getName().replaceFirst("\\.", "");
                                            }
                                        }
                                        if (result) {
                                            MediaScanUtils.renameMediaFile(mContext, file.getPath(), newPath);
                                        }
                                        return result;
                                    }
                                })
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Subscriber<Boolean>() {
                                            @Override
                                            public void onStart() {
                                                mView.showProgressDialog(mContext.getString(R.string.tips_handling));
                                            }

                                            @Override
                                            public void onNext(Boolean result) {
                                                if (result) {
                                                    mView.showMessage(mView.getResString(R.string.tips_show_hide_successfully));
                                                } else {
                                                    mView.showMessage(mView.getResString(R.string.tips_show_hide_in_error));
                                                }
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                mView.showError(e.getMessage());
                                                onCompleted();
                                            }

                                            @Override
                                            public void onCompleted() {
                                                mView.hideProgressDialog();
                                                mView.finishAction();
                                                refreshAfterFinishAction();
                                            }
                                        });
                                mRxManager.add(subscription);
                            }
                        });
                mRxManager.add(subscription);
            }
        });
    }

    /**
     * 显示文件详情（只支持单个文件）
     *
     * @param fileList 文件列表
     */
    public void onShowFileInfo(final ArrayList<FileItem> fileList, final ArrayList<Integer> selectedItemList) {
        if (fileList.size() != 1 || selectedItemList.size() != 1) {
            mView.showMessage(mView.getResString(R.string.tips_choose_one_file));
        } else {
            final FileItem fileItem = fileList.get(0);
            final int position = selectedItemList.get(0);
            mView.showInfoBottomSheet(fileItem, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mView.finishAction();
                    mView.changeData(fileItem, position);
                    mView.clearSelectedState();
                }
            });
        }
    }

    /**
     * 卸载文件（只支持单个文件）
     *
     * @param fileList 文件列表
     */
    public void onUninstall(final ArrayList<FileItem> fileList, final ArrayList<Integer> selectedItemList) {
        if (fileList.size() != 1 || selectedItemList.size() != 1) {
            mView.showMessage(mView.getResString(R.string.tips_choose_one_file));
        } else {
            try {
                FileItem fileItem = fileList.get(0);
                int position = selectedItemList.get(0);
                Intent intent = new Intent(Intent.ACTION_DELETE, Uri.fromParts("package", fileItem.getPackageName(), null));
                intent.putExtra("position", position);
                ((FileListActivity) mContext).startActivityForResult(intent, UNINSTALL_REQUEST_CODE);
            } catch (Exception e) {
                mView.showMessage(mView.getResString(R.string.tips_can_not_access_file));
            }
        }
    }

    /**
     * 删除文件
     *
     * @param fileList 文件列表
     */
    public void onDelete(final ArrayList<FileItem> fileList, final ArrayList<Integer> selectedItemList) {
        mView.showNormalAlert(mView.getResString(R.string.tips_delete_files), mView.getResString(R.string.act_delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                Subscription subscription = Observable.from(fileList).groupBy(new Func1<FileItem, Boolean>() {
                    @Override
                    public Boolean call(FileItem file) {
                        return file.isDirectory();
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<GroupedObservable<Boolean, FileItem>>() {
                            @Override
                            public void call(final GroupedObservable<Boolean, FileItem> o) {
                                Subscription subscription = o.all(new Func1<FileItem, Boolean>() {
                                    @Override
                                    public Boolean call(FileItem file) {
                                        boolean result;
                                        if (mSelectType == Constants.SelectType.MENU_SDCARD) {
                                            DocumentFile documentFile = DocumentFileUtils.getDocumentFile(new File(file.getPath()), o.getKey());
                                            result = mDocumentFileModel.deleteFile(documentFile);
                                        } else {
                                            if (o.getKey()) {
                                                result = mFileModel.deleteFolder(file.getPath());
                                            } else {
                                                result = mFileModel.deleteFile(file.getPath());
                                            }
                                        }
                                        if (result) {
                                            SPUtils.removeFileRemark(file.getPath());
                                            MediaScanUtils.removeMediaFromLib(mContext, file.getPath());
                                        }
                                        return result;
                                    }
                                })
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Subscriber<Boolean>() {
                                            @Override
                                            public void onStart() {
                                                mView.showProgressDialog(mContext.getString(R.string.tips_deleting));
                                            }

                                            @Override
                                            public void onNext(Boolean result) {
                                                if (result) {
                                                    Collections.sort(selectedItemList, new Comparator<Integer>() {
                                                        @Override
                                                        public int compare(Integer o1, Integer o2) {
                                                            return o2.compareTo(o1);
                                                        }
                                                    });
                                                    Iterator<Integer> it = selectedItemList.iterator();
                                                    while (it.hasNext()) {
                                                        mView.deleteData(it.next());
                                                    }
                                                    mView.clearSelectedState();
                                                    mView.showMessage(mView.getResString(R.string.tips_delete_successfully));
                                                } else {
                                                    mView.showMessage(mView.getResString(R.string.tips_delete_in_error));
                                                }
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                mView.showError(e.getMessage());
                                                onCompleted();
                                            }

                                            @Override
                                            public void onCompleted() {
                                                mView.hideProgressDialog();
                                                mView.finishAction();
                                            }
                                        });
                                mRxManager.add(subscription);
                            }
                        });
                mRxManager.add(subscription);
            }
        });
    }

    /**
     * 压缩文件
     *
     * @param fileList 文件列表
     */
    public void onCompress(final ArrayList<FileItem> fileList) {
        mView.showNormalAlert(mView.getResString(R.string.tips_compress_files), mView.getResString(R.string.act_compress), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final View view = mView.inflateFilenameInputDialogLayout();
                final TextInputLayout textInputLayout = mView.findTextInputLayout(view);
                final EditText editText = mView.findAlertDialogEditText(view);
                mView.showKeyboard(mView.findAlertDialogEditText(view));
                mView.showInputFileNameAlert(view, new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Button positionButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        positionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String filename = editText.getText().toString();
                                if (TextUtils.isEmpty(filename)) {
                                    textInputLayout.setEnabled(true);
                                    textInputLayout.setError(mView.getResString(R.string.tips_file_name_empty));
                                    textInputLayout.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            textInputLayout.setError(null);
                                            textInputLayout.setErrorEnabled(false);
                                        }
                                    }, 2000);
                                    return;
                                }
                                Subscription subscription = Observable.from(fileList).map(new Func1<FileItem, File>() {
                                    @Override
                                    public File call(FileItem fileItem) {
                                        return new File(fileItem.getPath());
                                    }
                                })
                                        .toList()
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(Schedulers.io())
                                        .map(new Func1<List<File>, Boolean>() {
                                            @Override
                                            public Boolean call(List<File> fileList) {
                                                ArrayList<File> fileArrayList = new ArrayList<>();
                                                fileArrayList.addAll(fileList);
                                                String newPath = mCurrentPath + File.separator + filename + ".zip";
                                                boolean result = mFileListModel.zipFileList(fileArrayList, newPath);
                                                if (result) {
                                                    for (File f : fileArrayList) {
                                                        SPUtils.removeFileRemark(f.getPath());
                                                    }
                                                    scanFile(newPath);
                                                }
                                                return result;
                                            }
                                        })
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Subscriber<Boolean>() {
                                            @Override
                                            public void onStart() {
                                                dialog.dismiss();
                                                mView.showProgressDialog(mContext.getString(R.string.tips_compressing));
                                            }

                                            @Override
                                            public void onNext(Boolean result) {
                                                if (result) {
                                                    mView.showMessage(mView.getResString(R.string.tips_compress_successfully));
                                                } else {
                                                    mView.showMessage(mView.getResString(R.string.tips_compress_in_error));
                                                }
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                mView.showError(e.getMessage());
                                                onCompleted();
                                            }

                                            @Override
                                            public void onCompleted() {
                                                mView.hideProgressDialog();
                                                mView.finishAction();
                                                refreshAfterFinishAction();
                                            }
                                        });
                                mRxManager.add(subscription);
                            }
                        });
                        Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                        negativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * 解压文件
     *
     * @param fileList 文件列表
     */
    public void onExtract(final ArrayList<FileItem> fileList) {
        mView.showNormalAlert(mView.getResString(R.string.tips_extract_file), mView.getResString(R.string.act_extract), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String filePath = fileList.get(0).getPath();
                String suffix = FileUtils.getSuffix(fileList.get(0).getName());
                switch (suffix) {
                    case ".7z":
                        Subscription subscription = Observable.create(new Observable.OnSubscribe<Boolean>() {
                            @Override
                            public void call(Subscriber<? super Boolean> subscriber) {
                                boolean result = mFileListModel.un7zipFileList(filePath, mCurrentPath);
                                if (result) {
                                    scanFile(mCurrentPath);
                                }
                                subscriber.onNext(result);
                                subscriber.onCompleted();
                            }
                        })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<Boolean>() {
                                    @Override
                                    public void onStart() {
                                        mView.showProgressDialog(mContext.getString(R.string.tips_extracting));
                                    }

                                    @Override
                                    public void onNext(Boolean result) {
                                        if (result) {
                                            mView.showMessage(mView.getResString(R.string.tips_extract_successfully));
                                        } else {
                                            mView.showMessage(mView.getResString(R.string.tips_extract_in_error));
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        mView.showError(e.getMessage());
                                        onCompleted();
                                    }

                                    @Override
                                    public void onCompleted() {
                                        mView.hideProgressDialog();
                                        mView.finishAction();
                                        refreshAfterFinishAction();
                                    }
                                });
                        mRxManager.add(subscription);
                        break;
                    case ".rar":
                        Subscription subscription2 = Observable.create(new Observable.OnSubscribe<Boolean>() {
                            @Override
                            public void call(Subscriber<? super Boolean> subscriber) {
                                boolean result = mFileListModel.unRarFileList(filePath, mCurrentPath);
                                if (result) {
                                    scanFile(mCurrentPath);
                                }
                                subscriber.onNext(result);
                                subscriber.onCompleted();
                            }
                        })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<Boolean>() {
                                    @Override
                                    public void onStart() {
                                        mView.showProgressDialog(mContext.getString(R.string.tips_extracting));
                                    }

                                    @Override
                                    public void onNext(Boolean result) {
                                        if (result) {
                                            mView.showMessage(mView.getResString(R.string.tips_extract_successfully));
                                        } else {
                                            mView.showMessage(mView.getResString(R.string.tips_extract_in_error));
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        mView.showError(e.getMessage());
                                        onCompleted();
                                    }

                                    @Override
                                    public void onCompleted() {
                                        mView.hideProgressDialog();
                                        mView.finishAction();
                                        refreshAfterFinishAction();
                                    }
                                });
                        mRxManager.add(subscription2);
                        break;
                    default:
                        if (CompressUtils.isEncrypted(filePath)) {
                            final View view = mView.inflatePasswordInputDialogLayout();
                            final EditText editText = mView.findAlertDialogEditText(view);
                            mView.showKeyboard(mView.findAlertDialogEditText(view));
                            mView.showInputFileNameAlert(view, new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(final DialogInterface dialog) {
                                    Button positionButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                                    positionButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            final String password = editText.getText().toString();
                                            Subscription subscription = Observable.create(new Observable.OnSubscribe<Boolean>() {
                                                @Override
                                                public void call(Subscriber<? super Boolean> subscriber) {
                                                    boolean result = mFileListModel.unZipFileList(filePath, mCurrentPath, password);
                                                    if (result) {
                                                        scanFile(mCurrentPath);
                                                    }
                                                    subscriber.onNext(result);
                                                    subscriber.onCompleted();
                                                }
                                            })
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(new Subscriber<Boolean>() {
                                                        @Override
                                                        public void onStart() {
                                                            dialog.dismiss();
                                                            mView.showProgressDialog(mContext.getString(R.string.tips_extracting));
                                                        }

                                                        @Override
                                                        public void onNext(Boolean result) {
                                                            if (result) {
                                                                mView.showMessage(mView.getResString(R.string.tips_extract_successfully));
                                                            } else {
                                                                mView.showMessage(mView.getResString(R.string.tips_extract_in_error));
                                                            }
                                                        }

                                                        @Override
                                                        public void onError(Throwable e) {
                                                            mView.showError(e.getMessage());
                                                            onCompleted();
                                                        }

                                                        @Override
                                                        public void onCompleted() {
                                                            mView.hideProgressDialog();
                                                            mView.finishAction();
                                                            refreshAfterFinishAction();
                                                        }
                                                    });
                                            mRxManager.add(subscription);
                                        }
                                    });
                                    Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                                    negativeButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            });

                        } else {
                            Subscription subscription3 = Observable.create(new Observable.OnSubscribe<Boolean>() {
                                @Override
                                public void call(Subscriber<? super Boolean> subscriber) {
                                    boolean result = mFileListModel.unZipFileList(filePath, mCurrentPath);
                                    if (result) {
                                        scanFile(mCurrentPath);
                                    }
                                    subscriber.onNext(result);
                                    subscriber.onCompleted();
                                }
                            })
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Subscriber<Boolean>() {
                                        @Override
                                        public void onStart() {
                                            mView.showProgressDialog(mContext.getString(R.string.tips_extracting));
                                        }

                                        @Override
                                        public void onNext(Boolean result) {
                                            if (result) {
                                                mView.showMessage(mView.getResString(R.string.tips_extract_successfully));
                                            } else {
                                                mView.showMessage(mView.getResString(R.string.tips_extract_in_error));
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            mView.showError(e.getMessage());
                                            onCompleted();
                                        }

                                        @Override
                                        public void onCompleted() {
                                            mView.hideProgressDialog();
                                            mView.finishAction();
                                            refreshAfterFinishAction();
                                        }
                                    });
                            mRxManager.add(subscription3);
                        }
                        break;
                }
            }
        });
    }

    /**
     * 复制文件
     */
    public void onCopy(final ArrayList<FileItem> fileList) {
        mView.showNormalAlert(mView.getResString(R.string.tips_copy_files), mView.getResString(R.string.act_copy), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Subscription subscription = Observable.from(fileList).groupBy(new Func1<FileItem, Boolean>() {
                    @Override
                    public Boolean call(FileItem file) {
                        return file.isDirectory();
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<GroupedObservable<Boolean, FileItem>>() {
                    @Override
                    public void call(final GroupedObservable<Boolean, FileItem> o) {
                        Subscription subscription = o.all(new Func1<FileItem, Boolean>() {
                            @Override
                            public Boolean call(FileItem file) {
                                boolean result;
                                String destPath = mCurrentPath + File.separator + file.getName();
                                File srcFile = new File(file.getPath());
                                File destFile = new File(destPath);
                                if (o.getKey()) {
                                    mDocumentFileModel.createOrExistsDir(srcFile);
                                    mDocumentFileModel.createOrExistsDir(destFile);
                                    result = mDocumentFileModel.copyFolder(srcFile, destFile);
                                } else {
                                    mDocumentFileModel.createOrExistsFile(srcFile);
                                    mDocumentFileModel.createOrExistsFile(destFile);
                                    result = mDocumentFileModel.copyFile(srcFile, destFile);
                                }
                                if (result) {
                                    scanFile(destPath);
                                }
                                return result;
                            }
                        })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<Boolean>() {
                                    @Override
                                    public void onStart() {
                                        mView.showProgressDialog(mContext.getString(R.string.tips_copying));
                                    }

                                    @Override
                                    public void onNext(Boolean result) {
                                        if (result) {
                                            mView.showMessage(mView.getResString(R.string.tips_copy_successfully));
                                        } else {
                                            mView.showMessage(mView.getResString(R.string.tips_copy_in_error));
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        mView.showError(e.getMessage());
                                        onCompleted();
                                    }

                                    @Override
                                    public void onCompleted() {
                                        mView.hideProgressDialog();
                                        mView.finishAction();
                                        refreshAfterFinishAction();
                                    }
                                });
                        mRxManager.add(subscription);
                    }
                });
                mRxManager.add(subscription);
            }
        });
    }

    /**
     * 移动文件
     */
    public void onMove(final ArrayList<FileItem> fileList) {
        mView.showNormalAlert(mView.getResString(R.string.tips_move_files), mView.getResString(R.string.act_move), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Subscription subscription = Observable.from(fileList).groupBy(new Func1<FileItem, Boolean>() {
                    @Override
                    public Boolean call(FileItem file) {
                        return file.isDirectory();
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<GroupedObservable<Boolean, FileItem>>() {
                    @Override
                    public void call(final GroupedObservable<Boolean, FileItem> o) {
                        Subscription subscription = o.all(new Func1<FileItem, Boolean>() {
                            @Override
                            public Boolean call(FileItem file) {
                                boolean result;
                                String destPath = mCurrentPath + File.separator + file.getName();
                                File srcFile = new File(file.getPath());
                                File destFile = new File(destPath);
                                if (o.getKey()) {
                                    mDocumentFileModel.createOrExistsDir(srcFile);
                                    mDocumentFileModel.createOrExistsDir(destFile);
                                    result = mDocumentFileModel.moveFolder(srcFile, destFile);
                                } else {
                                    mDocumentFileModel.createOrExistsFile(srcFile);
                                    mDocumentFileModel.createOrExistsFile(destFile);
                                    result = mDocumentFileModel.moveFile(srcFile, destFile);
                                }
                                if (result) {
                                    scanFile(destPath);
                                }
                                return result;
                            }
                        })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<Boolean>() {
                                    @Override
                                    public void onStart() {
                                        mView.showProgressDialog(mContext.getString(R.string.tips_moving));
                                    }

                                    @Override
                                    public void onNext(Boolean result) {
                                        if (result) {
                                            mView.showMessage(mView.getResString(R.string.tips_move_successfully));
                                        } else {
                                            mView.showMessage(mView.getResString(R.string.tips_move_in_error));
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        mView.showError(e.getMessage());
                                        onCompleted();
                                    }

                                    @Override
                                    public void onCompleted() {
                                        mView.hideProgressDialog();
                                        mView.finishAction();
                                        refreshAfterFinishAction();
                                    }
                                });
                        mRxManager.add(subscription);
                    }
                });
                mRxManager.add(subscription);
            }
        });
    }

    /**
     * 获取当前路径文件列表
     *
     * @param searchStr 查询文字
     * @return 当前路径文件列表
     */
    private ArrayList<FileItem> getCurrentFileList(String searchStr) {
        ArrayList<FileItem> fileItemList = null;

        switch (mSelectType) {
            case Constants.SelectType.MENU_FILE:
            case Constants.SelectType.MENU_DOWNLOAD:
            case Constants.SelectType.MENU_SDCARD:
                fileItemList = transformFileList(mFileListModel.getFileList(mCurrentPath, searchStr));
                break;
            case Constants.SelectType.MENU_RECENT:
                fileItemList = mFileListModel.getRecentFileList(mContext.getContentResolver());
                break;
            case Constants.SelectType.MENU_DOCUMENT:
                fileItemList = mFileListModel.getDocumentList(searchStr, mContext.getContentResolver());
                break;
            case Constants.SelectType.MENU_MUSIC:
                fileItemList = mFileListModel.getAudioList(searchStr, mContext.getContentResolver());
                break;
            case Constants.SelectType.MENU_PHOTO:
                fileItemList = mFileListModel.getImageList(searchStr, mContext.getContentResolver());
                break;
            case Constants.SelectType.MENU_VIDEO:
                fileItemList = mFileListModel.getVideoList(searchStr, mContext.getContentResolver());
                break;
            case Constants.SelectType.MENU_APK:
                fileItemList = mFileListModel.getApkList(searchStr, mContext.getContentResolver());
                break;
            case Constants.SelectType.MENU_ZIP:
                fileItemList = mFileListModel.getCompressList(searchStr, mContext.getContentResolver());
                break;
            case Constants.SelectType.MENU_APPS:
                fileItemList = mFileListModel.getInstalledList(searchStr);
                break;
        }

        if (fileItemList == null) {
            return null;
        }

        switch (mSortType) {
            case Constants.SortType.TYPE:
                fileItemList = mFileListModel.orderByType(fileItemList);
                break;
            case Constants.SortType.TIME:
                fileItemList = mFileListModel.orderByTime(fileItemList);
                break;
            case Constants.SortType.ALPHABET:
                fileItemList = mFileListModel.orderByAlphabet(fileItemList);
                break;
            case Constants.SortType.SIZE:
                fileItemList = mFileListModel.orderBySize(fileItemList);
                break;
            case Constants.SortType.REMARK:
                fileItemList = mFileListModel.orderByRemark(fileItemList);
                break;
        }

        switch (mOrderType) {
            case Constants.OrderType.DESC:
                break;
            case Constants.OrderType.ASC:
                mFileListModel.orderByOrder(fileItemList);
                break;
        }

        return fileItemList;
    }

    /**
     * 转换文件列表
     *
     * @param fileList 文件列表
     * @return ArrayList<FileItem>
     */
    private ArrayList<FileItem> transformFileList(ArrayList<File> fileList) {
        if (fileList != null && fileList.size() > 0) {
            ArrayList<FileItem> fileItemList = new ArrayList<>();
            for (File file : fileList) {
                FileItem fileItem = transformFile(file);
                if (fileItem != null) {
                    fileItemList.add(fileItem);
                }
            }
            return mFileListModel.orderByType(fileItemList);
        }
        return null;
    }

    /**
     * 转换文件
     *
     * @param file 文件列表
     * @return FileItem
     */
    private FileItem transformFile(File file) {
        PackageManager pm = BaseApplication.getInstance().getApplicationContext().getPackageManager();
        FileItem fileItem = new FileItem();
        fileItem.setName(file.getName());
        fileItem.setPath(file.getPath());
        if (file.isDirectory()) {
//          ileItem.setSize(FileUtils.getDirLength(file));
            fileItem.setSize("0");
            File[] listFiles = file.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                int hiddenCount = 0;
                for (File f : listFiles) {
                    if (f.isHidden() && !SPUtils.isShowHiddenFiles()) {
                        hiddenCount++;
                    }
                }
                fileItem.setCount(file.list().length - hiddenCount);
            } else {
                fileItem.setCount(0);
            }
        } else {
            fileItem.setSize(String.valueOf(FileUtils.getFileLength(file)));
            fileItem.setCount(0);
        }
        fileItem.setDate(String.valueOf(file.lastModified() / 1000));
        fileItem.setIsDirectory(file.isDirectory());
        fileItem.setParent(file.getParent());
        fileItem.setType(MimeTypeUtils.getTypeBySuffix(FileUtils.getSuffix(file.getName())));
        fileItem.setIsShow(!file.isHidden());
        fileItem.setRemark(SPUtils.getFileRemark(file.getPath()));

        if (file.getName().endsWith(".apk")) {
            PackageInfo packageInfo = pm.getPackageArchiveInfo(file.getPath(), PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                ApplicationInfo appInfo = packageInfo.applicationInfo;
                appInfo.sourceDir = file.getPath();
                appInfo.publicSourceDir = file.getPath();
                Drawable icon = appInfo.loadIcon(pm);
                fileItem.setIcon(icon);
            }
        }
        return fileItem;
    }

    /**
     * 进入某个文件夹
     *
     * @param file 文件夹
     */
    public void enterFolder(FileItem file, int scrollY) {
        mFileStack.push(file.getPath());
        mScrollYStack.push(scrollY);
        mView.addTab(file.getName());
        mCurrentPath = file.getPath();
        mBeforeScrollY = scrollY;
        mView.refreshData(false, 0);
    }

    /**
     * 点击路径进入某个文件夹
     *
     * @param index 文件层级
     * @return 是否tab被移除
     */
    public boolean enterCertainFolder(int index) {
        boolean isRemoved = false;
        while (mFileStack.size() > index + 1) {
            mFileStack.pop();
            if (mScrollYStack.size() > index + 2) {
                mScrollYStack.pop();
            }
            mView.removeTab();
            isRemoved = true;
        }
        if (isRemoved) {
            mCurrentPath = mFileStack.peek();
            mBeforeScrollY = mScrollYStack.peek();
            mScrollYStack.pop();
            mView.refreshData(false, mBeforeScrollY);
        }
        return isRemoved;
    }

    /**
     * 点击返回显示的文件夹
     *
     * @return 是否返回
     */
    public boolean backFolder() {
        if (mFileStack.size() > 1) {
            mFileStack.pop();
            mView.removeTab();
            mCurrentPath = mFileStack.peek();
            mBeforeScrollY = mScrollYStack.peek();
            mScrollYStack.pop();
            mView.refreshData(false, mBeforeScrollY);
            return true;
        }
        return false;
    }

    /**
     * 打开文件
     *
     * @param context 上下文
     * @param file    文件
     * @return 是否打开成功
     */
    public boolean openFile(Context context, File file) {
        return mFileModel.openFile(context, file);
    }

    /**
     * 分享文件
     *
     * @param context  上下文
     * @param fileList 文件列表
     * @return 是否分享成功
     */
    public boolean shareFile(Context context, List<File> fileList) {
        return mFileModel.shareFile(context, fileList);
    }

    private void scanFile(String filePath) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            ArrayList<File> list = FileUtils.listFilesInDir(filePath);
            int size = list.size();
            String[] pathArray = new String[size];
            String[] mimeTypeArray = new String[size];
            for (int i = 0; i < size; i++) {
                String path = list.get(i).getPath();
                pathArray[i] = path;
                mimeTypeArray[i] = MimeTypeUtils.getMIMEType(path);
            }
            MediaScanUtils.scanFiles(mContext, pathArray, mimeTypeArray);
        } else {
            String mimeType = MimeTypeUtils.getMIMEType(filePath);
            MediaScanUtils.scanFiles(mContext, new String[]{filePath}, new String[]{mimeType});
        }
    }

    public void refreshAfterFinishAction() {
        if (isPasteActonMode) {
            mView.refreshData(true);
        } else if (mEditType != Constants.EditType.COPY && mEditType != Constants.EditType.MOVE
                && mEditType != Constants.EditType.ZIP && mEditType != Constants.EditType.UNZIP) {
            mView.refreshData(true);
        } else {
            mView.refreshData(false);
        }
    }

}
