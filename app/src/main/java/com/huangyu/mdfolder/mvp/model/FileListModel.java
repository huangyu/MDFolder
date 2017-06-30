package com.huangyu.mdfolder.mvp.model;

import android.content.ContentResolver;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.huangyu.library.app.BaseApplication;
import com.huangyu.library.mvp.IBaseModel;
import com.huangyu.library.util.FileUtils;
import com.huangyu.mdfolder.app.Constants;
import com.huangyu.mdfolder.bean.FileItem;
import com.huangyu.mdfolder.utils.SDCardUtils;
import com.huangyu.mdfolder.utils.CompressUtils;
import com.huangyu.mdfolder.utils.comparator.AlphabetComparator;
import com.huangyu.mdfolder.utils.comparator.SizeComparator;
import com.huangyu.mdfolder.utils.comparator.TimeComparator;
import com.huangyu.mdfolder.utils.comparator.TypeComparator;
import com.huangyu.mdfolder.utils.filter.SearchFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by huangyu on 2017-5-24.
 */
public class FileListModel implements IBaseModel {

    public FileListModel() {

    }

    public ArrayList<File> getFileList(String path, String searchStr) {
        if (TextUtils.isEmpty(searchStr)) {
            return FileUtils.listFilesInDirWithFilter(path, new SearchFilter(searchStr), false);
        } else {
            return FileUtils.listFilesInDirWithFilter(path, new SearchFilter(searchStr), true);
        }
    }

//    public ArrayList<File> getAppsFileList(String searchStr) {
//        return FileUtils.listFilesInDirWithFilter(getStorageCardPath(false), new ApkFilter(searchStr), true);
//    }
//
//    public ArrayList<File> getMusicFileList(String searchStr) {
//        return FileUtils.listFilesInDirWithFilter(getStorageCardPath(false), new MusicFilter(searchStr), true);
//    }
//
//    public ArrayList<File> getPhotoFileList(String searchStr) {
//        return FileUtils.listFilesInDirWithFilter(getStorageCardPath(false), new PhotoFilter(searchStr), true);
//    }
//
//    public ArrayList<File> getVideoFileList(String searchStr) {
//        return FileUtils.listFilesInDirWithFilter(getStorageCardPath(false), new VideoFilter(searchStr), true);
//    }

    public ArrayList<FileItem> getGlobalFileListBySearch(String searchStr, ContentResolver contentResolver) {
        String[] projection = new String[]{
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_MODIFIED};

        Cursor cursor = contentResolver.query(MediaStore.Files.getContentUri("external"), projection,
                MediaStore.Files.FileColumns.DATA + " like ? ",
                new String[]{"%" + searchStr + "%"}, null);

        if (cursor != null) {
            PackageManager pm = BaseApplication.getInstance().getApplicationContext().getPackageManager();
            ArrayList<FileItem> documentList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                String fileLength = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                String date = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED));

