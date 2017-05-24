package com.huangyu.mdfolder.utils.comparator;

import java.io.File;
import java.util.Comparator;

/**
 * Created by huangyu on 2017-5-24.
 */
public class TypeComparator implements Comparator<File> {

    public int compare(File file1, File file2) {
        if (!file1.isDirectory() && file2.isDirectory()) {
            return 1;
        } else if (file1.isDirectory() && !file2.isDirectory()) {
            return -1;
        } else {
            return 0;
        }
    }

}
