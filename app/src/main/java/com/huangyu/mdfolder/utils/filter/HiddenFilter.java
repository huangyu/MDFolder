package com.huangyu.mdfolder.utils.filter;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by huangyu on 2017-5-24.
 */
public class HiddenFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
        return !name.matches("^\\.+[^\\.].+");
    }

}
