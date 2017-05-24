package com.huangyu.mdfolder.mvp.presenter;

import android.content.Context;

import com.huangyu.library.mvp.BasePresenter;
import com.huangyu.mdfolder.mvp.model.FileListModel;
import com.huangyu.mdfolder.mvp.model.FileModel;
import com.huangyu.mdfolder.mvp.view.IMainView;

import java.io.File;
import java.util.List;
import java.util.Stack;

/**
 * Created by huangyu on 2017/5/22.
 */

public class MainPresenter extends BasePresenter<IMainView> {

    private FileListModel mFileListModel;
    private FileModel mFileModel;
    private Stack<File> mFileStack;
    private String mCurrentPath;

    @Override
    public void create() {
        mFileListModel = new FileListModel();
        mFileModel = new FileModel();
        mFileStack = new Stack<>();
    }

    public List<File> getRootFileList() {
        mCurrentPath = mFileListModel.getSDCardPath();
        mView.addTab(mCurrentPath);
        mFileStack.push(new File(mCurrentPath));
        return getCurrentFileList();
    }

    public List<File> getCurrentFileList() {
        return mFileListModel.orderByType(mFileListModel.orderByAlphabet(mFileListModel.getFileList(mCurrentPath)));
    }

    /**
     * 进入某个文件夹
     *
     * @param file
     */
    public void enterFolder(File file) {
        mFileStack.push(file);
        mView.addTab(file.getName());
        mCurrentPath = file.getPath();
        mView.refreshData();
    }

    /**
     * 点击路径进入某个文件夹
     *
     * @param index
     * @return
     */
    public boolean enterCertainFolder(int index) {
        boolean isRemoved = false;
        while (mFileStack.size() > index + 1) {
            mFileStack.pop();
            mView.removeTab();
            isRemoved = true;
        }
        if (isRemoved) {
            File file = mFileStack.peek();
            mCurrentPath = file.getPath();
            mView.refreshData();
        }
        return isRemoved;
    }

    /**
     * 点击返回显示的文件夹
     *
     * @return
     */
    public boolean backFolder() {
        if (mFileStack.size() > 1) {
            mFileStack.pop();
            mView.removeTab();
            File file = mFileStack.peek();
            mCurrentPath = file.getPath();
            mView.refreshData();
            return true;
        }
        return false;
    }

    public void openFile(Context context, File file) {
        mFileModel.openFile(context, file);
    }

}
