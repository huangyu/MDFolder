package com.huangyu.mdfolder.utils.comparator;

import com.huangyu.mdfolder.bean.FileItem;

import java.util.Comparator;

/**
 * Created by huangyu on 2017-5-24.
 */
public class TypeComparator implements Comparator<FileItem> {

    public int compare(FileItem file1, FileItem file2) {
        if (file1.isDirectory() && !file2.isDirectory()) {
            return -1;
        } else if (file1.isDirectory() && file2.isDirectory()) {
            return file1.getName().compareToIgnoreCase(file2.getName());
        } else if (!file1.isDirectory() && !file2.isDirectory()) {
            return file1.getName().compareToIgnoreCase(file2.getName());
        } else if (!file1.isDirectory() && file2.isDirectory()) {
            return 1;
        } else {
            return 0;
        }
    }

}
