package com.huangyu.mdfolder.mvp.model;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.huangyu.library.app.BaseApplication;
import com.huangyu.library.mvp.IBaseModel;
import com.huangyu.library.util.FileUtils;
import com.huangyu.mdfolder.app.Constants;
import com.huangyu.mdfolder.bean.FileItem;
import com.huangyu.mdfolder.utils.CompressUtils;
import com.huangyu.mdfolder.utils.SDCardUtils;
import com.huangyu.mdfolder.utils.SPUtils;
import com.huangyu.mdfolder.utils.comparator.AlphabetComparator;
import com.huangyu.mdfolder.utils.comparator.SizeComparator;
import com.huangyu.mdfolder.utils.comparator.TimeComparator;
import com.huangyu.mdfolder.utils.comparator.TypeComparator;
import com.huangyu.mdfolder.utils.filter.SearchFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

//    public ArrayList<File> getAppsFileList(String path, String searchStr) {
//        return FileUtils.listFilesInDirWithFilter(path, new ApkFilter(searchStr), true);
//    }
//
//    public ArrayList<File> getCompressFileList(String path, String searchStr) {
//        return FileUtils.listFilesInDirWithFilter(path, new CompressFilter(searchStr), true);
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
                        fileItem.setIcon(icon);

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

    public ArrayList<FileItem> getPhotoAlbumList(String searchStr, ContentResolver contentResolver) {
        String[] STORE_IMAGES = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_MODIFIED
        };
        Cursor cursor = MediaStore.Images.Media.query(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES, null, MediaStore.Images.Media.DISPLAY_NAME + " asc");
        Map<String, FileItem> albumFolderMap = new LinkedHashMap<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int imageId = cursor.getInt(0);
                String filePath = cursor.getString(1);
                String fileName = cursor.getString(2);
                long addTime = cursor.getLong(3);

                int bucketId = cursor.getInt(4);
                String bucketName = cursor.getString(5);
                String fileLength = cursor.getString(6);
                String date = cursor.getString(7);

                String fileRealName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
                if (FileUtils.isFileExists(filePath)) {
                    FileItem fileItem = new FileItem();
                    fileItem.setPath(filePath);

                    FileItem albumFolder = albumFolderMap.get(bucketName);
                    if (albumFolder != null) {
                        if (TextUtils.isEmpty(searchStr) || fileRealName.contains(searchStr)) {
                            albumFolder.addPhoto(fileItem);
                        }
                    } else {
                        albumFolder = new FileItem();
                        albumFolder.setName(bucketName);
                        albumFolder.setType(Constants.FileType.IMAGE);
                        if (TextUtils.isEmpty(albumFolder.getPath())) {
                            albumFolder.setPath(filePath);
                        }
                        if (TextUtils.isEmpty(searchStr) || fileRealName.contains(searchStr)) {
                            albumFolder.addPhoto(fileItem);
                            albumFolderMap.put(bucketName, albumFolder);
                        }
                    }
                }
            }
            cursor.close();
        }
        ArrayList<FileItem> albumFolders = new ArrayList<>();

        for (Map.Entry<String, FileItem> folderEntry : albumFolderMap.entrySet()) {
            FileItem albumFolder = folderEntry.getValue();
            albumFolders.add(albumFolder);
        }
        return albumFolders;
    }

    public ArrayList<FileItem> getPhotoList(String searchStr, FileItem albumFolder, ContentResolver contentResolver) {
        String[] STORE_IMAGES = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_MODIFIED
        };
        Cursor cursor = MediaStore.Images.Media.query(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES, MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " = ? "
                , new String[]{albumFolder.getName()}, null);
        ArrayList<FileItem> fileItemList = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int imageId = cursor.getInt(0);
                String filePath = cursor.getString(1);
                String fileName = cursor.getString(2);
                long addTime = cursor.getLong(3);
                int bucketId = cursor.getInt(4);
                String bucketName = cursor.getString(5);
                String fileLength = cursor.getString(6);
                String date = cursor.getString(7);

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
                        fileItemList.add(fileItem);
                    }
                }
            }
            cursor.close();
        }
        return fileItemList;
    }