                String fileRealName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
                if (FileUtils.isFileExists(filePath) && !isFolder(fileRealName)) {
                    FileItem fileItem = new FileItem();
                    fileItem.setName(fileRealName);
                    fileItem.setPath(filePath);
                    fileItem.setSize(fileLength);
                    fileItem.setDate(date);
                    fileItem.setParent(null);
                    fileItem.setIsDirectory(false);
                    fileItem.setType(Constants.FileType.APK);
                    fileItem.setIsShow(true);

                    if (fileRealName.endsWith(".apk")) {
                        PackageInfo packageInfo = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
                        ApplicationInfo appInfo = packageInfo.applicationInfo;
                        appInfo.sourceDir = filePath;
                        appInfo.publicSourceDir = filePath;
                        Drawable icon = appInfo.loadIcon(pm);
                        fileItem.setApkIcon(icon);

                        if (TextUtils.isEmpty(searchStr) || fileRealName.contains(searchStr)) {
                            documentList.add(fileItem);
                        }
                    }
                }
            }
            cursor.close();
            return documentList;
        }
        return null;
    }

    private boolean isFolder(String fileRealName) {
        int index = fileRealName.indexOf(".");
        if (index > -1 && index != 0 && index != fileRealName.length() - 1) {
            return false;
        }
        return true;
    }

    public ArrayList<FileItem> getDocumentList(String searchStr, ContentResolver contentResolver) {
        String[] projection = new String[]{MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.MIME_TYPE};

        Cursor cursor = contentResolver.query(MediaStore.Files.getContentUri("external"), projection,
                MediaStore.Files.FileColumns.MIME_TYPE + " like ? or "
                        + MediaStore.Files.FileColumns.MIME_TYPE + " like ? or "
                        + MediaStore.Files.FileColumns.DATA + " like ? or "
                        + MediaStore.Files.FileColumns.DATA + " like ? or "
                        + MediaStore.Files.FileColumns.DATA + " like ? or "
                        + MediaStore.Files.FileColumns.DATA + " like ? or "
                        + MediaStore.Files.FileColumns.MIME_TYPE + " like ? ",
                new String[]{
                        "application/msword",
                        "application/vnd.ms-excel",
                        "%.ppt",
                        "%.docx",
                        "%.xlsx",
                        "%.pptx",
                        "application/pdf",
                }, null);

        if (cursor != null) {
            ArrayList<FileItem> documentList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                String fileLength = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                String date = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED));

                String fileRealName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
                if (FileUtils.isFileExists(filePath)) {
                    FileItem fileItem = new FileItem();
                    fileItem.setName(fileRealName);
                    fileItem.setPath(filePath);
                    fileItem.setSize(fileLength);
                    fileItem.setDate(date);
                    fileItem.setParent(null);
                    fileItem.setIsDirectory(false);
                    fileItem.setType(Constants.FileType.DOCUMENT);
                    fileItem.setIsShow(true);
                    if (TextUtils.isEmpty(searchStr) || fileRealName.contains(searchStr)) {
                        documentList.add(fileItem);
                    }
                }
            }
            cursor.close();
            return documentList;
        }
        return null;
    }

    public ArrayList<FileItem> getVideoList(String searchStr, ContentResolver contentResolver) {
        String[] projection = new String[]{MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.VideoColumns.SIZE,
                MediaStore.Video.VideoColumns.DATE_MODIFIED};

        Cursor cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null, null, null);

        if (cursor != null) {
            ArrayList<FileItem> videoList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
                String fileLength = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.SIZE));
                String date = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATE_MODIFIED));

                String fileRealName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
                if (FileUtils.isFileExists(filePath)) {
                    FileItem fileItem = new FileItem();
                    fileItem.setName(fileRealName);
                    fileItem.setPath(filePath);
                    fileItem.setSize(fileLength);
                    fileItem.setDate(date);
                    fileItem.setParent(null);
                    fileItem.setIsDirectory(false);
                    fileItem.setType(Constants.FileType.VIDEO);
                    fileItem.setIsShow(true);
                    if (TextUtils.isEmpty(searchStr) || fileRealName.contains(searchStr)) {
                        videoList.add(fileItem);
                    }
                }
            }
            cursor.close();
            return videoList;
        }
        return null;
    }

    public ArrayList<FileItem> getImageList(String searchStr, ContentResolver contentResolver) {
        String[] projection = new String[]{MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.SIZE,
                MediaStore.Images.ImageColumns.DATE_MODIFIED};
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null,
                null);
        if (cursor != null) {
            ArrayList<FileItem> imageList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                String fileLength = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE));
                String date = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_MODIFIED));

                String fileRealName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
                if (FileUtils.isFileExists(filePath)) {
                    FileItem fileItem = new FileItem();
                    fileItem.setName(fileRealName);
                    fileItem.setPath(filePath);
                    fileItem.setSize(fileLength);
                    fileItem.setDate(date);
                    fileItem.setParent(null);
                    fileItem.setIsDirectory(false);
                    fileItem.setType(Constants.FileType.IMAGE);
                    fileItem.setIsShow(true);
                    if (TextUtils.isEmpty(searchStr) || fileRealName.contains(searchStr)) {
                        imageList.add(fileItem);
                    }
                }
            }
            cursor.close();
            return imageList;
        }
        return null;
    }

    public ArrayList<FileItem> getAudioList(String searchStr, ContentResolver contentResolver) {
        String[] projection = new String[]{MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.SIZE,
                MediaStore.Audio.AudioColumns.DATE_MODIFIED};

        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null);

        if (cursor != null) {
            ArrayList<FileItem> audioList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
                String fileLength = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.SIZE));
                String date = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATE_MODIFIED));

                String fileRealName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
                if (FileUtils.isFileExists(filePath)) {
                    FileItem fileItem = new FileItem();
                    fileItem.setName(fileRealName);
                    fileItem.setPath(filePath);
                    fileItem.setSize(fileLength);
                    fileItem.setDate(date);
                    fileItem.setParent(null);
                    fileItem.setIsDirectory(false);
                    fileItem.setType(Constants.FileType.AUDIO);
                    fileItem.setIsShow(true);
                    if (TextUtils.isEmpty(searchStr) || fileRealName.contains(searchStr)) {
                        audioList.add(fileItem);
                    }
                }
            }
            cursor.close();
            return audioList;
        }
        return null;
    }

    public ArrayList<FileItem> getApkList(String searchStr, ContentResolver contentResolver) {
        String[] projection = new String[]{
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_MODIFIED};

        Cursor cursor = contentResolver.query(MediaStore.Files.getContentUri("external"), projection,
                MediaStore.Files.FileColumns.DATA + " like ? ",
                new String[]{"%" + ".apk"}, null);

        if (cursor != null) {
            PackageManager pm = BaseApplication.getInstance().getApplicationContext().getPackageManager();

            ArrayList<FileItem> apkList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                String fileLength = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                String date = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED));

                if (FileUtils.isFileExists(filePath)) {
                    PackageInfo packageInfo = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
                    ApplicationInfo appInfo = packageInfo.applicationInfo;
                    appInfo.sourceDir = filePath;
                    appInfo.publicSourceDir = filePath;
                    FileItem fileItem = new FileItem();
                    String fileRealName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
                    fileItem.setName(fileRealName);
                    fileItem.setPath(filePath);
                    fileItem.setSize(fileLength);
                    fileItem.setDate(date);
                    fileItem.setParent(null);
                    fileItem.setIsDirectory(false);
                    fileItem.setType(Constants.FileType.APK);
                    fileItem.setIsShow(true);
                    Drawable icon = appInfo.loadIcon(pm);
                    fileItem.setApkIcon(icon);
                    if (TextUtils.isEmpty(searchStr) || fileRealName.contains(searchStr)) {
                        apkList.add(fileItem);
                    }
                }
            }
            cursor.close();
            return apkList;
        }
        return null;
    }

    public ArrayList<FileItem> getCompressList(String searchStr, ContentResolver contentResolver) {
        String[] projection = new String[]{
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_MODIFIED};

        Cursor cursor = contentResolver.query(MediaStore.Files.getContentUri("external"), projection,
                MediaStore.Files.FileColumns.DATA + " like ? or " +
                        MediaStore.Files.FileColumns.DATA + " like ? or " +
                        MediaStore.Files.FileColumns.DATA + " like ? ",
                new String[]{"%" + ".zip", "%" + ".rar", "%" + ".7z"}, null);

        if (cursor != null) {
            ArrayList<FileItem> apkList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                String fileLength = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                String date = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED));

                if (FileUtils.isFileExists(filePath)) {
                    FileItem fileItem = new FileItem();
                    String fileRealName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
                    fileItem.setName(fileRealName);
                    fileItem.setPath(filePath);
                    fileItem.setSize(fileLength);
                    fileItem.setDate(date);
                    fileItem.setParent(null);
                    fileItem.setIsDirectory(false);
                    fileItem.setType(Constants.FileType.COMPRESS);
                    fileItem.setIsShow(true);
                    if (TextUtils.isEmpty(searchStr) || fileRealName.contains(searchStr)) {
                        apkList.add(fileItem);
                    }
                }
            }
            cursor.close();
            return apkList;
        }
        return null;
    }

    /**
     * 获取根目录文件路径
     *
     * @return
     */
    public String getRootPath() {
        return Environment.getRootDirectory().getPath();
//        return "/";
    }

    /**
     * 获取存储卡路径
     *
     * @return
     */
    public String getStorageCardPath(boolean isOuter) {
        return SDCardUtils.getStoragePath(BaseApplication.getInstance().getApplicationContext(), isOuter);
    }

    /**
     * 获取下载目录路径
     *
     * @return
     */
    public String getDownloadPath() {
        return SDCardUtils.getSDCardPath() + "Download";
    }

    /**
     * 按字母排序
     */
    public ArrayList<FileItem> orderByAlphabet(ArrayList<FileItem> fileList) {
        Collections.sort(fileList, new AlphabetComparator());
        return fileList;
    }

    /**
     * 按时间排序
     */
    public ArrayList<FileItem> orderByTime(ArrayList<FileItem> fileList) {
        Collections.sort(fileList, new TimeComparator());
        return fileList;
    }

    /**
     * 按类型排序
     */
    public ArrayList<FileItem> orderByType(ArrayList<FileItem> fileList) {
        Collections.sort(fileList, new TypeComparator());
        return fileList;
    }

    /**
     * 按大小排序
     */
    public ArrayList<FileItem> orderBySize(ArrayList<FileItem> fileList) {
        Collections.sort(fileList, new SizeComparator());
        return fileList;
    }

    /**
     * 逆序
     */
    public ArrayList<FileItem> orderByOrder(ArrayList<FileItem> fileList) {
        Collections.reverse(fileList);
        return fileList;
    }

    /**
     * 压缩文件
     *
     * @param resFiles    文件列表
     * @param zipFilePath 文件路径
     * @return true/false
     */
    public boolean zipFileList(ArrayList<File> resFiles, String zipFilePath) {
        return CompressUtils.zipFile(resFiles, zipFilePath);
    }

    /**
     * 解压缩文件
     *
     * @param zipFilePath 解压文件路径
     * @param toPath      目标文件路径
     * @return true/false
     */
    public boolean unZipFileList(String zipFilePath, String toPath) {
        return CompressUtils.unZipFile(zipFilePath, toPath);
    }

    /**
     * 解压缩文件
     *
     * @param zipFilePath 解压文件路径
     * @param toPath      目标文件路径
     * @param password    解压密码
     * @return true/false
     */
    public boolean unZipFileList(String zipFilePath, String toPath, String password) {
        return CompressUtils.unZipFile(zipFilePath, toPath, password);
    }

    /**
     * 解压缩文件
     *
     * @param zipFilePath 解压文件路径
     * @param toPath      目标文件路径
     * @return true/false
     */
    public boolean un7zipFileList(String zipFilePath, String toPath) {
        return CompressUtils.un7zipFile(zipFilePath, toPath);
    }

    /**
     * 解压缩文件
     *
     * @param zipFilePath 解压文件路径
     * @param toPath      目标文件路径
     * @return true/false
     */
    public boolean unRarFileList(String zipFilePath, String toPath) {
        return CompressUtils.unRarFile(zipFilePath, toPath);
    }

}
