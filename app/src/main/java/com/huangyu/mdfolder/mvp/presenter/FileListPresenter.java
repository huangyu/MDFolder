package com.huangyu.mdfolder.mvp.presenter;

import android.content.Context;

import com.huangyu.library.mvp.BasePresenter;
import com.huangyu.mdfolder.app.Constants;
import com.huangyu.mdfolder.mvp.model.FileListModel;
import com.huangyu.mdfolder.mvp.model.FileModel;
import com.huangyu.mdfolder.mvp.view.IFileListView;

import java.io.File;
import java.util.List;
import java.util.Stack;

/**
 * Created by huangyu on 2017/5/22.
 */

public class FileListPresenter extends BasePresenter<IFileListView> {

    private FileListModel mFileListModel;
    private FileModel mFileModel;
    private Stack<File> mFileStack;

    public String mCurrentPath; // 当前路径
    public int mEditMode;   // 当前编辑状态

    @Override
    public void create() {
        mFileListModel = new FileListModel();
        mFileModel = new FileModel();
        mFileStack = new Stack<>();
        mEditMode = Constants.EditType.NONE;
    }

    /**
     * 获取根目录文件列表
     *
     * @return
     */
    public List<File> getRootFileList() {
        mCurrentPath = mFileListModel.getRootPath();
        mView.addTab(mCurrentPath);
        mFileStack.push(new File(mCurrentPath));
        return getCurrentFileList();
    }

    /**
     * 获取存储器文件列表
     *
     * @return
     */
    public List<File> getStorageFileList() {
        mCurrentPath = mFileListModel.getSDCardPath();
        mView.addTab(mCurrentPath);
        mFileStack.push(new File(mCurrentPath));
        return getCurrentFileList();
    }

    /**
     * 获取当前路径文件列表
     *
     * @return
     */
    public List<File> getCurrentFileList() {
        return mFileListModel.orderByType(mFileListModel.orderByAlphabet(mFileListModel.getFileList(mCurrentPath)));
    }

    /**
     * 获取当前路径文件列表
     *
     * @param searchStr 查询文字
     * @return
     */
    public List<File> getCurrentFileList(String searchStr) {
        return mFileListModel.orderByType(mFileListModel.orderByAlphabet(mFileListModel.getFileList(mCurrentPath, searchStr)));
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
        mView.refreshData(false);
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
            mView.refreshData(false);
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
            mView.refreshData(false);
            return true;
        }
        return false;
    }

    public boolean hasFilePermission(String path) {
        return mFileModel.hasFilePermission(path);
    }

    public boolean hasFilePermission(File file) {
        return mFileModel.hasFilePermission(file);
    }

    public boolean openFile(Context context, File file) {
        return mFileModel.openFile(context, file);
    }

    public boolean isFileExists(String path) {
        return mFileModel.isFileExists(path);
    }

    public boolean isFolderExists(String path) {
        return mFileModel.isFolderExists(path);
    }

    public boolean addFile(String filePath) {
        return mFileModel.addFile(filePath);
    }

    public boolean addFolder(String folderPath) {
        return mFileModel.addFolder(folderPath);
    }

    public boolean deleteFile(String filePath) {
        return mFileModel.deleteFile(filePath);
    }

    public boolean deleteFolder(String folderPath) {
        return mFileModel.deleteFolder(folderPath);
    }

    public boolean moveFile(String srcFilePath, String destFilePath) {
        return mFileModel.moveFile(srcFilePath, destFilePath);
    }

    public boolean moveFolder(String srcFolderPath, String destFolderPath) {
        return mFileModel.moveFolder(srcFolderPath, destFolderPath);
    }

    public boolean copyFile(String srcFilePath, String destFilePath) {
        return mFileModel.copyFile(srcFilePath, destFilePath);
    }

    public boolean copyFolder(String srcFolderPath, String destFolderPath) {
        return mFileModel.copyFolder(srcFolderPath, destFolderPath);
    }

    public boolean renameFile(String filePath, String newName) {
        return mFileModel.renameFile(filePath, newName);
    }

}