//    public ArrayList<FileItem> getAudioAlbumList(String searchStr, ContentResolver contentResolver) {
//        String[] STORE_AUDIOS = {
//                MediaStore.Audio.Media._ID,
//                MediaStore.Audio.Media.DATA,
//                MediaStore.Audio.Media.DISPLAY_NAME,
//                MediaStore.Audio.Media.DATE_ADDED,
//                MediaStore.Audio.Media.ALBUM_ID,
//                MediaStore.Audio.Media.ALBUM,
//                MediaStore.Audio.Media.SIZE,
//                MediaStore.Audio.Media.DATE_MODIFIED
//        };
//        Cursor cursor = MediaStore.Images.Media.query(
//                contentResolver,
//                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, STORE_AUDIOS, null, MediaStore.Audio.Media.DISPLAY_NAME + " asc");
//        Map<String, FileItem> albumFolderMap = new LinkedHashMap<>();
//
//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//                int imageId = cursor.getInt(0);
//                String filePath = cursor.getString(1);
//                String fileName = cursor.getString(2);
//                long addTime = cursor.getLong(3);
//
//                int albumId = cursor.getInt(4);
//                String albumName = cursor.getString(5);
//                String fileLength = cursor.getString(6);
//                String date = cursor.getString(7);
//
//                String fileRealName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
//                if (FileUtils.isFileExists(filePath)) {
//                    FileItem fileItem = new FileItem();
//                    fileItem.setPath(filePath);
//                    FileItem albumFolder = albumFolderMap.get(albumName);
//                    if (albumFolder != null) {
//                        if (TextUtils.isEmpty(searchStr) || fileRealName.contains(searchStr)) {
//                            albumFolder.addPhoto(fileItem);
//                        }
//                    } else {
//                        albumFolder = new FileItem();
//                        albumFolder.setName(albumName);
//                        albumFolder.setType(Constants.FileType.AUDIO);
//
//                        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
//                        Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
//                        Bitmap bitmap = null;
//                        try {
//                            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, albumArtUri);
//                            bitmap = ImageUtils.decodeSampledBitmapFromUri(albumArtUri, 160, 160);
//                        } catch (IOException e) {
//                        }
//                        albumFolder.setIcon(ImageUtils.bitmap2Drawable(BaseApplication.getInstance().getResources(), bitmap));
//                        if (TextUtils.isEmpty(albumFolder.getPath())) {
//                            albumFolder.setPath(filePath);
//                        }
//                        if (TextUtils.isEmpty(searchStr) || fileRealName.contains(searchStr)) {
//                            albumFolder.addPhoto(fileItem);
//                            albumFolderMap.put(albumName, albumFolder);
//                        }
//                    }
//                }
//            }
//            cursor.close();
//        }
//        ArrayList<FileItem> albumFolders = new ArrayList<>();
//
//        for (Map.Entry<String, FileItem> folderEntry : albumFolderMap.entrySet()) {
//            FileItem albumFolder = folderEntry.getValue();
//            albumFolders.add(albumFolder);
//        }
//        return albumFolders;
//    }

    public ArrayList<FileItem> getAudioList(String searchStr, ContentResolver contentResolver) {
        String[] projection = new String[]{MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.SIZE,
                MediaStore.Audio.AudioColumns.DATE_MODIFIED};

        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);

        if (cursor != null) {
            ArrayList<FileItem> audioList = new ArrayList<>();
            MediaMetadataRetriever mmr;
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

                    mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(filePath);
                    byte[] data = mmr.getEmbeddedPicture();
                    if (data != null) {
                        fileItem.setBytes(data);
                    } else {
                        fileItem.setBytes(null);
                    }
                    mmr.release();
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

                    PackageInfo packageInfo = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
                    if (packageInfo != null) {
                        ApplicationInfo appInfo = packageInfo.applicationInfo;
                        appInfo.sourceDir = filePath;
                        appInfo.publicSourceDir = filePath;
                        Drawable icon = appInfo.loadIcon(pm);
                        fileItem.setIcon(icon);
                    }

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

    public ArrayList<FileItem> getInstalledList(String searchStr) {
        ArrayList<FileItem> appsList = new ArrayList<>();
        ArrayList<FileItem> systemList = new ArrayList<>();
        ArrayList<FileItem> nSystemList = new ArrayList<>();
        PackageManager packageManager = BaseApplication.getInstance().getPackageManager();
        List<PackageInfo> packages = packageManager.getInstalledPackages(0);
        FileItem fileItem;
        try {
            for (PackageInfo packageInfo : packages) {
                fileItem = new FileItem();
                String packageName = packageInfo.packageName;
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                String fileRealName;
                if (applicationInfo != null) {
                    fileRealName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                    fileItem.setName(fileRealName);
                    fileItem.setPath(packageInfo.applicationInfo.publicSourceDir);
                } else {
                    fileRealName = packageInfo.packageName;
                    fileItem.setName(fileRealName);
                    fileItem.setPath("");
                }
                fileItem.setDate(String.valueOf(packageInfo.lastUpdateTime / 1000));
                fileItem.setIcon(packageManager.getApplicationIcon(packageName));
                fileItem.setType(Constants.FileType.INSTALLED);
                fileItem.setParent(null);
                fileItem.setIsDirectory(false);
                fileItem.setPackageName(packageName);
                fileItem.setIsShow(true);

                if (TextUtils.isEmpty(searchStr) || fileRealName.contains(searchStr)) {
                    if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        nSystemList.add(fileItem);
                    } else {
                        systemList.add(fileItem);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }

        appsList.addAll(nSystemList);
        if (SPUtils.isShowAllApps()) {
            appsList.addAll(systemList);
        }
        return appsList;
    }

    public ArrayList<FileItem> getCompressList(String searchStr, ContentResolver
            contentResolver) {
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ArrayList<FileItem> getExternalList(Uri uri, String searchStr, ContentResolver
            contentResolver) {
        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        if (cursor != null) {
            ArrayList<FileItem> externalList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String fileName = cursor.getString(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME));
                long date = cursor.getLong(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_LAST_MODIFIED));
                long fileLength = cursor.getLong(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_SIZE));
                Uri fileUri = DocumentsContract.buildChildDocumentsUriUsingTree(uri, cursor.getString(0));

                FileItem fileItem = new FileItem();
                fileItem.setName(fileName);
                fileItem.setPath(fileUri.toString());
                fileItem.setSize(String.valueOf(fileLength));
                fileItem.setDate(String.valueOf(date));
                fileItem.setParent(null);
                fileItem.setIsDirectory(false);
                fileItem.setType(Constants.FileType.FILE);
                fileItem.setIsShow(true);
                externalList.add(fileItem);
            }
            cursor.close();
            return externalList;
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
