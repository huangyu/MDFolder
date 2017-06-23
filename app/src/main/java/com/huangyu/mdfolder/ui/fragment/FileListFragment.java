package com.huangyu.mdfolder.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.huangyu.library.ui.BaseFragment;
import com.huangyu.library.ui.CommonRecyclerViewAdapter;
import com.huangyu.library.util.LogUtils;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.app.Constants;
import com.huangyu.mdfolder.bean.FileItem;
import com.huangyu.mdfolder.mvp.presenter.FileListPresenter;
import com.huangyu.mdfolder.mvp.view.IFileListView;
import com.huangyu.mdfolder.ui.activity.AudioBrowserActivity;
import com.huangyu.mdfolder.ui.activity.FileListActivity;
import com.huangyu.mdfolder.ui.activity.ImageBrowserActivity;
import com.huangyu.mdfolder.ui.activity.VideoBrowserActivity;
import com.huangyu.mdfolder.ui.adapter.FileListAdapter;
import com.huangyu.mdfolder.ui.widget.TabView;
import com.huangyu.mdfolder.utils.AlertUtils;
import com.huangyu.mdfolder.utils.KeyboardUtils;
import com.jakewharton.rxbinding.view.RxView;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * Created by huangyu on 2017-5-23.
 */
public class FileListFragment extends BaseFragment<IFileListView, FileListPresenter> implements IFileListView {

