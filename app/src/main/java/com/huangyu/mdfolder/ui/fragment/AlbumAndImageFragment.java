package com.huangyu.mdfolder.ui.fragment;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.SharedElementCallback;
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

import com.huangyu.library.app.BaseApplication;
import com.huangyu.library.ui.BaseFragment;
import com.huangyu.library.ui.CommonRecyclerViewAdapter;
import com.huangyu.library.ui.CommonRecyclerViewHolder;
import com.huangyu.library.util.LogUtils;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.app.Constants;
import com.huangyu.mdfolder.bean.FileItem;
import com.huangyu.mdfolder.mvp.presenter.AlbumAndImagePresenter;
import com.huangyu.mdfolder.mvp.view.IAlbumAndImageView;
import com.huangyu.mdfolder.ui.activity.FileListActivity;
import com.huangyu.mdfolder.ui.activity.ImageBrowserActivity;
import com.huangyu.mdfolder.ui.adapter.ImageAdapter;
import com.huangyu.mdfolder.ui.adapter.AlbumAdapter;
import com.huangyu.mdfolder.ui.widget.decoration.VerticalGirdDecoration;
import com.huangyu.mdfolder.ui.widget.TabView;
import com.huangyu.mdfolder.utils.AlertUtils;
import com.huangyu.mdfolder.utils.KeyboardUtils;
import com.huangyu.mdfolder.utils.OnSharedViewListener;
import com.huangyu.mdfolder.utils.SPUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * Created by huangyu on 2017-5-23.
 */
public class AlbumAndImageFragment extends BaseFragment<IAlbumAndImageView, AlbumAndImagePresenter> implements IAlbumAndImageView {

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

    private AlbumAdapter mAlbumAdapter;
    private ImageAdapter mImageAdapter;
    private VerticalGirdDecoration mAlbumVerticalGirdDecoration;
    private VerticalGirdDecoration mImageVerticalGirdDecoration;
    private String mSearchStr;
    private ActionMode mActionMode;

    private ProgressDialog mProgressDialog;

    private int mExitPosition;
    private int mEnterPosition;

