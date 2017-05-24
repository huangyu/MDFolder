package com.huangyu.mdfolder.utils.comparator;

import java.io.File;
import java.util.Comparator;

/**
 * Created by huangyu on 2017-5-24.
 */
public class TimeComparator implements Comparator<File> {

    public int compare(File file1, File file2) {
        if (file1.lastModified() < file2.lastModified()) {
            return -1;
        } else if (file1.lastModified() > file2.lastModified()) {
            return 1;
        } else {
            return 0;
        }
    }

}
