package com.huangyu.mdfolder.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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

import com.huangyu.library.BuildConfig;
import com.huangyu.library.ui.BaseFragment;
import com.huangyu.library.ui.CommonRecyclerViewAdapter;
import com.huangyu.library.util.LogToFileUtils;
import com.huangyu.library.util.LogUtils;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.app.Constants;
import com.huangyu.mdfolder.bean.FileItem;
import com.huangyu.mdfolder.mvp.presenter.AlbumFolderPresenter;
import com.huangyu.mdfolder.mvp.view.IAlbumFolderView;
import com.huangyu.mdfolder.ui.activity.FileListActivity;
import com.huangyu.mdfolder.ui.activity.ImageBrowserActivity;
import com.huangyu.mdfolder.ui.adapter.AlbumFolderAdapter;
import com.huangyu.mdfolder.ui.adapter.AlbumImageAdapter;
import com.huangyu.mdfolder.ui.widget.AlbumVerticalGirdDecoration;
import com.huangyu.mdfolder.ui.widget.TabView;
import com.huangyu.mdfolder.utils.AlertUtils;
import com.huangyu.mdfolder.utils.KeyboardUtils;
import com.huangyu.mdfolder.utils.SPUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * Created by huangyu on 2017-5-23.
 */
public class AlbumFolderFragment extends BaseFragment<IAlbumFolderView, AlbumFolderPresenter> implements IAlbumFolderView {

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

    private AlbumFolderAdapter mAdapter;
    private AlbumImageAdapter mImageAdapter;
    private String mSearchStr;
    private ActionMode mActionMode;

