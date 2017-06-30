package com.huangyu.mdfolder.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

/**
 * Created by huangyu on 2017/6/29.
 */

public class MediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {

    /**
     * 扫描对象
     */
    private MediaScannerConnection mediaScanConn = null;
    private int scanTimes = 0;
    /**
     * 文件路径集合
     **/
    private String[] filePaths;
    /**
     * 文件MimeType集合
     **/
    private String[] mimeTypes;

    public MediaScanner(Context context) {
        mediaScanConn = new MediaScannerConnection(context, this);
    }

    /**
     * 扫描文件
     *
     * @param filePaths
     * @param mimeTypes
     */
    public void scanFiles(String[] filePaths, String[] mimeTypes) {
        this.filePaths = filePaths;
        this.mimeTypes = mimeTypes;
        mediaScanConn.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        for (int i = 0; i < filePaths.length; i++) {
            mediaScanConn.scanFile(filePaths[i], mimeTypes[i]);
        }
        filePaths = null;
        mimeTypes = null;
    }

    /**
     * 扫描完成
     *
     * @param path
     * @param uri
     */
    @Override
    public void onScanCompleted(String path, Uri uri) {
        scanTimes++;
        if (scanTimes == filePaths.length) {
            mediaScanConn.disconnect();
            scanTimes = 0;
        }
    }

}
