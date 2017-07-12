package com.huangyu.mdfolder.utils.filter;

import android.text.TextUtils;

import com.huangyu.mdfolder.utils.SPUtils;
import com.huangyu.mdfolder.utils.StringUtils;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by huangyu on 2017-5-24.
 */
public class SearchFilter implements FilenameFilter {

    private String mSearchStr;
    private boolean isShowHidden;

    public SearchFilter(String searchStr) {
        this.mSearchStr = searchStr;
        this.isShowHidden = SPUtils.isShowHiddenFiles();
    }

    @Override
    public boolean accept(File dir, String name) {
        boolean hiddenResult = !name.matches("^\\.+[^\\.].+");
        if (TextUtils.isEmpty(mSearchStr)) {
            return isShowHidden || hiddenResult;
        } else {
            String remark = SPUtils.getFileRemark(dir.getPath());
            boolean nameResult = StringUtils.containsIgnoreCase(name, mSearchStr);
            boolean remarkResult = TextUtils.isEmpty(remark) || StringUtils.containsIgnoreCase(remark, mSearchStr);
            if (isShowHidden) {
                return nameResult || remarkResult;
            } else {
                return nameResult || remarkResult || hiddenResult;
            }
        }
    }



}
