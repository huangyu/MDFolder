package com.huangyu.mdfolder.mvp.model;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.huangyu.library.app.BaseApplication;
import com.huangyu.library.mvp.IBaseModel;
import com.huangyu.library.util.FileUtils;
import com.huangyu.mdfolder.app.Constants;
import com.huangyu.mdfolder.bean.FileItem;
import com.huangyu.mdfolder.utils.DateUtils;
import com.huangyu.mdfolder.utils.SDCardUtils;
import com.huangyu.mdfolder.utils.ZipUtils;
import com.huangyu.mdfolder.utils.comparator.AlphabetComparator;
import com.huangyu.mdfolder.utils.comparator.SizeComparator;
import com.huangyu.mdfolder.utils.comparator.TimeComparator;
import com.huangyu.mdfolder.utils.comparator.TypeComparator;
import com.huangyu.mdfolder.utils.filter.SearchFilter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by huangyu on 2017-5-24.
 */
public class FileListModel implements IBaseModel {

    public FileListModel() {

    }

    public ArrayList<File> getFileList(String path, String searchStr) {
        return FileUtils.listFilesInDirWithFilter(path, new SearchFilter(searchStr), false);
    }

//    public List<File> getAppsFileList(String searchStr) {
//        return FileUtils.listFilesInDirWithFilter(getStorageCardPath(), new ApkFilter(searchStr), true);
//    }
//
//    public List<File> getMusicFileList(String searchStr) {
//        return FileUtils.listFilesInDirWithFilter(getStorageCardPath(), new MusicFilter(searchStr), true);
//    }
//
//    public List<File> getPhotoFileList(String searchStr) {
//        return FileUtils.listFilesInDirWithFilter(getStorageCardPath(), new PhotoFilter(searchStr), true);
//    }
//
//    public List<File> getVideoFileList(String searchStr) {
//        return FileUtils.listFilesInDirWithFilter(getStorageCardPath(), new VideoFilter(searchStr), true);
//    }

    public ArrayList<FileItem> getDocumentList(String searchStr, ContentResolver contentResolver) {
        String[] projection = new String[]{MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.TITLE, MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_MODIFIED, MediaStore.Files.FileColumns.MIME_TYPE};

        Cursor cursor = contentResolver.query(MediaStore.Files.getContentUri("external"), projection,
                MediaStore.Files.FileColumns.DATA + " like ? or "
                        + MediaStore.Files.FileColumns.DATA + " like ? or "
                        + MediaStore.Files.FileColumns.DATA + " like ? or "
                        + MediaStore.Files.FileColumns.DATA + " like ? or "
                        + MediaStore.Files.FileColumns.DATA + " like ? or "
                        + MediaStore.Files.FileColumns.DATA + " like ? or "
                        + MediaStore.Files.FileColumns.DATA + " like ? ",
                new String[]{
                        "%.doc",
                        "%.xls",
                        "%.ppt",
                        "%.docx",
                        "%.xlsx",
                        "%.pptx",
                        "%.pdf",
                }, MediaStore.Files.FileColumns.TITLE + " asc");

        if (cursor != null) {
            ArrayList<FileItem> documentList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                String date = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED));

                if (FileUtils.isFileExists(filePath)) {
                    FileItem fileItem = new FileItem();
                    fileItem.setName(filePath.substring(filePath.lastIndexOf(File.separator) + 1));
                    fileItem.setPath(filePath);
                    fileItem.setSize(FileUtils.getFileLength(filePath));
                    fileItem.setDate(DateUtils.getFormatDate(Long.valueOf(date) * 1000));
                    fileItem.setParent(null);
                    fileItem.setIsDirectory(false);
                    fileItem.setType(Constants.FileType.DOCUMENT);
                    fileItem.setIsShow(true);
                    if (TextUtils.isEmpty(searchStr) || fileName.contains(searchStr)) {
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
                MediaStore.Video.VideoColumns.DISPLAY_NAME, MediaStore.Video.VideoColumns.SIZE,
                MediaStore.Video.VideoColumns.DATE_MODIFIED};

        Cursor cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null, null, MediaStore.Video.VideoColumns.DISPLAY_NAME + " asc");

