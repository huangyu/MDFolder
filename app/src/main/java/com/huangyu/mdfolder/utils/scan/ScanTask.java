package com.huangyu.mdfolder.utils.scan;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.huangyu.library.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.huangyu.library.app.BaseApplication.getInstance;

/**
 * Created by huangyu on 2017/7/9.
 */

public class ScanTask extends AsyncTask<Void, Integer, Void> {

    private List<String> mPathsList;
    private ScanCallBack mScanCallBack;
    private int mFileSize;
    private volatile int mCount;

    public ScanTask(ScanCallBack scanCallBack, int fileSize) {
        this.mScanCallBack = scanCallBack;
        this.mFileSize = fileSize;
        mPathsList = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        mScanCallBack.onDialogShow();
        mCount = 0;
    }

    @Override
    protected Void doInBackground(Void... params) {
        getAllPaths(Environment.getExternalStorageDirectory(), mPathsList, mFileSize);
        if (mPathsList != null && mPathsList.size() > 0) {
            mScanCallBack.onProgressStart(mPathsList.size());
        }
        if (mPathsList != null && mPathsList.size() > 0) {
            MediaScannerConnection.scanFile(getInstance(), mPathsList.toArray(new String[]{}), null, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                    publishProgress(mCount++);
                }
            });
            while (mCount != mPathsList.size()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {

                }
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        mScanCallBack.onProgressUpdate(progress[0], mPathsList.size());
    }

    @Override
    protected void onPostExecute(Void v) {
        mScanCallBack.onDialogDismiss();
    }

    private void getAllPaths(File root, List<String> listPaths, int fileSize) {
        File files[] = root.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    getAllPaths(f, listPaths, fileSize);
                } else {
                    if (FileUtils.getFileLength(f) > fileSize * 1024) {
                        listPaths.add(f.getAbsolutePath());
                    }
                }
            }
        }
    }

    public interface ScanCallBack {
        void onDialogShow();

        void onProgressStart(int total);

        void onProgressUpdate(Integer progress, int total);

        void onDialogDismiss();
    }

}
