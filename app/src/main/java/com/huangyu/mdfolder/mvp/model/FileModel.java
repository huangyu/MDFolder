package com.huangyu.mdfolder.mvp.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.huangyu.library.mvp.IBaseModel;
import com.huangyu.library.util.FileUtils;
import com.huangyu.mdfolder.utils.MimeTypeUtils;

import java.io.File;

/**
 * Created by huangyu on 2017-5-24.
 */
public class FileModel implements IBaseModel {

    /**
     * 打开文件
     *
     * @param file 文件
     */
    public boolean openFile(Context context, File file) {
        if (file != null && !file.isDirectory() && file.exists()) {
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(context, "com.huangyu.mdfolder.fileprovider", file);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    uri = Uri.fromFile(file);
                }
                intent.setDataAndType(uri, getMIMEType(file));
                context.startActivity(Intent.createChooser(intent, "选择应用打开文件"));
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    private String getMIMEType(File file) {
        return MimeTypeUtils.getMIMEType(file.getPath());
    }

    public boolean hasFilePermission(String path) {
        return new File(path).canExecute();
    }

    public boolean hasFilePermission(File file) {
        return file.canExecute();
    }

    public boolean isFileExists(String path) {
        return FileUtils.isFileExists(path) && !FileUtils.isDir(path);
    }

    public boolean isFolderExists(String path) {
        return FileUtils.isFileExists(path) && FileUtils.isDir(path);
    }

    public boolean addFile(String filePath) {
        return FileUtils.createFile(filePath);
    }

    public boolean addFolder(String folderPath) {
        return FileUtils.createFolder(folderPath);
    }

    public boolean deleteFile(String filePath) {
        return FileUtils.deleteFile(filePath);
    }

    public boolean deleteFolder(String folderPath) {
        return FileUtils.deleteDir(folderPath);
    }

    public boolean moveFile(String srcFilePath, String destFilePath) {
        return FileUtils.moveFile(srcFilePath, destFilePath);
    }

    public boolean moveFolder(String srcFolderPath, String destFolderPath) {
        return FileUtils.moveDir(srcFolderPath, destFolderPath);
    }

    public boolean copyFile(String srcFilePath, String destFilePath) {
        return FileUtils.copyFile(srcFilePath, destFilePath);
    }

    public boolean copyFolder(String srcFolderPath, String destFolderPath) {
        return FileUtils.copyDir(srcFolderPath, destFolderPath);
    }

    public boolean renameFile(String filePath, String newName) {
        return FileUtils.rename(filePath, newName);
    }

    public boolean hideFile(String filePath, String newName) {
        return FileUtils.rename(filePath, "." + newName);
    }

    public boolean showFile(String filePath, String newName) {
        return FileUtils.rename(filePath, newName.replaceFirst("\\.", ""));
    }

}