    private final int mDefaultGridCount = 2;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_album;
    }

    @Override
    protected IAlbumAndImageView initAttachView() {
        return this;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mIvCenter.setColorFilter(getResources().getColor(R.color.colorDarkGrey));

        mImageAdapter = new ImageAdapter(getContext());
        mImageAdapter.setOnItemClick(new CommonRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mPresenter.mEditType == Constants.EditType.NONE) {
                    FileItem file = mImageAdapter.getItem(position);
                    if (file == null) {
                        finishAction();
                        mPresenter.refreshAfterFinishAction();
                        return;
                    }
                    if (SPUtils.isBuildInMode()) {
                        Intent intent = new Intent(getActivity(), ImageBrowserActivity.class);
                        intent.putExtra(getString(R.string.intent_image_list), mImageAdapter.getDataList());
                        intent.putExtra(getString(R.string.intent_image_position), position);
                        intent.putExtra(getString(R.string.intent_image_sort_type), mPresenter.mSortType);
                        intent.putExtra(getString(R.string.intent_image_order_type), mPresenter.mOrderType);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            CommonRecyclerViewHolder holder = (CommonRecyclerViewHolder) view.getTag();
                            ImageView ivImage = holder.getView(R.id.iv_image);
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                                    getActivity(), ivImage, ivImage.getTransitionName());
                            getActivity().startActivityForResult(intent, 0, options.toBundle());
                            mSharedViewListener.onSharedViewListener(position);
                        } else {
                            getActivity().startActivity(intent);
                        }
                    } else {
                        if (!mPresenter.openFile(getContext(), new File(file.getPath()))) {
                            AlertUtils.showToast(getContext(), getString(R.string.tips_can_not_access_file));
                        }
                    }
                } else if (mPresenter.mEditType != Constants.EditType.COPY && mPresenter.mEditType != Constants.EditType.MOVE
                        && mPresenter.mEditType != Constants.EditType.ZIP && mPresenter.mEditType != Constants.EditType.UNZIP) {
                    mPresenter.mEditType = Constants.EditType.SELECT;
                    mImageAdapter.switchSelectedState(position);
                    mActionMode.setTitle(String.format(getString(R.string.tips_selected), mImageAdapter.getSelectedItemCount()));
                    if (mImageAdapter.getSelectedItemCount() == 0) {
                        finishAction();
                        mPresenter.refreshAfterFinishAction();
                    }
                }
            }
        });
        mImageAdapter.setOnItemLongClick(new CommonRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                if (mPresenter.mEditType == Constants.EditType.COPY || mPresenter.mEditType == Constants.EditType.MOVE
                        || mPresenter.mEditType == Constants.EditType.ZIP || mPresenter.mEditType == Constants.EditType.UNZIP) {
                    return;
                }
                mPresenter.mEditType = Constants.EditType.SELECT;
                mImageAdapter.switchSelectedState(position);
                if (mActionMode == null) {
                    mActionMode = getPhotoControlActionMode();
                }
                mActionMode.setTitle(String.format(getString(R.string.tips_selected), mImageAdapter.getSelectedItemCount()));
            }
        });

        mAlbumAdapter = new AlbumAdapter(getContext());
        mAlbumAdapter.setOnItemClick(new CommonRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mPresenter.mEditType == Constants.EditType.NONE) {
                    mPresenter.mCurrentAlbum = mAlbumAdapter.getItem(position);
                    mPresenter.loadImage(mSearchStr, true, getScrollYDistance());
                } else if (mPresenter.mEditType != Constants.EditType.COPY && mPresenter.mEditType != Constants.EditType.MOVE
                        && mPresenter.mEditType != Constants.EditType.ZIP && mPresenter.mEditType != Constants.EditType.UNZIP) {
                    mPresenter.mEditType = Constants.EditType.SELECT;
                    mAlbumAdapter.switchSelectedState(position);
                    mActionMode.setTitle(String.format(getString(R.string.tips_selected), mAlbumAdapter.getSelectedItemCount()));
                    if (mAlbumAdapter.getSelectedItemCount() == 0) {
                        finishAction();
                        mPresenter.refreshAfterFinishAction();
                    }
                }
            }
        });
        mAlbumAdapter.setOnItemLongClick(new CommonRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                if (mPresenter.mEditType == Constants.EditType.COPY || mPresenter.mEditType == Constants.EditType.MOVE
                        || mPresenter.mEditType == Constants.EditType.ZIP || mPresenter.mEditType == Constants.EditType.UNZIP) {
                    return;
                }
                mPresenter.mEditType = Constants.EditType.SELECT;
                mAlbumAdapter.switchSelectedState(position);
                if (mActionMode == null) {
                    mActionMode = getAlbumControlActionMode();
                }
                mActionMode.setTitle(String.format(getString(R.string.tips_selected), mAlbumAdapter.getSelectedItemCount()));
            }
        });

        mRecyclerView.setAdapter(mAlbumAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), mDefaultGridCount));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        Drawable albumDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.album_grid_decoration, null);
        mAlbumVerticalGirdDecoration = new VerticalGirdDecoration(albumDrawable);
        Drawable imageDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.image_grid_decoration, null);
        mImageVerticalGirdDecoration = new VerticalGirdDecoration(imageDrawable);
        mRecyclerView.removeItemDecoration(mAlbumVerticalGirdDecoration);
        mRecyclerView.removeItemDecoration(mImageVerticalGirdDecoration);
        mRecyclerView.addItemDecoration(mAlbumVerticalGirdDecoration);

        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        String themeMode = ((FileListActivity) getActivity()).getThemeMode();
        switch (themeMode) {
            case "1":
                mTabView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
                break;
            case "2":
                mTabView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryIndigo));
                mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryIndigo));
                break;
            case "3":
                mTabView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryCyan));
                mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryCyan));
                break;
            case "4":
                mTabView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryTeal));
                mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryTeal));
                break;
            case "5":
                mTabView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryGreen));
                mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryGreen));
                break;
            case "6":
                mTabView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryRed));
                mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryRed));
                break;
            case "7":
                mTabView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryPurple));
                mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryPurple));
                break;
            case "8":
                mTabView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryOrange));
                mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryOrange));
                break;
            case "9":
                mTabView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryYellow));
                mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryYellow));
                break;
            case "10":
                mTabView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryPink));
                mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryPink));
                break;
            case "11":
                mTabView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryBrown));
                mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryBrown));
                break;
            case "12":
                mTabView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryGrey));
                mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryGrey));
                break;
            case "13":
                mTabView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryBlack));
                mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryBlack));
                break;
            default:
                mTabView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryBlue));
                mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryBlue));
                break;
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mPresenter.isInAlbum) {
                    mPresenter.loadAlbum(mSearchStr, 0);
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
                    mPresenter.loadAlbum(mSearchStr, 0);
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
                    mPresenter.loadAlbum(mSearchStr, 0);
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
                    mPresenter.loadAlbum(mSearchStr, 0);
                } else {
                    mPresenter.loadImage(mSearchStr, true);
                }
            }
        });

