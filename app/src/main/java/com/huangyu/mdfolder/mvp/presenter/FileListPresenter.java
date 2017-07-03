package com.huangyu.mdfolder.mvp.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputLayout;
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
import com.huangyu.mdfolder.mvp.model.FileListModel;
import com.huangyu.mdfolder.mvp.model.FileModel;
import com.huangyu.mdfolder.mvp.view.IFileListView;
import com.huangyu.mdfolder.utils.CompressUtils;
import com.huangyu.mdfolder.utils.MediaScanner;
import com.huangyu.mdfolder.utils.MimeTypeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

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

import static com.huangyu.mdfolder.app.Constants.OrderType.DESC;

/**
 * Created by huangyu on 2017/5/22.
 */

public class FileListPresenter extends BasePresenter<IFileListView> {

    private FileListModel mFileListModel;
    private FileModel mFileModel;
    private Stack<String> mFileStack;   // 文件路径栈
    private Stack<Integer> mScrollYStack;   // 列表position栈
    private MediaScanner mMediaScanner;

    private String mCurrentPath; // 当前路径
    private int mBeforeScrollY; // 当前position
    public int mEditType;   // 当前编辑状态
    public int mSelectType;   // 当前选择类型
    public int mSortType;   // 当前排序类型
    public int mOrderType;  // 当前升降序类型

    @Override
    public void create() {
        mMediaScanner = new MediaScanner(mContext);
        mFileListModel = new FileListModel();
        mFileModel = new FileModel();
        mFileStack = new Stack<>();
        mScrollYStack = new Stack<>();
        mEditType = Constants.EditType.NONE;
        mSelectType = Constants.SelectType.MENU_FILE;
        mSortType = Constants.SortType.TYPE;
        mOrderType = DESC;
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
                        mView.refreshData(fileList, false, 0);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
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
                        mView.refreshData(fileList, false, 0);
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
                        mView.refreshData(fileList, false, 0);
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
                        mView.showTabs();
                        mView.startRefresh();
                        mView.hideTabs();
                    }

