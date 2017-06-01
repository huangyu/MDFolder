package com.huangyu.mdfolder.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.huangyu.library.ui.BaseFragment;
import com.huangyu.library.ui.CommonRecyclerViewAdapter;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.mvp.presenter.FileListPresenter;
import com.huangyu.mdfolder.mvp.view.IFileListView;
import com.huangyu.mdfolder.ui.adapter.FileListAdapter;
import com.huangyu.mdfolder.ui.widget.TabView;
import com.huangyu.mdfolder.utils.AlertUtils;
import com.huangyu.mdfolder.utils.KeyboardUtils;

import java.io.File;

import butterknife.Bind;
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

    @Bind(R.id.fam_add)
    FloatingActionMenu mFamAdd;

    @Bind(R.id.fab_add_file)
    FloatingActionButton mFabAddFile;

    @Bind(R.id.fab_add_folder)
    FloatingActionButton mFabAddFolder;

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
        mAdapter = new FileListAdapter(getContext());
        mAdapter.setOnItemClick(new CommonRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                File file = mAdapter.getItem(position);
                if (file.isDirectory()) {
                    mPresenter.enterFolder(file);
                } else {
                    mPresenter.openFile(getContext(), file);
                }

                if (mActionMode != null) {
                    mActionMode.finish();
                }
            }
        });
        mAdapter.setOnItemLongClick(new CommonRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final int position) {
                mActionMode = getActivity().startActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        mode.getMenuInflater().inflate(R.menu.menu_control, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_delete:
                                final File file = mAdapter.getItem(position);
                                if (file.isDirectory()) {
                                    AlertUtils.showNormalAlert(getContext(), getString(R.string.tips_delete_folder), getString(R.string.act_delete), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mPresenter.deleteFolder(file.getPath());
                                            refreshData();
                                            dialog.dismiss();

                                            if (mActionMode != null) {
                                                mActionMode.finish();
                                            }
                                        }
                                    });
                                } else {
                                    AlertUtils.showNormalAlert(getContext(), getString(R.string.tips_delete_file), getString(R.string.act_delete), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mPresenter.deleteFile(file.getPath());
                                            refreshData();
                                            dialog.dismiss();

                                            if (mActionMode != null) {
                                                mActionMode.finish();
                                            }
                                        }
                                    });
                                }
                                break;
                        }
                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        refreshData();
                        getActivity().supportInvalidateOptionsMenu();

                        if (mActionMode != null) {
                            mActionMode = null;
                        }
                    }
                });
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshData();
                        mSwipeRefreshLayout.setRefreshing(false);

                        if (mActionMode != null) {
                            mActionMode.finish();
                        }
                    }
                }, 50);
            }
        });

        mFabAddFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View view = inflater.inflate(R.layout.dialog_add, new LinearLayout(getContext()), false);
                final AppCompatEditText editText = (AppCompatEditText) view.findViewById(R.id.et_name);
                mCoordinatorLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        KeyboardUtils.showSoftInput(editText);
                    }
                }, 200);
                AlertUtils.showCustomAlert(getContext(), getString(R.string.tips_add_file), view, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String fileName = editText.getText().toString();
                        String filePath = mPresenter.getCurrentPath() + File.separator + fileName;

                        if (mPresenter.isFileExists(filePath)) {
                            AlertUtils.showSnack(mCoordinatorLayout, getString(R.string.tips_file_exist));
                        } else {
                            if (mPresenter.addFile(filePath)) {
                                refreshData();
                            } else {
                                AlertUtils.showSnack(mCoordinatorLayout, getString(R.string.tips_add_file_error));
                            }
                        }
                        dialog.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCoordinatorLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                KeyboardUtils.hideSoftInput(getContext(), editText);
                            }
                        }, 200);
                        dialog.dismiss();
                    }
                });
                mFamAdd.close(true);
            }
        });
        mFabAddFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View view = inflater.inflate(R.layout.dialog_add, new LinearLayout(getContext()), false);
                final AppCompatEditText editText = (AppCompatEditText) view.findViewById(R.id.et_name);
                mCoordinatorLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        KeyboardUtils.showSoftInput(editText);
                    }
                }, 200);
                AlertUtils.showCustomAlert(getContext(), getString(R.string.tips_add_folder), view, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String fileName = editText.getText().toString();
                        String filePath = mPresenter.getCurrentPath() + File.separator + fileName;

                        if (mPresenter.isFolderExists(filePath)) {
                            AlertUtils.showSnack(mCoordinatorLayout, getString(R.string.tips_folder_exist));
                        } else {
                            if (mPresenter.addFolder(filePath)) {
                                refreshData();
                            } else {
                                AlertUtils.showSnack(mCoordinatorLayout, getString(R.string.tips_add_folder_error));
                            }
                        }
                        dialog.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCoordinatorLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                KeyboardUtils.hideSoftInput(getContext(), editText);
                            }
                        }, 200);
                        dialog.dismiss();
                    }
                });
                mFamAdd.close(true);
            }
        });

        mAdapter.setData(mPresenter.getRootFileList());

        mRxManager.on("search", new Action1<String>() {
            @Override
            public void call(String text) {
                mSearchStr = text;
                refreshData();
            }
        });

        mRxManager.on("resetSearch", new Action1<String>() {
            @Override
            public void call(String text) {
                mSearchStr = "";
                refreshData();
            }
        });
    }

    @Override
    public void addTab(String path) {
        mTabView.addTab(path, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag(R.id.tag);
                if (tag != null && tag instanceof Integer) {
                    int index = (Integer) tag;
                    mPresenter.enterCertainFolder(index);
                }

                if (mActionMode != null) {
                    mActionMode.finish();
                }
            }
        });
    }

    @Override
    public boolean removeTab() {
        return mTabView.removeTab();
    }

    @Override
    public void refreshData() {
        mAdapter.clearData();
        if (TextUtils.isEmpty(mSearchStr)) {
            mAdapter.setData(mPresenter.getCurrentFileList());
        } else {
            mAdapter.setData(mPresenter.getCurrentFileList(mSearchStr));
        }
    }

    public boolean onBackPressed() {
        if (mFamAdd.isOpened()) {
            mFamAdd.close(true);
            return true;
        }
        return mPresenter.backFolder();
    }

}
