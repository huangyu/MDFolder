package com.huangyu.mdfolder.utils.comparator;

import com.huangyu.mdfolder.bean.FileItem;

import java.util.Comparator;

/**
 * Created by huangyu on 2017-5-24.
 */
public class SizeComparator implements Comparator<FileItem> {

    public int compare(FileItem file1, FileItem file2) {
        long size1 = file1.getSize();
        long size2 = file2.getSize();
        long diff = size1 - size2;
        if (file1.isDirectory() && !file2.isDirectory()) {
            return -1;
        } else if (!file1.isDirectory() && file2.isDirectory()) {
            return 1;
        } else if (file1.isDirectory() && file2.isDirectory()) {
            return 0;
        } else {
            if (diff < 0) {
                return -1;
            } else if (diff > 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }

}