    private ProgressDialog progressDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_album;
    }

    @Override
    protected IAlbumFolderView initAttachView() {
        return this;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mIvCenter.setColorFilter(getResources().getColor(R.color.colorDarkGray));

        mImageAdapter = new AlbumImageAdapter(getContext());
        mImageAdapter.setOnItemClick(new CommonRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mPresenter.mEditType == Constants.EditType.NONE) {
                    FileItem file = mImageAdapter.getItem(position);
                    if (file == null) {
                        finishAction();
                        return;
                    }
                    if (SPUtils.isBuildInMode()) {
                        Intent intent = new Intent(getActivity(), ImageBrowserActivity.class);
                        intent.putExtra(getString(R.string.intent_image_list), mImageAdapter.getDataList());
                        intent.putExtra(getString(R.string.intent_image_position), position);
                        getActivity().startActivity(intent);
                    } else {
                        if (!mPresenter.openFile(getContext(), new File(file.getPath()))) {
                            AlertUtils.showSnack(mCoordinatorLayout, getString(R.string.tips_can_not_access_file));
                        }
                    }
                } else if (mPresenter.mEditType == Constants.EditType.COPY || mPresenter.mEditType == Constants.EditType.CUT
                        || mPresenter.mEditType == Constants.EditType.ZIP || mPresenter.mEditType == Constants.EditType.UNZIP) {
                } else {
                    mPresenter.mEditType = Constants.EditType.SELECT;
                    mImageAdapter.switchSelectedState(position);
                    mActionMode.setTitle(mImageAdapter.getSelectedItemCount() + getString(R.string.tips_selected));
                    if (mImageAdapter.getSelectedItemCount() == 0) {
                        finishAction();
                    }
                }
            }
        });
        mImageAdapter.setOnItemLongClick(new CommonRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                if (mPresenter.mEditType == Constants.EditType.COPY || mPresenter.mEditType == Constants.EditType.CUT
                        || mPresenter.mEditType == Constants.EditType.ZIP || mPresenter.mEditType == Constants.EditType.UNZIP) {
                    return;
                }
                mPresenter.mEditType = Constants.EditType.SELECT;
                mImageAdapter.switchSelectedState(position);
                if (mActionMode == null) {
                    mActionMode = getControlActionMode();
                }
                mActionMode.setTitle(mImageAdapter.getSelectedItemCount() + getString(R.string.tips_selected));
            }
        });

        mAdapter = new AlbumFolderAdapter(getContext());
        mAdapter.setOnItemClick(new CommonRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mPresenter.mCurrentAlbum = mAdapter.getItem(position);
                mPresenter.loadImage(mSearchStr, true);
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.grid_decoration, null);
        mRecyclerView.addItemDecoration(new AlbumVerticalGirdDecoration(drawable));
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
                if (mPresenter.isInAlbum) {
                    mPresenter.loadAlbum(mSearchStr);
                } else {
                    mPresenter.loadImage(mSearchStr, true);
                }
            }
        });

        mRxManager.on("onSortType", new Action1<Integer>() {
            @Override
            public void call(Integer sortType) {
                mPresenter.mSortType = sortType;
                if (mPresenter.isInAlbum) {
                    mPresenter.loadAlbum(mSearchStr);
                } else {
                    mPresenter.loadImage(mSearchStr, true);
                }
            }
        });

        mRxManager.on("onOrderType", new Action1<Integer>() {
            @Override
            public void call(Integer orderType) {
                mPresenter.mOrderType = orderType;
                if (mPresenter.isInAlbum) {
                    mPresenter.loadAlbum(mSearchStr);
                } else {
                    mPresenter.loadImage(mSearchStr, true);
                }
            }
        });

        mRxManager.on("onSearch", new Action1<String>() {
            @Override
            public void call(String text) {
                mSearchStr = text;
                if (mPresenter.isInAlbum) {
                    mPresenter.loadAlbum(mSearchStr);
                } else {
                    mPresenter.loadImage(mSearchStr, true);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter.isInAlbum) {
            mPresenter.loadAlbum(mSearchStr);
        } else {
            mPresenter.loadImage(mSearchStr, true);
        }
    }

    public void startRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    public void stopRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void addTab(String s) {
        mTabView.removeAllTabs();
        mTabView.addTab(s, null);
    }

    public void showTabs() {
        if (mTabView.isHide()) {
            mTabView.showTabs();
        }
    }

    public void showError(String error) {
        if (BuildConfig.DEBUG) {
            LogUtils.logd(error);
            LogToFileUtils.saveCrashInfoFile(error);
        }
        AlertUtils.showSnack(mCoordinatorLayout, getString(R.string.tips_error));
    }

    public void showMessage(String message) {
        AlertUtils.showSnack(mCoordinatorLayout, message);
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
        AlertUtils.showFileInfoBottomSheet(getContext(), fileItem, onCancelListener);
    }

    @Override
    public View inflateFilenameInputDialogLayout() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.dialog_input, new LinearLayout(getContext()), false);
    }

    @Override
    public View inflatePasswordInputDialogLayout() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.dialog_password, new LinearLayout(getContext()), false);
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

    public String getResString(@StringRes int resId) {
        return getContext().getString(resId);
    }

    private void refreshData(boolean ifClearSelected) {
        mPresenter.loadImage(mSearchStr, ifClearSelected);
    }

    public void refreshData(ArrayList<FileItem> imageList, boolean ifClearSelected) {
        mImageAdapter.clearData(ifClearSelected);

        if (imageList == null || imageList.isEmpty()) {
            mLlEmpty.setVisibility(View.VISIBLE);
        } else {
            mLlEmpty.setVisibility(View.GONE);
            mImageAdapter.setData(imageList);
        }
        mRecyclerView.setAdapter(mImageAdapter);
    }

    public void refreshAlbum(ArrayList<FileItem> albumFolderList) {
        mAdapter.clearData(true);

        if (albumFolderList == null || albumFolderList.isEmpty()) {
            mLlEmpty.setVisibility(View.VISIBLE);
        } else {
            mLlEmpty.setVisibility(View.GONE);
            mAdapter.setData(albumFolderList);
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    public void finishAction() {
        mSearchStr = "";
        if (mActionMode != null) {
            mActionMode.finish();
        }
        mPresenter.mEditType = Constants.EditType.NONE;
    }

    public boolean onBackPressed() {
        if (mPresenter.isInAlbum) {
            return false;
        } else {
            mPresenter.loadAlbum(mSearchStr);
            return true;
        }
    }

    private ActionMode getControlActionMode() {
        return getActivity().startActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_control_image, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                menu.clear();
                mode.getMenuInflater().inflate(R.menu.menu_control_image, menu);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                final ArrayList<FileItem> fileList = mImageAdapter.getSelectedDataList();
                switch (item.getItemId()) {
                    case R.id.action_rename:
                        mPresenter.onRenameFile(fileList);
                        break;
                    case R.id.action_info:
                        mPresenter.onShowFileInfo(fileList);
                        break;
                    case R.id.action_share:
                        File file;
                        List<File> files = new ArrayList<>();
                        for (FileItem fileItem : fileList) {
                            file = new File(fileItem.getPath());
                            files.add(file);
                        }
                        mPresenter.shareFile(getContext(), files);
                        break;
                    case R.id.action_delete:
                        mPresenter.onDelete(fileList);
                        break;
//                    case R.id.action_copy:
//                        mPresenter.mEditType = Constants.EditType.COPY;
//                        mActionMode = getPasteActonMode();
//                        mImageAdapter.mSelectedFileList = fileList;
//                        mActionMode.setTitle(mAdapter.getSelectedItemCount() + getString(R.string.tips_selected));
//                        break;
//                    case R.id.action_move:
//                        mPresenter.mEditType = Constants.EditType.CUT;
//                        mActionMode = getPasteActonMode();
//                        mImageAdapter.mSelectedFileList = fileList;
//                        mActionMode.setTitle(mAdapter.getSelectedItemCount() + getString(R.string.tips_selected));
//                        break;
                    case R.id.action_show_hide:
                        mPresenter.onShowHideFile(fileList);
                        break;
//                    case R.id.action_compress:
//                        mPresenter.mEditType = Constants.EditType.ZIP;
//                        mActionMode = getPasteActonMode();
//                        mImageAdapter.mSelectedFileList = fileList;
//                        mActionMode.setTitle(mAdapter.getSelectedItemCount() + getString(R.string.tips_selected));
//                        break;
//                    case R.id.action_extract:
//                        if (fileList.size() != 1) {
//                            showMessage(getResString(R.string.tips_choose_one_file));
//                        } else {
//                            mPresenter.mEditType = Constants.EditType.UNZIP;
//                            mActionMode = getPasteActonMode();
//                            mImageAdapter.mSelectedFileList = fileList;
//                            mActionMode.setTitle(mAdapter.getSelectedItemCount() + getString(R.string.tips_selected));
//                        }
//                        break;
                    case R.id.action_select_all:
                        mPresenter.mEditType = Constants.EditType.SELECT;
                        mImageAdapter.selectAll();
                        mActionMode.setTitle(mImageAdapter.getSelectedItemCount() + getString(R.string.tips_selected));
                        if (mImageAdapter.getSelectedItemCount() == 0) {
                            finishAction();
                        }
                        break;
                    case R.id.action_inverse_all:
                        mPresenter.mEditType = Constants.EditType.SELECT;
                        mImageAdapter.inverseAll();
                        mActionMode.setTitle(mImageAdapter.getSelectedItemCount() + getString(R.string.tips_selected));
                        if (mImageAdapter.getSelectedItemCount() == 0) {
                            finishAction();
                        }
                        break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                if (mPresenter.mEditType != Constants.EditType.COPY && mPresenter.mEditType != Constants.EditType.CUT
                        && mPresenter.mEditType != Constants.EditType.ZIP && mPresenter.mEditType != Constants.EditType.UNZIP) {
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

//    private ActionMode getPasteActonMode() {
//        return getActivity().startActionMode(new ActionMode.Callback() {
//            @Override
//            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//                mode.getMenuInflater().inflate(R.menu.menu_paste, menu);
//                return true;
//            }
//
//            @Override
//            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//                menu.clear();
//                mode.getMenuInflater().inflate(R.menu.menu_paste, menu);
//                return true;
//            }
//
//            @Override
//            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//                final ArrayList<FileItem> fileList = mImageAdapter.mSelectedFileList;
//                switch (item.getItemId()) {
//                    case R.id.action_paste:
//                        if (mPresenter.mEditType == Constants.EditType.COPY) {
//                            mPresenter.onCopy(fileList);
//                        } else if (mPresenter.mEditType == Constants.EditType.CUT) {
//                            mPresenter.onMove(fileList);
//                        } else if (mPresenter.mEditType == Constants.EditType.ZIP) {
//                            mPresenter.onZip(fileList);
//                        } else if (mPresenter.mEditType == Constants.EditType.UNZIP) {
//                            mPresenter.onUnzip(fileList);
//                        }
//                        break;
//                }
//                return false;
//            }
//
//            @Override
//            public void onDestroyActionMode(ActionMode mode) {
//                refreshData(true);
//                getActivity().supportInvalidateOptionsMenu();
//                mActionMode = null;
//                mPresenter.mEditType = Constants.EditType.NONE;
//                mImageAdapter.mSelectedFileList = null;
//            }
//        });
//    }

}
