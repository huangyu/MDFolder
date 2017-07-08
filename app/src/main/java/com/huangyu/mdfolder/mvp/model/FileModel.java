package com.huangyu.mdfolder.mvp.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.huangyu.library.mvp.IBaseModel;
import com.huangyu.library.util.FileUtils;
import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.utils.MimeTypeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangyu on 2017-5-24.
 */
public class FileModel implements IBaseModel {

    /**
     * 打开文件
     *
     * @param context 上下文
     * @param file    文件
     * @return true/false
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
                intent.setDataAndType(uri, MimeTypeUtils.getMIMEType(file.getPath()));
                context.startActivity(intent);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * 分享文件
     *
     * @param context  上下文
     * @param fileList 文件列表
     * @return true/false
     */
    public boolean shareFile(Context context, List<File> fileList) {
        try {
            boolean multiple = fileList.size() > 1;
            Intent intent = new Intent(multiple ? android.content.Intent.ACTION_SEND_MULTIPLE : android.content.Intent.ACTION_SEND);
            if (multiple) {
                shareMultiFiles(context, intent, fileList);
            } else {
                shareSingleFile(context, intent, fileList.get(0));
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void shareSingleFile(Context context, Intent intent, File file) {
        String mimeType = MimeTypeUtils.getMIMEType(file.getPath());
        intent.setType(mimeType);
        Uri uri;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            uri = FileProvider.getUriForFile(context, "com.huangyu.mdfolder.fileprovider", file);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        } else {
        uri = Uri.fromFile(file);
//        }
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        context.startActivity(Intent.createChooser(intent, context.getString(R.string.menu_share)));
    }

    private void shareMultiFiles(Context context, Intent intent, List<File> fileList) {
        ArrayList<Uri> uriArrayList = new ArrayList<>();
        for (File file : fileList) {
            Uri uri;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                uri = FileProvider.getUriForFile(context, "com.huangyu.mdfolder.fileprovider", file);
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            } else {
            uri = Uri.fromFile(file);
//            }
            uriArrayList.add(uri);
        }
        intent.setType("*/*");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriArrayList);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.menu_share)));
    }

    public void inputStreamToFile(InputStream is, File file) throws IOException {
        OutputStream os = new FileOutputStream(file);
        int bytesRead;
        byte[] buffer = new byte[8192];
        while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        is.close();
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
