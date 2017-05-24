package com.huangyu.mdfolder.mvp.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.huangyu.library.mvp.IBaseModel;
import com.huangyu.library.util.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by huangyu on 2017-5-24.
 */
public class FileModel implements IBaseModel {

    private Map<String, String> mimeTypeMap;

    public FileModel() {
        mimeTypeMap = new HashMap<>();
        mimeTypeMap.put(
                ".3gp", "video/3gpp"
        );
        mimeTypeMap.put(
                ".apk", "application/vnd.android.package-archive"
        );
        mimeTypeMap.put(
                ".asf", "video/x-ms-asf"
        );
        mimeTypeMap.put(
                ".avi", "video/x-msvideo"
        );
        mimeTypeMap.put(
                ".bin", "application/octet-stream"
        );
        mimeTypeMap.put(
                ".bmp", "image/bmp"
        );
        mimeTypeMap.put(
                ".c", "text/plain"
        );
        mimeTypeMap.put(
                ".class", "application/octet-stream"
        );
        mimeTypeMap.put(
                ".conf", "text/plain"
        );
        mimeTypeMap.put(
                ".cpp", "text/plain"
        );
        mimeTypeMap.put(
                ".doc", "application/msword"
        );
        mimeTypeMap.put(
                ".exe", "application/octet-stream"
        );
        mimeTypeMap.put(
                ".gif", "image/gif"
        );
        mimeTypeMap.put(
                ".gtar", "application/x-gtar"
        );
        mimeTypeMap.put(
                ".gz", "application/x-gzip"
        );
        mimeTypeMap.put(
                ".h", "text/plain"
        );
        mimeTypeMap.put(
                ".htm", "text/html"
        );
        mimeTypeMap.put(
                ".html", "text/html"
        );
        mimeTypeMap.put(
                ".jar", "application/java-archive"
        );
        mimeTypeMap.put(
                ".java", "text/plain"
        );
        mimeTypeMap.put(
                ".jpeg", "image/jpeg"
        );
        mimeTypeMap.put(
                ".jpg", "image/jpeg"
        );
        mimeTypeMap.put(
                ".js", "application/x-javascript"
        );
        mimeTypeMap.put(
                ".log", "text/plain"
        );
        mimeTypeMap.put(
                ".m3u", "audio/x-mpegurl"
        );
        mimeTypeMap.put(
                ".m4a", "audio/mp4a-latm"
        );
        mimeTypeMap.put(
                ".m4b", "audio/mp4a-latm"
        );
        mimeTypeMap.put(
                ".m4p", "audio/mp4a-latm"
        );
        mimeTypeMap.put(
                ".m4u", "video/vnd.mpegurl"
        );
        mimeTypeMap.put(
                ".m4v", "video/x-m4v"
        );
        mimeTypeMap.put(
                ".mov", "video/quicktime"
        );
        mimeTypeMap.put(
                ".mp2", "audio/x-mpeg"
        );
        mimeTypeMap.put(
                ".mp3", "audio/x-mpeg"
        );
        mimeTypeMap.put(
                ".mp4", "video/mp4"
        );
        mimeTypeMap.put(
                ".mpc", "application/vnd.mpohun.certificate"
        );
        mimeTypeMap.put(
                ".mpe", "video/mpeg"
        );
        mimeTypeMap.put(
                ".mpeg", "video/mpeg"
        );
        mimeTypeMap.put(
                ".mpg", "video/mpeg"
        );
        mimeTypeMap.put(
                ".mpg4", "video/mp4"
        );
        mimeTypeMap.put(
                ".mpga", "audio/mpeg"
        );
        mimeTypeMap.put(
                ".msg", "application/vnd.ms-outlook"
        );
        mimeTypeMap.put(
                ".ogg", "audio/ogg"
        );
        mimeTypeMap.put(
                ".pdf", "application/pdf"
        );
        mimeTypeMap.put(
                ".png", "image/png"
        );
        mimeTypeMap.put(
                ".pps", "application/vnd.ms-powerpoint"
        );
        mimeTypeMap.put(
                ".ppt", "application/vnd.ms-powerpoint"
        );
        mimeTypeMap.put(
                ".prop", "text/plain"
        );
        mimeTypeMap.put(
                ".rar", "application/x-rar-compressed"
        );
        mimeTypeMap.put(
                ".rc", "text/plain"
        );
        mimeTypeMap.put(
                ".rmvb", "audio/x-pn-realaudio"
        );
        mimeTypeMap.put(
                ".rtf", "application/rtf"
        );
        mimeTypeMap.put(
                ".sh", "text/plain"
        );
        mimeTypeMap.put(
                ".tar", "application/x-tar"
        );
        mimeTypeMap.put(
                ".tgz", "application/x-compressed"
        );
        mimeTypeMap.put(
                ".txt", "text/plain"
        );
        mimeTypeMap.put(
                ".wav", "audio/x-wav"
        );
        mimeTypeMap.put(
                ".wma", "audio/x-ms-wma"
        );
        mimeTypeMap.put(
                ".wmv", "audio/x-ms-wmv"
        );
        mimeTypeMap.put(
                ".wps", "application/vnd.ms-works"
        );
        mimeTypeMap.put(
                ".xml", "text/plain"
        );
        mimeTypeMap.put(
                ".z", "application/x-compress"
        );
        mimeTypeMap.put(
                ".zip", "application/zip"
        );
        mimeTypeMap.put(
                "", "*/*"
        );
    }

    /**
     * 打开文件
     *
     * @param file
     */
    public void openFile(Context context, File file) {
        if (file != null && !file.isDirectory() && file.exists()) {
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
            context.startActivity(intent);
        }
    }

    private String getMIMEType(File file) {
        return mimeTypeMap.get(FileUtils.getFileExtension(file));
    }

}
