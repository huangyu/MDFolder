package com.huangyu.mdfolder.mvp.presenter;

import com.huangyu.library.mvp.BasePresenter;
import com.huangyu.mdfolder.mvp.model.FileListModel;
import com.huangyu.mdfolder.mvp.view.IMainView;

import java.io.File;
import java.util.List;
import java.util.Stack;

/**
 * Created by huangyu on 2017/5/22.
 */

public class MainPresenter extends BasePresenter<IMainView> {

    private FileListModel mFileModel;
    private Stack<File> mFileStack;
    private String mCurrentPath;

    @Override
    public void create() {
        mFileModel = new FileListModel();
        mFileStack = new Stack<>();
    }

    public List<File> getRootFileList() {
        mCurrentPath = mFileModel.getSDCardPath();
        mView.addTab(mCurrentPath);
        mFileStack.push(new File(mCurrentPath));
        return getCurrentFileList();
    }

    public List<File> getCurrentFileList() {
        return mFileModel.orderByType(mFileModel.orderByAlphabet(mFileModel.getFileList(mCurrentPath)));
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
     * 点击返回返回的文件夹
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

}
