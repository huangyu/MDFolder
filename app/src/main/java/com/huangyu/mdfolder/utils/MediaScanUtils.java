package com.huangyu.mdfolder.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.util.Locale;

/**
 * Created by huangyu on 2017/7/5.
 */

public class MediaScanUtils {


    /**
     * @param context
     * @param paths     File paths to scan
     * @param mimeTypes mimeTypes in array;it could be null;then
     * @param callback
     */
    public static void scanFiles(Context context, String[] paths, String[] mimeTypes, MediaScannerConnection.OnScanCompletedListener callback) {
        if (null != paths && paths.length != 0) {
            MediaScannerConnection.scanFile(context, paths, mimeTypes, callback);
        }
    }

    public static void scanFiles(Context context, String[] paths, String[] mimeTypes) {
        scanFiles(context, paths, mimeTypes, null);
    }

    public static void scanFiles(Context context, String[] paths) {
        scanFiles(context, paths, null);
    }

    public static int removeImageFromLib(Context context, String filePath) {
        ContentResolver resolver = context.getContentResolver();
        return resolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{filePath});
    }

    public static int removeAudioFromLib(Context context, String filePath) {
        return context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Audio.Media.DATA + "=?", new String[]{filePath});
    }

    public static int removeVideoFromLib(Context context, String filePath) {
        return context.getContentResolver().delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Video.Media.DATA + "=?", new String[]{filePath});
    }

    public static int removeMediaFromLib(Context context, String filePath) {
        String mimeType = MimeTypeUtils.getMIMEType(filePath);
        int affectedRows = 0;
        if (null != mimeType) {
            mimeType = mimeType.toLowerCase(Locale.US);
            if (isImage(mimeType)) {
                affectedRows = removeImageFromLib(context, filePath);
            } else if (isAudio(mimeType)) {
                affectedRows = removeAudioFromLib(context, filePath);
            } else if (isVideo(mimeType)) {
                affectedRows = removeVideoFromLib(context, filePath);
            } else {
                affectedRows = deleteMediaFile(context, filePath);
            }
        }
        return affectedRows;
    }

    public static boolean isAudio(String mimeType) {
        return mimeType.startsWith("audio");
    }

    public static boolean isImage(String mimeType) {
        return mimeType.startsWith("image");
    }

    public static boolean isVideo(String mimeType) {
        return mimeType.startsWith("video");
    }


    public static boolean isMediaFile(String filePath) {
        String mimeType = MimeTypeUtils.getMIMEType(filePath);
        return isMediaType(mimeType);
    }

    public static boolean isMediaType(String mimeType) {
        boolean isMedia = false;
        if (!TextUtils.isEmpty(mimeType)) {
            mimeType = mimeType.toLowerCase(Locale.US);
            isMedia = isImage(mimeType) || isAudio(mimeType) || isVideo(mimeType);
        }
        return isMedia;
    }

    public static int deleteMediaFile(Context context, String filePath) {
        return context.getContentResolver().delete(MediaStore.Files.getContentUri("external"), MediaStore.Files.FileColumns.DATA + " =? ", new String[]{filePath});
    }

    public static int renameMediaFile(Context context, String srcPath, String destPath) {
        removeMediaFromLib(context, srcPath);
        scanFiles(context, new String[]{destPath});
        return 0;
    }

}

