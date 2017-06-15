package com.huangyu.mdfolder.utils.comparator;

import java.io.File;
import java.util.Comparator;

/**
 * Created by huangyu on 2017-5-24.
 */
public class TypeComparator implements Comparator<File> {


    public TypeComparator() {
    }

    public int compare(File file1, File file2) {
        if (file1.isDirectory() && !file2.isDirectory()) {
            return -1;
        } else if (file1.isDirectory() && file2.isDirectory() || !file1.isDirectory() && !file2.isDirectory()) {
            return file1.getName().compareToIgnoreCase(file2.getName());
        } else if (!file1.isDirectory() && file2.isDirectory()) {
            return 1;
        } else {
            return 0;
        }
    }

}