        if (cursor != null) {
            ArrayList<FileItem> videoList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME));
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
                String date = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATE_MODIFIED));

                if (FileUtils.isFileExists(filePath)) {
                    FileItem fileItem = new FileItem();
                    fileItem.setName(fileName);
                    fileItem.setPath(filePath);
                    fileItem.setSize(FileUtils.getFileLength(filePath));
                    fileItem.setDate(DateUtils.getFormatDate(Long.valueOf(date) * 1000));
                    fileItem.setParent(null);
                    fileItem.setIsDirectory(false);
                    fileItem.setType(Constants.FileType.VIDEO);
                    fileItem.setIsShow(true);
                    if (TextUtils.isEmpty(searchStr) || fileName.contains(searchStr)) {
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
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.SIZE,
                MediaStore.Images.ImageColumns.DATE_MODIFIED};
        Cursor cursor = contentResolver.query(imageUri, projection, null, null,
                MediaStore.Images.ImageColumns.DISPLAY_NAME + " asc");
        if (cursor != null) {
            ArrayList<FileItem> imageList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                String date = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_MODIFIED));

                if (FileUtils.isFileExists(filePath)) {
                    FileItem fileItem = new FileItem();
                    fileItem.setName(fileName);
                    fileItem.setPath(filePath);
                    fileItem.setSize(FileUtils.getFileLength(filePath));
                    fileItem.setDate(DateUtils.getFormatDate(Long.valueOf(date) * 1000));
                    fileItem.setParent(null);
                    fileItem.setIsDirectory(false);
                    fileItem.setType(Constants.FileType.IMAGE);
                    fileItem.setIsShow(true);
                    if (TextUtils.isEmpty(searchStr) || fileName.contains(searchStr)) {
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
                MediaStore.Audio.AudioColumns.DISPLAY_NAME, MediaStore.Audio.AudioColumns.SIZE,
                MediaStore.Audio.AudioColumns.DATE_MODIFIED};

        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, MediaStore.Audio.AudioColumns.DISPLAY_NAME + " asc");

        if (cursor != null) {
            ArrayList<FileItem> audioList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME));
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
                String date = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATE_MODIFIED));

                if (FileUtils.isFileExists(filePath)) {
                    FileItem fileItem = new FileItem();
                    fileItem.setName(fileName);
                    fileItem.setPath(filePath);
                    fileItem.setSize(FileUtils.getFileLength(filePath));
                    fileItem.setDate(DateUtils.getFormatDate(Long.valueOf(date) * 1000));
                    fileItem.setParent(null);
                    fileItem.setIsDirectory(false);
                    fileItem.setType(Constants.FileType.AUDIO);
                    fileItem.setIsShow(true);
                    if (TextUtils.isEmpty(searchStr) || fileName.contains(searchStr)) {
                        audioList.add(fileItem);
                    }
                }
            }
            cursor.close();
            return audioList;
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
    }

    /**
     * 获取存储卡路径
     *
     * @return
     */
    public String getStorageCardPath(boolean isInner) {
        return SDCardUtils.getStoragePath(BaseApplication.getInstance().getApplicationContext(), isInner);
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
    public boolean zipFileList(Collection<File> resFiles, String zipFilePath) {
        try {
            return ZipUtils.zipFiles(resFiles, zipFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 解压缩文件
     *
     * @param resFiles    文件列表
     * @param zipFilePath 文件路径
     * @return true/false
     */
    public boolean unzipFileList(Collection<File> resFiles, String zipFilePath) {
        try {
            return ZipUtils.unzipFiles(resFiles, zipFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