//        if (mPresenter.isInAlbum) {
//            mPresenter.loadAlbum(mSearchStr);
//        } else {
//            mPresenter.loadImage(mSearchStr, true);
//        }
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
        if (BaseApplication.isDebug()) {
            LogUtils.logd(error);
//            LogToFileUtils.saveCrashInfoFile(error);
        }
        AlertUtils.showToast(getContext(), getString(R.string.tips_error));
    }

    @Override
    public void addData(FileItem fileItem) {
        if (mPresenter.isInAlbum) {
            mAlbumAdapter.addItem(fileItem);
            mAlbumAdapter.clearSelectedState();
        } else {
            mImageAdapter.addItem(fileItem);
            mImageAdapter.clearSelectedState();
        }
    }

    @Override
    public void changeData(FileItem fileItem, int position) {
        if (mPresenter.isInAlbum) {
            mAlbumAdapter.changeItem(fileItem, position);
            mAlbumAdapter.clearSelectedState();
        } else {
            mImageAdapter.changeItem(fileItem, position);
            mImageAdapter.clearSelectedState();
        }
    }

    @Override
    public void deleteData(int position) {
        if (mPresenter.isInAlbum) {
            mAlbumAdapter.removeItem(position);
            mAlbumAdapter.clearSelectedState();
        } else {
            mImageAdapter.removeItem(position);
            mImageAdapter.clearSelectedState();
        }
    }

    @Override
    public void refreshData(boolean ifClearSelected) {
        if (mPresenter.isInAlbum) {
            mPresenter.loadAlbum(mSearchStr);
        } else {
            mPresenter.loadImage(mSearchStr, ifClearSelected);
        }
    }

    public void showMessage(String message) {
        AlertUtils.showToast(getContext(), message);
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
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle(getString(R.string.tips_alert));
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public String getResString(@StringRes int resId) {
        return getContext().getString(resId);
    }

    public void refreshImageView(ArrayList<FileItem> imageList, boolean ifClearSelected) {
        mImageAdapter.clearData(ifClearSelected);

        if (imageList == null || imageList.isEmpty()) {
            mLlEmpty.setVisibility(View.VISIBLE);
        } else {
            mLlEmpty.setVisibility(View.GONE);
            mImageAdapter.setData(imageList);
        }
        if (!(mRecyclerView.getAdapter() instanceof ImageAdapter)) {
            mRecyclerView.removeItemDecoration(mAlbumVerticalGirdDecoration);
            mRecyclerView.removeItemDecoration(mImageVerticalGirdDecoration);
            mRecyclerView.addItemDecoration(mImageVerticalGirdDecoration);
            mRecyclerView.setAdapter(mImageAdapter);
        }
    }

    public void refreshAlbumView(ArrayList<FileItem> albumFolderList, final int scrollY) {
        mAlbumAdapter.clearData(true);

        if (albumFolderList == null || albumFolderList.isEmpty()) {
            mLlEmpty.setVisibility(View.VISIBLE);
        } else {
            mLlEmpty.setVisibility(View.GONE);
            mAlbumAdapter.setData(albumFolderList);
        }

        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (scrollY != 0) {
                    mRecyclerView.scrollBy(0, scrollY);
                } else {
                    mRecyclerView.scrollTo(0, scrollY);
                }
                mRecyclerView.removeOnLayoutChangeListener(this);
            }
        });
        mRecyclerView.removeItemDecoration(mAlbumVerticalGirdDecoration);
        mRecyclerView.removeItemDecoration(mImageVerticalGirdDecoration);
        mRecyclerView.addItemDecoration(mAlbumVerticalGirdDecoration);
        mRecyclerView.setAdapter(mAlbumAdapter);
    }

    @Override
    public void onDestroy() {
        finishAction();
        super.onDestroy();
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

    private ActionMode getPhotoControlActionMode() {
        return getActivity().startActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mPresenter.isActionModeActive = true;
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
                final ArrayList<Integer> selectedItemList = mImageAdapter.getSelectedItemList();
                switch (item.getItemId()) {
                    case R.id.action_rename:
                        mPresenter.onRenameFile(fileList, selectedItemList);
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
                        mPresenter.onDelete(fileList, selectedItemList);
                        break;
                    case R.id.action_show_hide:
                        mPresenter.onShowHideFile(fileList);
                        break;
                    case R.id.action_select_all:
                        mPresenter.mEditType = Constants.EditType.SELECT;
                        mImageAdapter.selectAll();
                        mActionMode.setTitle(String.format(getString(R.string.tips_selected), mImageAdapter.getSelectedItemCount()));
                        if (mImageAdapter.getSelectedItemCount() == 0) {
                            finishAction();
                            mPresenter.refreshAfterFinishAction();
                        }
                        break;
                    case R.id.action_inverse_all:
                        mPresenter.mEditType = Constants.EditType.SELECT;
                        mImageAdapter.inverseAll();
                        mActionMode.setTitle(String.format(getString(R.string.tips_selected), mImageAdapter.getSelectedItemCount()));
                        if (mImageAdapter.getSelectedItemCount() == 0) {
                            finishAction();
                            mPresenter.refreshAfterFinishAction();
                        }
                        break;
                    case R.id.action_remark:
                        mPresenter.onRemark(fileList, selectedItemList);
                        break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                if (mPresenter.mEditType != Constants.EditType.COPY && mPresenter.mEditType != Constants.EditType.MOVE
                        && mPresenter.mEditType != Constants.EditType.ZIP && mPresenter.mEditType != Constants.EditType.UNZIP) {
                    getActivity().supportInvalidateOptionsMenu();
                    mActionMode = null;
                    mPresenter.mEditType = Constants.EditType.NONE;
                    if (mPresenter.isInAlbum) {
                        mAlbumAdapter.clearSelectedState();
                    } else {
                        mImageAdapter.clearSelectedState();
                    }
                }
                mPresenter.isActionModeActive = false;
            }
        });
    }

    private ActionMode getAlbumControlActionMode() {
        return getActivity().startActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mPresenter.isActionModeActive = true;
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                menu.clear();
                mode.getMenuInflater().inflate(R.menu.menu_control_image_file, menu);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                final ArrayList<FileItem> fileList = mAlbumAdapter.getSelectedDataList();
                final ArrayList<Integer> selectedItemList = mAlbumAdapter.getSelectedItemList();
                switch (item.getItemId()) {
                    case R.id.action_info:
                        mPresenter.onShowFileInfo(fileList);
                        break;
                    case R.id.action_delete:
                        mPresenter.onDeleteAlbum(fileList, selectedItemList);
                        break;
                    case R.id.action_select_all:
                        mPresenter.mEditType = Constants.EditType.SELECT;
                        mAlbumAdapter.selectAll();
                        mActionMode.setTitle(String.format(getString(R.string.tips_selected), mAlbumAdapter.getSelectedItemCount()));
                        if (mAlbumAdapter.getSelectedItemCount() == 0) {
                            finishAction();
                            mPresenter.refreshAfterFinishAction();
                        }
                        break;
                    case R.id.action_inverse_all:
                        mPresenter.mEditType = Constants.EditType.SELECT;
                        mAlbumAdapter.inverseAll();
                        mActionMode.setTitle(String.format(getString(R.string.tips_selected), mAlbumAdapter.getSelectedItemCount()));
                        if (mAlbumAdapter.getSelectedItemCount() == 0) {
                            finishAction();
                            mPresenter.refreshAfterFinishAction();
                        }
                        break;
                    case R.id.action_remark:
                        mPresenter.onRemark(fileList, selectedItemList);
                        break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                if (mPresenter.mEditType != Constants.EditType.COPY && mPresenter.mEditType != Constants.EditType.MOVE
                        && mPresenter.mEditType != Constants.EditType.ZIP && mPresenter.mEditType != Constants.EditType.UNZIP) {
                    getActivity().supportInvalidateOptionsMenu();
                    mActionMode = null;
                    mPresenter.mEditType = Constants.EditType.NONE;
                    if (mPresenter.isInAlbum) {
                        mAlbumAdapter.clearSelectedState();
                    } else {
                        mImageAdapter.clearSelectedState();
                    }
                }
                mPresenter.isActionModeActive = false;
            }
        });
    }

    public int getScrollYDistance() {
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        View firstVisibleChildView = layoutManager.findViewByPosition(position);
        int itemHeight = firstVisibleChildView.getHeight();
        return (position / mDefaultGridCount) * itemHeight - firstVisibleChildView.getTop();
    }

    public boolean isActionModeActive() {
        return mPresenter.isActionModeActive;
    }

    /************************************ 转场动画相关 ************************************/
    private OnSharedViewListener mSharedViewListener = new OnSharedViewListener() {
        @Override
        public void onSharedViewListener(int enterPosition) {
            setCallback(enterPosition);
        }
    };

    @TargetApi(21)
    public void onActivityReenter(int resultCode, Intent data) {
        getActivity().postponeEnterTransition();
        mExitPosition = data.getIntExtra(getString(R.string.intent_exit_position), mEnterPosition);
        mRecyclerView.scrollToPosition(mExitPosition);
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                getActivity().startPostponedEnterTransition();
            }
        });
    }

    @TargetApi(21)
    private void setCallback(final int enterPosition) {
        mEnterPosition = enterPosition;
        getActivity().setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                if (mExitPosition != -1 && mExitPosition != enterPosition && names.size() > 0) {
                    names.clear();
                    sharedElements.clear();
                    View view = mRecyclerView.findViewById(mExitPosition);
                    if (view != null) {
                        names.add(view.getTransitionName());
                        sharedElements.put(view.getTransitionName(), view);
                    }
                }
                mExitPosition = -1;
                setExitSharedElementCallback(null);
            }
        });
    }

}