    @Bind(R.id.cl_main)
    CoordinatorLayout mCoordinatorLayout;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.tab_view)
    TabView mTabView;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.ll_empty)
    LinearLayout mLlEmpty;

    @Bind(R.id.iv_center)
    ImageView mIvCenter;

    @Bind(R.id.fam_add)
    FloatingActionMenu mFamAdd;

    @Bind(R.id.fab_add_file)
    FloatingActionButton mFabAddFile;

    @Bind(R.id.fab_add_folder)
    FloatingActionButton mFabAddFolder;

    private ProgressDialog progressDialog;
    private FileListAdapter mAdapter;
    private ActionMode mActionMode;
    private String mSearchStr;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected IFileListView initAttachView() {
        return this;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mIvCenter.setColorFilter(getResources().getColor(R.color.colorDarkGray));

        mAdapter = new FileListAdapter(getActivity());
        mAdapter.setOnItemClick(new CommonRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mPresenter.mEditType == Constants.EditType.NONE) {
                    FileItem file = mAdapter.getItem(position);
                    if (file == null) {
                        finishAction();
                        return;
                    }

                    if (file.isDirectory()) {
                        mPresenter.enterFolder(file);
                    } else {
                        // 进入图片浏览
                        if (file.getType() == Constants.FileType.IMAGE) {
                            Intent intent = new Intent(getActivity(), ImageBrowserActivity.class);
                            intent.putExtra(getString(R.string.intent_image_list), mAdapter.getDataList());
                            intent.putExtra(getString(R.string.intent_image_position), position);
                            getActivity().startActivity(intent);
                        }
                        // 进入视频浏览
                        else if (file.getType() == Constants.FileType.VIDEO) {
                            Intent intent = new Intent(getActivity(), VideoBrowserActivity.class);
                            intent.putExtra(getString(R.string.intent_video_list), mAdapter.getDataList());
                            intent.putExtra(getString(R.string.intent_video_position), position);
                            getActivity().startActivity(intent);
                        }
                        // 进入音频浏览
                        else if (file.getType() == Constants.FileType.AUDIO) {
                            Intent intent = new Intent(getActivity(), AudioBrowserActivity.class);
                            intent.putExtra(getString(R.string.intent_audio_list), mAdapter.getDataList());
                            intent.putExtra(getString(R.string.intent_audio_position), position);
                            getActivity().startActivity(intent);
                        }
                        // 打开文件
                        else if (!mPresenter.openFile(getContext(), new File(file.getPath()))) {
                            AlertUtils.showSnack(mCoordinatorLayout, getString(R.string.tips_no_permission_to_access_file));
                        }
                    }

                    finishAction();
                } else if (mPresenter.mEditType == Constants.EditType.COPY || mPresenter.mEditType == Constants.EditType.CUT) {
                    FileItem file = mAdapter.getItem(position);
                    if (file == null) {
                        return;
                    }

                    if (file.isDirectory()) {
                        mPresenter.enterFolder(file);
                    }
                } else {
                    mPresenter.mEditType = Constants.EditType.SELECT;
                    mAdapter.switchSelectedState(position);
                    mActionMode.setTitle(mAdapter.getSelectedItemCount() + getString(R.string.tips_selected));
                    if (mAdapter.getSelectedItemCount() == 0) {
                        finishAction();
                    }
                }
            }
        });

        mAdapter.setOnItemLongClick(new CommonRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final int position) {
                if (mPresenter.mEditType == Constants.EditType.COPY || mPresenter.mEditType == Constants.EditType.CUT) {
                    return;
                }
                mPresenter.mEditType = Constants.EditType.SELECT;
                mAdapter.switchSelectedState(position);
                if (mActionMode == null) {
                    mActionMode = getControlActionMode();
                }
                mActionMode.setTitle(mAdapter.getSelectedItemCount() + getString(R.string.tips_selected));
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        if (((FileListActivity) getActivity()).isLightMode()) {
            mTabView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        } else {
            mTabView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.onRefreshInSwipe(mSearchStr, false);
            }
        });

        RxView.clicks(mFabAddFile).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                mPresenter.onAddFile();
            }
        });

        RxView.clicks(mFabAddFolder).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                mPresenter.onAddFolder();
            }
        });

        initRxManagerActions();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onLoadStorageFileList(false, mSearchStr);
    }

    private void initRxManagerActions() {
        mRxManager.on("onSearch", new Action1<String>() {
            @Override
            public void call(String text) {
                mSearchStr = text;
                mPresenter.onRefresh(text, true);
            }
        });

        mRxManager.on("toStorage", new Action1<Boolean>() {
            @Override
            public void call(Boolean isInner) {
                mPresenter.mFileType = Constants.SelectType.MENU_FILE;
                mPresenter.onLoadStorageFileList(isInner, mSearchStr);
            }
        });

        mRxManager.on("toRoot", new Action1<String>() {
            @Override
            public void call(String text) {
                mPresenter.mFileType = Constants.SelectType.MENU_FILE;
                mPresenter.onLoadRootFileList(mSearchStr);
            }
        });

        mRxManager.on("toPhoto", new Action1<String>() {
            @Override
            public void call(String s) {
                mPresenter.mFileType = Constants.SelectType.MENU_PHOTO;
                mPresenter.onLoadMultiTypeFileList(mSearchStr, mPresenter.mFileType);
            }
        });

        mRxManager.on("toMusic", new Action1<String>() {
            @Override
            public void call(String s) {
                mPresenter.mFileType = Constants.SelectType.MENU_MUSIC;
                mPresenter.onLoadMultiTypeFileList(mSearchStr, mPresenter.mFileType);
            }
        });

        mRxManager.on("toVideo", new Action1<String>() {
            @Override
            public void call(String s) {
                mPresenter.mFileType = Constants.SelectType.MENU_VIDEO;
                mPresenter.onLoadMultiTypeFileList(mSearchStr, mPresenter.mFileType);
            }
        });

        mRxManager.on("toDocument", new Action1<String>() {
            @Override
            public void call(String s) {
                mPresenter.mFileType = Constants.SelectType.MENU_DOCUMENT;
                mPresenter.onLoadMultiTypeFileList(mSearchStr, mPresenter.mFileType);
            }
        });

        mRxManager.on("toDownload", new Action1<String>() {
            @Override
            public void call(String s) {
                mPresenter.mFileType = Constants.SelectType.MENU_DOWNLOAD;
                mPresenter.onLoadDownloadFileList(mSearchStr);
            }
        });

        mRxManager.on("onSortType", new Action1<Integer>() {
            @Override
            public void call(Integer sortType) {
                mPresenter.mSortType = sortType;
                mPresenter.onRefresh(mSearchStr, true);
            }
        });

        mRxManager.on("onOrderType", new Action1<Integer>() {
            @Override
            public void call(Integer orderType) {
                mPresenter.mOrderType = orderType;
                mPresenter.onRefresh(mSearchStr, true);
            }
        });
    }

    @Override
    public void startRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void stopRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void addTab(String path) {
        mTabView.addTab(path, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag(R.id.tab_tag);
                if (tag != null && tag instanceof Integer) {
                    int index = (Integer) tag;
                    mPresenter.enterCertainFolder(index);
                }
                if (mPresenter.mEditType != Constants.EditType.COPY && mPresenter.mEditType != Constants.EditType.CUT) {
                    finishAction();
                }
            }
        });
    }

    @Override
    public boolean removeTab() {
        return mTabView.removeTab();
    }

    @Override
    public void removeAllTabs() {
        mTabView.removeAllTabs();
    }

    @Override
    public void refreshData(boolean ifClearSelected) {
        mPresenter.onRefresh(mSearchStr, ifClearSelected);
    }

    @Override
    public void refreshData(ArrayList<FileItem> filesList, boolean ifClearSelected) {
        mAdapter.clearData(ifClearSelected);

        if (filesList == null || filesList.isEmpty()) {
            mLlEmpty.setVisibility(View.VISIBLE);
        } else {
            mLlEmpty.setVisibility(View.GONE);
            mAdapter.setData(filesList);
        }
    }

    @Override
    public void showMessage(String message) {
        AlertUtils.showSnack(mCoordinatorLayout, message);
    }

    @Override
    public void showError(String error) {
        LogUtils.logd(error);
        AlertUtils.showSnack(mCoordinatorLayout, getString(R.string.tips_error));
    }

    @Override
    public void showKeyboard(final EditText editText) {
        getActivity().getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyboardUtils.showSoftInput(editText);
            }
        }, 200);
    }

    @Override
    public void hideKeyboard(final EditText editText) {
        getActivity().getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyboardUtils.hideSoftInput(getContext(), editText);
            }
        }, 200);
    }

    @Override
    public void showInfoBottomSheet(FileItem fileItem, DialogInterface.OnCancelListener onCancelListener) {
        AlertUtils.showInfoBottomSheet(getContext(), fileItem, onCancelListener);
    }

    @Override
    public View inflateAlertDialogLayout() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.dialog_input, new LinearLayout(getContext()), false);
    }

    @Override
    public TextInputLayout findTextInputLayout(View view) {
        return (TextInputLayout) ButterKnife.findById(view, R.id.til_tips);
    }

    @Override
    public EditText findAlertDialogEditText(View view) {
        return (AppCompatEditText) ButterKnife.findById(view, R.id.et_name);
    }

    @Override
    public AlertDialog showInputFileNameAlert(View view, DialogInterface.OnShowListener onShowListener) {
        return AlertUtils.showCustomAlert(getContext(), "", view, onShowListener);
    }

    @Override
    public AlertDialog showNormalAlert(String message, String positiveString, DialogInterface.OnClickListener positiveClick) {
        return AlertUtils.showNormalAlert(getContext(), message, positiveString, positiveClick);
    }

    @Override
    public void closeFloatingActionMenu() {
        mFamAdd.close(true);
    }

    @Override
    public String getResString(@StringRes int resId) {
        return getContext().getString(resId);
    }

    public void finishAction() {
        if (mActionMode != null) {
            mActionMode.finish();
        }
        mPresenter.mEditType = Constants.EditType.NONE;
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(getString(R.string.tips_alert));
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private ActionMode getControlActionMode() {
        return getActivity().startActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                if (mPresenter.mFileType == Constants.SelectType.MENU_FILE || mPresenter.mFileType == Constants.SelectType.MENU_DOWNLOAD) {
                    mode.getMenuInflater().inflate(R.menu.menu_control, menu);
                } else {
                    mode.getMenuInflater().inflate(R.menu.menu_control_type, menu);
                }
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                menu.clear();
                if (mPresenter.mFileType == Constants.SelectType.MENU_FILE || mPresenter.mFileType == Constants.SelectType.MENU_DOWNLOAD) {
                    mode.getMenuInflater().inflate(R.menu.menu_control, menu);
                } else {
                    mode.getMenuInflater().inflate(R.menu.menu_control_type, menu);
                }
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                final ArrayList<FileItem> fileList = mAdapter.getSelectedDataList();
                switch (item.getItemId()) {
                    case R.id.action_rename:
                        mPresenter.onRenameFile(fileList);
                        break;
                    case R.id.action_info:
                        mPresenter.onShowFileInfo(fileList);
                        break;
                    case R.id.action_delete:
                        mPresenter.onDelete(fileList);
                        break;
                    case R.id.action_copy:
                        mPresenter.mEditType = Constants.EditType.COPY;
                        mActionMode = getPasteActonMode();
                        mAdapter.mSelectedFileList = fileList;
                        mActionMode.setTitle(mAdapter.getSelectedItemCount() + getString(R.string.tips_selected));
                        break;
                    case R.id.action_cut:
                        mPresenter.mEditType = Constants.EditType.CUT;
                        mActionMode = getPasteActonMode();
                        mAdapter.mSelectedFileList = fileList;
                        mActionMode.setTitle(mAdapter.getSelectedItemCount() + getString(R.string.tips_selected));
                        break;
                    case R.id.action_show_hide:
                        mPresenter.onShowHideFile(fileList);
                        break;
                    case R.id.action_zip:
                        mPresenter.onZip(fileList);
                        break;
                    case R.id.action_unzip:
                        mPresenter.onUnzip(fileList);
                        break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                if (mPresenter.mEditType != Constants.EditType.COPY && mPresenter.mEditType != Constants.EditType.CUT) {
                    refreshData(true);
                    getActivity().supportInvalidateOptionsMenu();
                    mActionMode = null;
                    mPresenter.mEditType = Constants.EditType.NONE;
                } else {
                    refreshData(false);
                }
            }
        });
    }

    private ActionMode getPasteActonMode() {
        return getActivity().startActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_paste, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                menu.clear();
                mode.getMenuInflater().inflate(R.menu.menu_paste, menu);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                final ArrayList<FileItem> fileList = mAdapter.mSelectedFileList;
                switch (item.getItemId()) {
                    case R.id.action_paste:
                        if (mPresenter.mEditType == Constants.EditType.COPY) {
                            mPresenter.onCopy(fileList);
                        } else if (mPresenter.mEditType == Constants.EditType.CUT) {
                            mPresenter.onCut(fileList);
                        }
                        break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                refreshData(true);
                getActivity().supportInvalidateOptionsMenu();
                mActionMode = null;
                mPresenter.mEditType = Constants.EditType.NONE;
                mAdapter.mSelectedFileList = null;
            }
        });
    }

    public boolean onBackPressed() {
        if (mFamAdd.isOpened()) {
            mFamAdd.close(true);
            return true;
        }
        return mPresenter.backFolder();
    }

    @Override
    public void onDestroyView() {
        hideProgressDialog();
        super.onDestroyView();
    }

}
