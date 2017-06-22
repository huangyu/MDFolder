package com.huangyu.mdfolder.utils.comparator;

import com.huangyu.mdfolder.bean.FileItem;
import com.huangyu.mdfolder.utils.DateUtils;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by huangyu on 2017-5-24.
 */
public class TimeComparator implements Comparator<FileItem> {

    public int compare(FileItem file1, FileItem file2) {
        Date date1 = DateUtils.stringToDate(file1.getDate());
        Date date2 = DateUtils.stringToDate(file2.getDate());
        long diff = date1.getTime() - date2.getTime();
        if (diff < 0) {
            return -1;
        } else if (diff > 0) {
            return 1;
        } else {
            return 0;
        }
    }

}
