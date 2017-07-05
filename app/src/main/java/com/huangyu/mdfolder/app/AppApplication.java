package com.huangyu.mdfolder.app;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.huangyu.library.app.BaseApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangyu on 2017-5-23.
 */
public class AppApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
//        scanAllFile();
    }

    /**
     * 每次全盘扫描一次sd卡文件，防止部分第三方发送的文件信息件没能更快被写入媒体数据库
     */
    public static void scanAllFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> listPaths = new ArrayList<>();
                    getAllPaths(Environment.getExternalStorageDirectory(), listPaths);
                    MediaScannerConnection.scanFile(getInstance(), listPaths.toArray(new String[]{}), null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.e("tag", "scan: " + path);
                        }
                    });
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }).start();
    }

    public static void getAllPaths(File root, List<String> listPaths) {
        File files[] = root.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory() && !f.getPath().contains("/Android/data")) {
                    getAllPaths(f, listPaths);
                } else {
                    listPaths.add(f.getAbsolutePath());
                }
            }
        }
    }

}