                    @Override
                    public void onNext(ArrayList<FileItem> fileList) {
                        mView.refreshData(fileList, false, 0);
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
    public void onLoadMultiTypeFileList(final String searchStr, final int fileType) {
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
                        switch (fileType) {
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
                        }
                        mView.refreshData(fileList, false, 0);
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
     * 刷新界面
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
                        mView.refreshData(fileList, ifClearSelected, 0);
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
                    }

                    @Override
                    public void onNext(ArrayList<FileItem> fileList) {
                        mView.refreshData(fileList, ifClearSelected, scrollY);
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
                    }
                });
        mRxManager.add(subscription);
    }

    /**
     * 刷新界面
     */
    public void onRefresh(final String searchStr, final boolean ifClearSelected) {
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
                        mView.refreshData(fileList, ifClearSelected, 0);
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
     * 新增文件
     */
    public void onAddFile() {
        if (mSelectType != Constants.SelectType.MENU_FILE) {
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
                                    }

                                    @Override
                                    public void onNext(String filePath) {
                                        if (filePath == null) {
                                            return;
                                        }
                                        if (mFileModel.addFile(filePath)) {
                                            mMediaScanner.scanFile(new File(filePath), MimeTypeUtils.getMIMEType(filePath));
                                            mView.showMessage(mView.getResString(R.string.tips_add_file_successfully));
                                            mView.refreshData(false, 0);
                                        } else {
                                            mView.showMessage(mView.getResString(R.string.tips_add_file_error));
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        mView.showError(e.getMessage());
                                        onCompleted();
                                    }

                                    @Override
                                    public void onCompleted() {
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
     * 新增文件夹
     */
    public void onAddFolder() {
        if (mSelectType != Constants.SelectType.MENU_FILE) {
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
                                    }

                                    @Override
                                    public void onNext(String filePath) {
                                        if (filePath == null) {
                                            return;
                                        }
                                        if (mFileModel.addFolder(filePath)) {
                                            mMediaScanner.scanFile(new File(filePath), MimeTypeUtils.getMIMEType(filePath));
                                            mView.showMessage(mView.getResString(R.string.tips_add_folder_successfully));
                                            mView.refreshData(false, 0);
                                        } else {
                                            mView.showMessage(mView.getResString(R.string.tips_add_folder_error));
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        mView.showError(e.getMessage());
                                        onCompleted();
                                    }

                                    @Override
                                    public void onCompleted() {
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
     * 重命名文件（暂时只支持单个文件）
     *
     * @param fileList 文件列表
     */
    public void onRenameFile(final ArrayList<FileItem> fileList) {
        if (fileList.size() != 1) {
            mView.showMessage(mView.getResString(R.string.tips_choose_one_file));
        } else {
            final View view = mView.inflateFilenameInputDialogLayout();
            final TextInputLayout textInputLayout = mView.findTextInputLayout(view);
            final EditText editText = mView.findAlertDialogEditText(view);
            final String filename = fileList.get(0).getName();
            final String filePath = fileList.get(0).getPath();
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
                                            boolean result = mFileModel.renameFile(filePath, editText.getText().toString());
                                            String newPath = new File(filePath).getParent() + File.separator + editText.getText().toString();
                                            mMediaScanner.scanFile(new File(newPath), null);
                                            return result;
                                        }
                                    })
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Subscriber<Boolean>() {
                                        @Override
                                        public void onStart() {
                                            dialog.dismiss();
                                            mView.startRefresh();
                                        }

                                        @Override
                                        public void onNext(Boolean result) {
                                            if (result) {
                                                mMediaScanner.delete(mContext, filePath);
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
                                            mView.stopRefresh();
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
                        .subscribe(new Action1<GroupedObservable<Boolean, FileItem>>() {
                            @Override
                            public void call(final GroupedObservable<Boolean, FileItem> o) {
                                Subscription subscription = o.all(new Func1<FileItem, Boolean>() {
                                    @Override
                                    public Boolean call(FileItem file) {
                                        boolean result;
                                        if (o.getKey()) {
                                            result = mFileModel.hideFile(file.getPath(), file.getName());
                                            String newPath = new File(file.getPath()).getParent() + File.separator + "." + file.getName();
                                            mMediaScanner.scanFile(new File(newPath), null);
                                        } else {
                                            result = mFileModel.showFile(file.getPath(), file.getName());
                                            String newPath = new File(file.getPath()).getParent() + File.separator + file.getName().replaceFirst("\\.", "");
                                            mMediaScanner.scanFile(new File(newPath), null);
                                        }
                                        return result;
                                    }
                                })
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Subscriber<Boolean>() {
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
     * 显示文件详情
     *
     * @param fileList 文件列表
     */
    public void onShowFileInfo(final ArrayList<FileItem> fileList) {
        if (fileList.size() != 1) {
            mView.showMessage(mView.getResString(R.string.tips_choose_one_file));
        } else {
            mView.showInfoBottomSheet(fileList.get(0), new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mView.finishAction();
                }
            });
        }
    }

    /**
     * 删除文件
     *
     * @param fileList 文件列表
     */
    public void onDelete(final ArrayList<FileItem> fileList) {
        mView.showNormalAlert(mView.getResString(R.string.tips_delete_files), mView.getResString(R.string.act_delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Subscription subscription = Observable.from(fileList).groupBy(new Func1<FileItem, Boolean>() {
                    @Override
                    public Boolean call(FileItem file) {
                        return file.isDirectory();
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Action1<GroupedObservable<Boolean, FileItem>>() {
                            @Override
                            public void call(final GroupedObservable<Boolean, FileItem> o) {
                                Subscription subscription = o.all(new Func1<FileItem, Boolean>() {
                                    @Override
                                    public Boolean call(FileItem file) {
                                        boolean result;
                                        if (o.getKey()) {
                                            result = mFileModel.deleteFolder(file.getPath());
                                        } else {
                                            result = mFileModel.deleteFile(file.getPath());
                                        }
                                        mMediaScanner.delete(mContext, file.getPath());
                                        return result;
                                    }
                                })
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Subscriber<Boolean>() {
                                            @Override
                                            public void onNext(Boolean result) {
                                                if (result) {
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
    public void onZip(final ArrayList<FileItem> fileList) {
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
                                        .delay(300, TimeUnit.MILLISECONDS)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Subscriber<List<File>>() {
                                            @Override
                                            public void onStart() {
                                                dialog.dismiss();
                                                mView.showProgressDialog(mContext.getString(R.string.tips_compressing));
                                            }

                                            @Override
                                            public void onNext(List<File> fileList) {
                                                ArrayList<File> fileArrayList = new ArrayList<>();
                                                fileArrayList.addAll(fileList);
                                                String newPath = mCurrentPath + File.separator + filename + ".zip";
                                                boolean result = mFileListModel.zipFileList(fileArrayList, newPath);
                                                mMediaScanner.scanFile(new File(newPath), MimeTypeUtils.getMIMEType(newPath));
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
    public void onUnzip(final ArrayList<FileItem> fileList) {
        mView.showNormalAlert(mView.getResString(R.string.tips_extract_file), mView.getResString(R.string.act_extract), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String filePath = fileList.get(0).getPath();
                String suffix = FileUtils.getSuffix(fileList.get(0).getName());
                switch (suffix) {
                    case ".7z":
                        Subscription subscription = Observable.from(fileList).map(new Func1<FileItem, File>() {
                            @Override
                            public File call(FileItem fileItem) {
                                return new File(fileItem.getPath());
                            }
                        })
                                .toList()
                                .delay(300, TimeUnit.MILLISECONDS)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<List<File>>() {
                                    @Override
                                    public void onStart() {
                                        mView.showProgressDialog(mContext.getString(R.string.tips_extracting));
                                    }

                                    @Override
                                    public void onNext(List<File> fileList) {
                                        boolean result = mFileListModel.un7zipFileList(filePath, mCurrentPath);
                                        mMediaScanner.scanFile(new File(mCurrentPath), MimeTypeUtils.getMIMEType(mCurrentPath));
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
                                    }
                                });
                        mRxManager.add(subscription);
                        break;
                    case ".rar":
                        Subscription subscription2 = Observable.from(fileList).map(new Func1<FileItem, File>() {
                            @Override
                            public File call(FileItem fileItem) {
                                return new File(fileItem.getPath());
                            }
                        })
                                .toList()
                                .delay(300, TimeUnit.MILLISECONDS)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<List<File>>() {
                                    @Override
                                    public void onStart() {
                                        mView.showProgressDialog(mContext.getString(R.string.tips_extracting));
                                    }

                                    @Override
                                    public void onNext(List<File> fileList) {
                                        boolean result = mFileListModel.unRarFileList(filePath, mCurrentPath);
                                        mMediaScanner.scanFile(new File(mCurrentPath), MimeTypeUtils.getMIMEType(mCurrentPath));
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
                                            Subscription subscription = Observable.from(fileList).map(new Func1<FileItem, File>() {
                                                @Override
                                                public File call(FileItem fileItem) {
                                                    return new File(fileItem.getPath());
                                                }
                                            })
                                                    .toList()
                                                    .delay(300, TimeUnit.MILLISECONDS)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(new Subscriber<List<File>>() {
                                                        @Override
                                                        public void onStart() {
                                                            dialog.dismiss();
                                                            mView.showProgressDialog(mContext.getString(R.string.tips_extracting));
                                                        }

                                                        @Override
                                                        public void onNext(List<File> fileList) {
                                                            boolean result = mFileListModel.unZipFileList(filePath, mCurrentPath, password);
                                                            mMediaScanner.scanFile(new File(mCurrentPath), MimeTypeUtils.getMIMEType(mCurrentPath));
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
                            Subscription subscription3 = Observable.from(fileList).map(new Func1<FileItem, File>() {
                                @Override
                                public File call(FileItem fileItem) {
                                    return new File(fileItem.getPath());
                                }
                            })
                                    .toList()
                                    .delay(500, TimeUnit.MILLISECONDS)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Subscriber<List<File>>() {
                                        @Override
                                        public void onStart() {
                                            mView.showProgressDialog(mContext.getString(R.string.tips_extracting));
                                        }

                                        @Override
                                        public void onNext(List<File> fileList) {
                                            boolean result = mFileListModel.unZipFileList(filePath, mCurrentPath);
                                            mMediaScanner.scanFile(new File(mCurrentPath), MimeTypeUtils.getMIMEType(mCurrentPath));
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
                }).subscribe(new Action1<GroupedObservable<Boolean, FileItem>>() {
                    @Override
                    public void call(final GroupedObservable<Boolean, FileItem> o) {
                        Subscription subscription = o.all(new Func1<FileItem, Boolean>() {
                            @Override
                            public Boolean call(FileItem file) {
                                boolean result;
                                String newPath = mCurrentPath + File.separator + file.getName();
                                if (o.getKey()) {
                                    result = mFileModel.copyFolder(file.getPath(), newPath);
                                } else {
                                    result = mFileModel.copyFile(file.getPath(), newPath);
                                }
                                mMediaScanner.scanFile(new File(newPath), MimeTypeUtils.getMIMEType(newPath));
                                return result;
                            }
                        }).subscribe(new Subscriber<Boolean>() {
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
                }).subscribe(new Action1<GroupedObservable<Boolean, FileItem>>() {
                    @Override
                    public void call(final GroupedObservable<Boolean, FileItem> o) {
                        Subscription subscription = o.all(new Func1<FileItem, Boolean>() {
                            @Override
                            public Boolean call(FileItem file) {
                                boolean result;
                                String newPath = mCurrentPath + File.separator + file.getName();
                                if (o.getKey()) {
                                    result = mFileModel.moveFolder(file.getPath(), newPath);
                                } else {
                                    result = mFileModel.moveFile(file.getPath(), newPath);
                                }
                                mMediaScanner.scanFile(new File(newPath), MimeTypeUtils.getMIMEType(newPath));
                                return result;
                            }
                        }).subscribe(new Subscriber<Boolean>() {
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
     * 获取当前路径文件列表
     *
     * @param searchStr 查询文字
     * @return 当前路径文件列表
     */
    private ArrayList<FileItem> getCurrentFileList(String searchStr) {
        ArrayList<FileItem> fileItemList = null;

        switch (mSelectType) {
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
            case Constants.SelectType.MENU_FILE:
            case Constants.SelectType.MENU_DOWNLOAD:
                fileItemList = transformFileList(mFileListModel.getFileList(mCurrentPath, searchStr));
                break;
            case Constants.SelectType.MENU_APK:
                fileItemList = mFileListModel.getApkList(searchStr, mContext.getContentResolver());
                break;
            case Constants.SelectType.MENU_ZIP:
                fileItemList = mFileListModel.getCompressList(searchStr, mContext.getContentResolver());
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
     * 转换文件列表，ArrayList<File>ArrayList<FileItem>
     *
     * @param fileList 文件列表
     * @return ArrayList<FileItem>
     */
    private ArrayList<FileItem> transformFileList(ArrayList<File> fileList) {
        if (fileList != null && fileList.size() > 0) {
            PackageManager pm = BaseApplication.getInstance().getApplicationContext().getPackageManager();
            ArrayList<FileItem> fileItemList = new ArrayList<>();
            FileItem fileItem;
            for (File file : fileList) {
                fileItem = new FileItem();
                fileItem.setName(file.getName());
                fileItem.setPath(file.getPath());
                if (file.isDirectory()) {
//                    fileItem.setSize(FileUtils.getDirLength(file));
                    fileItem.setSize("0");
                    if (file.list() != null && file.list().length > 0) {
                        fileItem.setCount(file.list().length);
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

                if (file.getName().endsWith(".apk")) {
                    PackageInfo packageInfo = pm.getPackageArchiveInfo(file.getPath(), PackageManager.GET_ACTIVITIES);
                    ApplicationInfo appInfo = packageInfo.applicationInfo;
                    appInfo.sourceDir = file.getPath();
                    appInfo.publicSourceDir = file.getPath();
                    Drawable icon = appInfo.loadIcon(pm);
                    fileItem.setApkIcon(icon);
                }
                fileItemList.add(fileItem);
            }
            return mFileListModel.orderByType(fileItemList);
        }
        return null;
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

}
