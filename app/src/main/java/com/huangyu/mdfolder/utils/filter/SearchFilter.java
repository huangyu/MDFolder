package com.huangyu.mdfolder.utils.filter;

import android.text.TextUtils;

import com.huangyu.mdfolder.utils.SPUtils;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by huangyu on 2017-5-24.
 */
public class SearchFilter implements FilenameFilter {

    private String mSearchStr;

    public SearchFilter(String searchStr) {
        this.mSearchStr = searchStr;
    }

    @Override
    public boolean accept(File dir, String name) {
        if (TextUtils.isEmpty(mSearchStr)) {
            return SPUtils.isShowHiddenFiles() || !name.startsWith(".");
        } else {
            if (SPUtils.isShowHiddenFiles()) {
                return containsIgnoreCase(name, mSearchStr);
            } else {
                return containsIgnoreCase(name, mSearchStr) && !name.startsWith(".");
            }
        }
    }

    /**
     * 忽略大小写匹配
     *
     * @param str
     * @param searchStr
     * @return
     */
    private static boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }

        final int length = searchStr.length();
        if (length == 0) {
            return true;
        }

        for (int i = str.length() - length; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, 0, length)) {
                return true;
            }
        }
        return false;
    }

}
