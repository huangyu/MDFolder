package com.huangyu.mdfolder.mvp.model;

import android.os.Environment;

import com.huangyu.library.mvp.IBaseModel;
import com.huangyu.library.util.FileUtils;
import com.huangyu.mdfolder.utils.SDCardUtils;
import com.huangyu.mdfolder.utils.comparator.AlphabetComparator;
import com.huangyu.mdfolder.utils.comparator.TimeComparator;
import com.huangyu.mdfolder.utils.comparator.TypeComparator;
import com.huangyu.mdfolder.utils.filter.HiddenFilter;
import com.huangyu.mdfolder.utils.filter.SearchFilter;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Created by huangyu on 2017-5-24.
 */
public class FileListModel implements IBaseModel {

    public FileListModel() {

    }

    public List<File> getFileList(String path) {
        return FileUtils.listFilesInDirWithFilter(path, new HiddenFilter(), false);
    }

    public List<File> getFileList(String path, String searchStr) {
        return FileUtils.listFilesInDirWithFilter(path, new SearchFilter(searchStr), false);
    }

    public String getRootPath() {
        return Environment.getRootDirectory().getPath();
    }

    public String getSDCardPath() {
        return SDCardUtils.getSDCardPath();
    }

    /**
     * 按字母排序
     */
    public List<File> orderByAlphabet(List<File> fileList) {
        Collections.sort(fileList, new AlphabetComparator());
        return fileList;
    }

    /**
     * 按时间排序
     */
    public List<File> orderByTime(List<File> fileList) {
        Collections.sort(fileList, new TimeComparator());
        return fileList;
    }

    /**
     * 按类型排序
     */
    public List<File> orderByType(List<File> fileList) {
        Collections.sort(fileList, new TypeComparator());
        return fileList;
    }

}
