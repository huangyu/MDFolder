package com.huangyu.mdfolder.utils;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;

import java.util.List;

import static com.huangyu.library.app.BaseApplication.getInstance;

/**
 * Created by huangyu on 2017/7/9.
 */

public class ScanTask extends AsyncTask<Void, Integer, Void> {

    private List<String> mPathsList;
    private ScanCallBack mScanCallBack;
    private volatile int count;

    public ScanTask(List<String> pathsList, ScanCallBack scanCallBack) {
        this.mPathsList = pathsList;
        this.mScanCallBack = scanCallBack;
    }

    @Override
    protected void onPreExecute() {
        if (mPathsList != null && mPathsList.size() > 0) {
            mScanCallBack.onPreExecute(mPathsList.size());
        }
        count = 0;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (mPathsList != null && mPathsList.size() > 0) {
            MediaScannerConnection.scanFile(getInstance(), mPathsList.toArray(new String[]{}), null, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                    publishProgress(count++);
                }
            });
            while (count != mPathsList.size()) {

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
        mScanCallBack.onPostExecute();
    }

    public interface ScanCallBack {
        void onPreExecute(int total);

        void onProgressUpdate(Integer progress, int total);

        void onPostExecute();
    }

}
