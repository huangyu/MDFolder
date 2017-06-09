package com.huangyu.mdfolder.utils.filter;

import android.text.TextUtils;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by huangyu on 2017-6-9.
 */
public class PhotoFilter implements FilenameFilter {

    private String mSearchStr;
    private String[] suffixArray = {".bmp", ".jpg", ".ico", ".gif", ".svg", ".png"};

    public PhotoFilter(String searchStr) {
        this.mSearchStr = searchStr;
    }

    @Override
    public boolean accept(File dir, String name) {
        if (TextUtils.isEmpty(mSearchStr)) {
            return !name.startsWith(".") && isInArray(name);
        }
        return name.contains(mSearchStr) && !name.startsWith(".") && isInArray(name);
    }

    private boolean isInArray(String name) {
        for (String musicSuffix : suffixArray) {
            if (name.endsWith(musicSuffix)) {
                return true;
            }
        }
        return false;
    }

}
