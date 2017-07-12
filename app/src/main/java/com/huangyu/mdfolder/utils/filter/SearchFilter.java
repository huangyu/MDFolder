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
    public boolean accept(File file, String name) {
        boolean hiddenResult = !name.matches("^\\.+[^\\.].+");
        if (TextUtils.isEmpty(mSearchStr)) {
            return isShowHidden || hiddenResult;
        } else {
            String remark = SPUtils.getFileRemark(file.getPath() + File.separator + name);
            boolean nameResult = StringUtils.containsIgnoreCase(name, mSearchStr);
            boolean remarkResult = StringUtils.containsIgnoreCase(remark, mSearchStr);
            if (isShowHidden) {
                if (TextUtils.isEmpty(remark)) {
                    return nameResult;
                } else {
                    return nameResult || remarkResult;
                }
            } else {
                if (TextUtils.isEmpty(remark)) {
                    return nameResult && hiddenResult;
                } else {
                    return (nameResult || remarkResult) && hiddenResult;
                }
            }
        }
    }


}
