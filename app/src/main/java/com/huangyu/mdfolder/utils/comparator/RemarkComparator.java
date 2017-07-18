package com.huangyu.mdfolder.utils.comparator;

import android.text.TextUtils;

import com.huangyu.mdfolder.bean.FileItem;

import java.util.Comparator;

/**
 * Created by huangyu on 2017-5-24.
 */
public class RemarkComparator implements Comparator<FileItem> {


    public RemarkComparator() {
    }

    public int compare(FileItem file1, FileItem file2) {
        if (TextUtils.isEmpty(file1.getRemark()) && TextUtils.isEmpty(file2.getRemark())) {
            return 0;
        } else if (!TextUtils.isEmpty(file1.getRemark()) && TextUtils.isEmpty(file2.getRemark())) {
            return -1;
        } else if (TextUtils.isEmpty(file1.getRemark()) && !TextUtils.isEmpty(file2.getRemark())) {
            return 1;
        }
        return file1.getRemark().compareToIgnoreCase(file2.getRemark());
    }

}
