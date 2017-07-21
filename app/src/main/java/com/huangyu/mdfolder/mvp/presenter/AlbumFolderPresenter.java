package com.huangyu.mdfolder.mvp.presenter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.huangyu.library.mvp.BasePresenter;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.app.Constants;
import com.huangyu.mdfolder.bean.FileItem;
import com.huangyu.mdfolder.mvp.model.FileListModel;
import com.huangyu.mdfolder.mvp.model.FileModel;
import com.huangyu.mdfolder.mvp.view.IAlbumFolderView;
import com.huangyu.mdfolder.utils.MediaScanUtils;
import com.huangyu.mdfolder.utils.SPUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;

import static com.huangyu.mdfolder.app.Constants.OrderType.DESC;

/**
 * Created by huangyu on 2017/5/22.
 */

public class AlbumFolderPresenter extends BasePresenter<IAlbumFolderView> {

    private FileListModel mFileListModel;
    private FileModel mFileModel;

    public int mEditType;   // 当前编辑状态
    public int mSortType;   // 当前排序类型
    public int mOrderType;  // 当前升降序类型
    public FileItem mCurrentAlbum;
    public boolean isInAlbum = true;
    private int mScrollY;

    @Override
    public void create() {
        mFileListModel = new FileListModel();
        mFileModel = new FileModel();
        mEditType = Constants.EditType.NONE;
        mSortType = Constants.SortType.TYPE;
        mOrderType = DESC;
        mScrollY = 0;
    }

    public void loadAlbum(final String searchStr, final int scrollY) {
        Subscription subscription = Observable.defer(new Func0<Observable<ArrayList<FileItem>>>() {
            @Override
            public Observable<ArrayList<FileItem>> call() {
                return Observable.just(mFileListModel.getPhotoAlbumList(searchStr, mContext.getContentResolver()));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mScrollY = scrollY;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<FileItem>>() {
                    @Override
                    public void onStart() {
                        mView.startRefresh();
                    }

                    @Override
                    public void onNext(ArrayList<FileItem> albumFolderList) {
                        mView.addTab(mView.getResString(R.string.str_album) + "  " + albumFolderList.size());
                        mView.refreshAlbum(albumFolderList, mScrollY);
                        isInAlbum = true;
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

    public void loadAlbum(final String searchStr) {
        Subscription subscription = Observable.defer(new Func0<Observable<ArrayList<FileItem>>>() {
            @Override
            public Observable<ArrayList<FileItem>> call() {
                return Observable.just(mFileListModel.getPhotoAlbumList(searchStr, mContext.getContentResolver()));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<FileItem>>() {
                    @Override
                    public void onStart() {
                        mView.startRefresh();
                    }

                    @Override
                    public void onNext(ArrayList<FileItem> albumFolderList) {
                        mView.addTab(mView.getResString(R.string.str_album) + "  " + albumFolderList.size());
                        mView.refreshAlbum(albumFolderList, mScrollY);
                        isInAlbum = true;
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

    public void loadImage(final String searchStr, final boolean ifClearSelected, final int scrollY) {
        Subscription subscription = Observable.defer(new Func0<Observable<ArrayList<FileItem>>>() {
            @Override
            public Observable<ArrayList<FileItem>> call() {
                return Observable.just(getCurrentImageList(searchStr, mCurrentAlbum));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mScrollY = scrollY;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<FileItem>>() {
                    @Override
                    public void onStart() {
                        mView.startRefresh();
                    }

                    @Override
                    public void onNext(ArrayList<FileItem> imageList) {
                        mView.addTab(mView.getResString(R.string.str_image) + "  " + imageList.size());
                        mView.refreshData(imageList, ifClearSelected);
                        isInAlbum = false;
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

    public void loadImage(final String searchStr, final boolean ifClearSelected) {
        Subscription subscription = Observable.defer(new Func0<Observable<ArrayList<FileItem>>>() {
            @Override
            public Observable<ArrayList<FileItem>> call() {
                return Observable.just(getCurrentImageList(searchStr, mCurrentAlbum));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<FileItem>>() {
                    @Override
                    public void onStart() {
                        mView.startRefresh();
                    }

                    @Override
                    public void onNext(ArrayList<FileItem> imageList) {
                        mView.addTab(mView.getResString(R.string.str_image) + "  " + imageList.size());
                        mView.refreshData(imageList, ifClearSelected);
                        isInAlbum = false;
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

    private ArrayList<FileItem> getCurrentImageList(String searchStr, FileItem albumFolder) {
        ArrayList<FileItem> fileItemList;

        fileItemList = mFileListModel.getPhotoList(searchStr, albumFolder, mContext.getContentResolver());

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
                            Subscription subscription = Observable.create(new Observable.OnSubscribe<Boolean>() {
                                @Override
                                public void call(Subscriber<? super Boolean> subscriber) {
                                    boolean result = mFileModel.renameFile(filePath, editText.getText().toString());
                                    String newPath = new File(filePath).getParent() + File.separator + editText.getText().toString();
                                    if (result) {
                                        MediaScanUtils.renameMediaFile(mContext, filePath, newPath);
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
                                        }

                                        @Override
                                        public void onNext(Boolean result) {
                                            if (result) {
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
                                        String newPath;
                                        if (o.getKey()) {
                                            result = mFileModel.hideFile(file.getPath(), file.getName());
                                            newPath = new File(file.getPath()).getParent() + File.separator + "." + file.getName();
                                        } else {
                                            result = mFileModel.showFile(file.getPath(), file.getName());
                                            newPath = new File(file.getPath()).getParent() + File.separator + file.getName().replaceFirst("\\.", "");
                                        }
                                        if (result) {
                                            MediaScanUtils.renameMediaFile(mContext, file.getPath(), newPath);
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
                                        if (result) {
                                            MediaScanUtils.removeMediaFromLib(mContext, file.getPath());
                                        }
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
     * 删除文件
     *
     * @param fileList 文件列表
     */
    public void onDeleteAlbum(final ArrayList<FileItem> fileList) {
        mView.showNormalAlert(mView.getResString(R.string.tips_delete_files), mView.getResString(R.string.act_delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Subscription subscription = Observable.from(fileList)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<FileItem>() {
                            @Override
                            public void onNext(FileItem fileItem) {
                                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                                ContentResolver resolver = mContext.getContentResolver();
                                resolver.delete(uri, MediaStore.Images.Media.BUCKET_ID + " = " + fileItem.getId(), null);
                                mView.showMessage(mView.getResString(R.string.tips_delete_successfully));
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
    }

    /**
     * 备注文件（暂时只支持单个文件）
     *
     * @param fileList 文件列表
     */
    public void onRemark(final ArrayList<FileItem> fileList) {
        if (fileList.size() != 1) {
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
                                    .observeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Subscriber<Object>() {
                                        @Override
                                        public void onStart() {
                                            dialog.dismiss();
                                            mView.startRefresh();
                                        }

                                        @Override
                                        public void onNext(Object result) {
                                            SPUtils.setFileRemark(filePath, remark);
                                            mView.showMessage(mView.getResString(R.string.tips_remark_successfully));
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
