package com.huangyu.mdfolder.app;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.huangyu.library.app.BaseApplication;
import com.huangyu.library.util.FileUtils;
import com.huangyu.library.util.SPUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangyu on 2017-5-23.
 */
public class AppApplication extends BaseApplication {

    private SPUtils spUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        spUtils = new SPUtils(Constants.APP_NAME);
//        scanAllFile();
    }

    public SPUtils getSPUtils() {
        return spUtils;
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
                if (f.isDirectory()) {
                    if (needScan(f)) {
                        getAllPaths(f, listPaths);
                    }
                } else {
                    if (!f.isHidden() || FileUtils.getFileLength(f) > 30 * 1024) {
                        listPaths.add(f.getAbsolutePath());
                    }
                }
            }
        }
    }

    private static boolean needScan(File dir) {
        for (String fileName : dir.list()) {
            // contains .nomedia file or is hidden file or is cache file
            if (fileName.equals(".nomedia") || dir.isHidden() || fileName.contains("cache") || fileName.contains("Cache")) {
                return false;
            }
        }
        return true;
    }

}
