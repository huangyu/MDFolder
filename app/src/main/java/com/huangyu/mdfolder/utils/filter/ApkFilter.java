package com.huangyu.mdfolder.utils.filter;

import android.text.TextUtils;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by huangyu on 2017-6-9.
 */
public class ApkFilter implements FilenameFilter {

    private String mSearchStr;

    public ApkFilter(String searchStr) {
        this.mSearchStr = searchStr;
    }

    @Override
    public boolean accept(File dir, String name) {
        if (TextUtils.isEmpty(mSearchStr)) {
            return !name.startsWith(".") && name.endsWith(".apk");
        }
        return name.contains(mSearchStr) && !name.startsWith(".") && name.endsWith(".apk");
    }
}
